package com.tekion.accounting.service;

import com.tekion.accounting.model.BankTransaction;
import com.tekion.accounting.model.DepositBatch;
import com.tekion.accounting.model.ReconciliationMatch;
import com.tekion.accounting.repository.BankTransactionRepository;
import com.tekion.accounting.repository.DepositBatchRepository;
import com.tekion.accounting.repository.ReconciliationMatchRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * AI-powered reconciliation service
 * Uses rule-based matching with Llama-generated explanations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AIReconciliationService {
    
    private final BankTransactionRepository bankTransactionRepository;
    private final DepositBatchRepository depositBatchRepository;
    private final ReconciliationMatchRepository reconciliationMatchRepository;
    private final OllamaService ollamaService;
    private final ExceptionService exceptionService;
    
    @Value("${app.merchant-fee.min-percentage}")
    private double merchantFeeMinPercentage;
    
    @Value("${app.merchant-fee.max-percentage}")
    private double merchantFeeMaxPercentage;
    
    private static final String DEALERSHIP_ID = "DEALER-001";
    
    /**
     * Run AI reconciliation for all unmatched transactions and unreconciled batches
     */
    public ReconciliationResult runAIReconciliation() {
        log.info("Starting AI reconciliation for dealership: {}", DEALERSHIP_ID);
        
        ReconciliationResult result = new ReconciliationResult();
        
        // Get unmatched bank transactions (credits only - deposits)
        List<BankTransaction> unmatchedTransactions = bankTransactionRepository
                .findByDealershipIdAndStatusAndType(DEALERSHIP_ID, "UNMATCHED", "CREDIT");
        
        // Get unreconciled deposit batches
        List<DepositBatch> unreconciledBatches = depositBatchRepository
                .findByDealershipIdAndReconciled(DEALERSHIP_ID, false);
        
        log.info("Found {} unmatched transactions and {} unreconciled batches", 
                unmatchedTransactions.size(), unreconciledBatches.size());
        
        // Try to match each transaction with batches
        for (BankTransaction transaction : unmatchedTransactions) {
            boolean matched = false;
            
            // Strategy 1: Exact match
            for (DepositBatch batch : unreconciledBatches) {
                if (Math.abs(transaction.getAmount() - batch.getTotal()) < 0.01) {
                    createMatch(transaction, batch, "EXACT_MATCH", 100.0, result);
                    matched = true;
                    break;
                }
            }
            
            // Strategy 2: Merchant fee match (1.5% - 3.5% difference)
            if (!matched) {
                for (DepositBatch batch : unreconciledBatches) {
                    double difference = batch.getTotal() - transaction.getAmount();
                    double feePercentage = (difference / batch.getTotal()) * 100;
                    
                    if (feePercentage >= merchantFeeMinPercentage && 
                        feePercentage <= merchantFeeMaxPercentage) {
                        
                        createMerchantFeeMatch(transaction, batch, difference, feePercentage, result);
                        matched = true;
                        break;
                    }
                }
            }
            
            // Strategy 3: No match found - create exception
            if (!matched) {
                createUnmatchedException(transaction, result);
            }
        }
        
        // Check for unmatched batches (timing differences)
        for (DepositBatch batch : unreconciledBatches) {
            boolean hasMatch = reconciliationMatchRepository
                    .findByDepositBatchId(batch.getId()).isPresent();
            
            if (!hasMatch) {
                createTimingDifferenceException(batch, result);
            }
        }
        
        log.info("AI Reconciliation complete: {} matches, {} exceptions", 
                result.getMatchesCreated(), result.getExceptionsCreated());
        
        return result;
    }
    
    /**
     * Create exact match
     */
    private void createMatch(BankTransaction transaction, DepositBatch batch, 
                            String matchType, double confidence, ReconciliationResult result) {
        
        String explanation = ollamaService.generateMatchExplanation(
                transaction.getAmount(), batch.getTotal(), batch.getBatchNumber());
        
        ReconciliationMatch match = new ReconciliationMatch();
        match.setDealershipId(DEALERSHIP_ID);
        match.setBankTransactionId(transaction.getId());
        match.setDepositBatchId(batch.getId());
        match.setMatchType(matchType);
        match.setConfidenceScore(confidence);
        match.setAiConfidence((int) confidence);  // Set both fields for compatibility
        match.setAiSuggested(true);  // Mark as AI-suggested
        match.setAiExplanation(explanation);
        match.setStatus("SUGGESTED");
        
        reconciliationMatchRepository.save(match);
        result.incrementMatches();
        
        log.info("Created {} match: Bank ${} <-> Batch {} ${}",
                matchType, transaction.getAmount(), batch.getBatchNumber(), batch.getTotal());
    }

    /**
     * Create merchant fee match with exception
     */
    private void createMerchantFeeMatch(BankTransaction transaction, DepositBatch batch,
                                       double feeAmount, double feePercentage, ReconciliationResult result) {

        // Create the match
        String explanation = ollamaService.generateMatchExplanation(
                transaction.getAmount(), batch.getTotal(), batch.getBatchNumber());

        ReconciliationMatch match = new ReconciliationMatch();
        match.setDealershipId(DEALERSHIP_ID);
        match.setBankTransactionId(transaction.getId());
        match.setDepositBatchId(batch.getId());
        match.setMatchType("MERCHANT_FEE_MATCH");
        match.setConfidenceScore(95.0);
        match.setAiConfidence(95);  // Set both fields for compatibility
        match.setAiSuggested(true);  // Mark as AI-suggested
        match.setAiExplanation(explanation);
        match.setStatus("SUGGESTED");

        reconciliationMatchRepository.save(match);
        result.incrementMatches();

        // Create exception for the merchant fee
        String memo = ollamaService.generateMerchantFeeMemo(
                batch.getTotal(), transaction.getAmount(), feeAmount, feePercentage);

        exceptionService.createException(
                "MERCHANT_FEE",
                String.format("Merchant fee detected: %.2f%% ($%.2f) on batch %s",
                        feePercentage, feeAmount, batch.getBatchNumber()),
                feeAmount,
                transaction.getId(),
                batch.getId(),
                memo,
                "6100 - Merchant Fee Expense"
        );

        result.incrementExceptions();

        log.info("Created merchant fee match: Bank ${} <-> Batch {} ${} (Fee: ${} / {}%)",
                transaction.getAmount(), batch.getBatchNumber(), batch.getTotal(),
                feeAmount, String.format("%.2f", feePercentage));
    }

    /**
     * Create exception for unmatched transaction
     */
    private void createUnmatchedException(BankTransaction transaction, ReconciliationResult result) {

        String memo = ollamaService.generateUnmatchedMemo(
                transaction.getAmount(), transaction.getType());

        exceptionService.createException(
                "UNMATCHED_TRANSACTION",
                String.format("Unmatched bank %s: $%.2f on %s",
                        transaction.getType().toLowerCase(),
                        Math.abs(transaction.getAmount()),
                        transaction.getDate()),
                transaction.getAmount(),
                transaction.getId(),
                null,
                memo,
                "1200 - Undeposited Funds"
        );

        result.incrementExceptions();

        log.info("Created unmatched exception: Bank {} ${}",
                transaction.getType(), transaction.getAmount());
    }

    /**
     * Create exception for timing difference (batch not in bank yet)
     */
    private void createTimingDifferenceException(DepositBatch batch, ReconciliationResult result) {

        String memo = ollamaService.generateTimingDifferenceMemo(
                batch.getBatchNumber(), batch.getTotal());

        exceptionService.createException(
                "TIMING_DIFFERENCE",
                String.format("Deposit batch %s ($%.2f) not yet in bank feed",
                        batch.getBatchNumber(), batch.getTotal()),
                batch.getTotal(),
                null,
                batch.getId(),
                memo,
                "1210 - Deposits in Transit"
        );

        result.incrementExceptions();

        log.info("Created timing difference exception: Batch {} ${}",
                batch.getBatchNumber(), batch.getTotal());
    }

    /**
     * Result object to track reconciliation progress
     */
    @Data
    public static class ReconciliationResult {
        private int matchesCreated = 0;
        private int exceptionsCreated = 0;

        public void incrementMatches() {
            matchesCreated++;
        }

        public void incrementExceptions() {
            exceptionsCreated++;
        }
    }
}
