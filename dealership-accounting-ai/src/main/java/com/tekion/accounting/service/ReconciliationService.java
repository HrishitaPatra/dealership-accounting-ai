package com.tekion.accounting.service;

import com.tekion.accounting.model.BankTransaction;
import com.tekion.accounting.model.DepositBatch;
import com.tekion.accounting.model.ReconciliationMatch;
import com.tekion.accounting.repository.ReconciliationMatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for Reconciliation business logic
 * Handles matching bank transactions to deposit batches
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReconciliationService {
    
    private final ReconciliationMatchRepository reconciliationMatchRepository;
    private final BankTransactionService bankTransactionService;
    private final DepositBatchService depositBatchService;
    
    private static final String DEALERSHIP_ID = "DEALER-001";
    
    /**
     * Manually confirm a match between bank transaction and deposit batch
     */
    public ReconciliationMatch confirmMatch(String bankTransactionId, String depositBatchId, 
                                           boolean aiSuggested, Integer aiConfidence, 
                                           List<String> aiReasons) {
        log.info("Confirming match: Bank TXN {} <-> Batch {}", bankTransactionId, depositBatchId);
        
        // Validate bank transaction exists and is unmatched
        BankTransaction bankTxn = bankTransactionService.getBankTransactionById(bankTransactionId);
        if ("MATCHED".equals(bankTxn.getStatus())) {
            throw new RuntimeException("Bank transaction already matched: " + bankTxn.getTransactionId());
        }
        
        // Validate deposit batch exists and is unreconciled
        DepositBatch batch = depositBatchService.getDepositBatchById(depositBatchId);
        if (batch.getReconciled()) {
            throw new RuntimeException("Deposit batch already reconciled: " + batch.getBatchNumber());
        }
        
        // Create reconciliation match
        ReconciliationMatch match = ReconciliationMatch.builder()
            .dealershipId(DEALERSHIP_ID)
            .bankTransactionId(bankTransactionId)
            .depositBatchId(depositBatchId)
            .aiSuggested(aiSuggested)
            .aiConfidence(aiConfidence)
            .aiReasons(aiReasons != null ? aiReasons : new ArrayList<>())
            .userConfirmed(true)
            .matchedBy("DEMO-USER")
            .build();
        
        ReconciliationMatch saved = reconciliationMatchRepository.save(match);
        
        // Update statuses
        bankTransactionService.markTransactionAsMatched(bankTransactionId);
        depositBatchService.markBatchAsReconciled(depositBatchId);
        
        log.info("Match confirmed: Bank TXN {} <-> Batch {}", 
                 bankTxn.getTransactionId(), batch.getBatchNumber());
        
        return saved;
    }
    
    /**
     * Get all reconciliation matches
     */
    public List<ReconciliationMatch> getAllMatches() {
        return reconciliationMatchRepository.findByDealershipIdOrderByMatchedAtDesc(DEALERSHIP_ID);
    }
    
    /**
     * Get AI-suggested matches
     */
    public List<ReconciliationMatch> getAISuggestedMatches() {
        return reconciliationMatchRepository.findByDealershipIdAndAiSuggested(DEALERSHIP_ID, true);
    }
    
    /**
     * Get match by bank transaction ID
     */
    public ReconciliationMatch getMatchByBankTransactionId(String bankTransactionId) {
        return reconciliationMatchRepository.findByBankTransactionId(bankTransactionId)
            .orElse(null);
    }
    
    /**
     * Get match by deposit batch ID
     */
    public ReconciliationMatch getMatchByDepositBatchId(String depositBatchId) {
        return reconciliationMatchRepository.findByDepositBatchId(depositBatchId)
            .orElse(null);
    }
}

