package com.tekion.accounting.service;

import com.tekion.accounting.model.BankTransaction;
import com.tekion.accounting.repository.BankTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for BankTransaction business logic
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BankTransactionService {
    
    private final BankTransactionRepository bankTransactionRepository;
    
    private static final String DEALERSHIP_ID = "DEALER-001";

    /**
     * Seed demo bank transactions
     */
    public List<BankTransaction> seedDemoBankTransactions() {
        log.info("Seeding demo bank transactions");

        // Delete existing bank transactions to avoid duplicates
        bankTransactionRepository.deleteByDealershipId(DEALERSHIP_ID);
        log.info("Deleted existing bank transactions for dealership: {}", DEALERSHIP_ID);

        List<BankTransaction> transactions = new ArrayList<>();

        // Demo transaction 1: EXACT MATCH for BATCH-001 ($54.00)
        BankTransaction txn1 = BankTransaction.builder()
            .dealershipId(DEALERSHIP_ID)
            .transactionId("BANK-TXN-001")
            .date(LocalDate.now().minusDays(1))
            .description("Cash Deposit - Batch 001")
            .amount(54.00)  // EXACT MATCH for BATCH-001!
            .type("CREDIT")
            .status("UNMATCHED")
            .build();

        // Demo transaction 2: EXACT MATCH for BATCH-002 ($108.00)
        BankTransaction txn2 = BankTransaction.builder()
            .dealershipId(DEALERSHIP_ID)
            .transactionId("BANK-TXN-002")
            .date(LocalDate.now().minusDays(2))
            .description("Cash Deposit - Batch 002")
            .amount(108.00)  // EXACT MATCH for BATCH-002!
            .type("CREDIT")
            .status("UNMATCHED")
            .build();

        // Demo transaction 3: MERCHANT FEE MATCH (500 - 2.9% = 485.50)
        // This will match if you create a batch with $500
        BankTransaction txn3 = BankTransaction.builder()
            .dealershipId(DEALERSHIP_ID)
            .transactionId("BANK-TXN-003")
            .date(LocalDate.now().minusDays(1))
            .description("Credit Card Deposit - Merchant Services")
            .amount(485.50)
            .type("CREDIT")
            .status("UNMATCHED")
            .build();

        // Demo transaction 4: UNMATCHED (no corresponding batch)
        BankTransaction txn4 = BankTransaction.builder()
            .dealershipId(DEALERSHIP_ID)
            .transactionId("BANK-TXN-004")
            .date(LocalDate.now().minusDays(3))
            .description("Unknown Wire Transfer")
            .amount(250.00)
            .type("CREDIT")
            .status("UNMATCHED")
            .build();

        // Demo transaction 5: Debit (bank fee)
        BankTransaction txn5 = BankTransaction.builder()
            .dealershipId(DEALERSHIP_ID)
            .transactionId("BANK-TXN-005")
            .date(LocalDate.now().minusDays(1))
            .description("Monthly Service Fee")
            .amount(-25.00)
            .type("DEBIT")
            .status("UNMATCHED")
            .build();

        transactions.add(bankTransactionRepository.save(txn1));
        transactions.add(bankTransactionRepository.save(txn2));
        transactions.add(bankTransactionRepository.save(txn3));
        transactions.add(bankTransactionRepository.save(txn4));
        transactions.add(bankTransactionRepository.save(txn5));

        log.info("Seeded {} demo bank transactions (2 exact matches, 1 merchant fee, 1 unmatched, 1 debit)", transactions.size());
        
        return transactions;
    }
    
    /**
     * Get all unmatched transactions
     */
    public List<BankTransaction> getUnmatchedTransactions() {
        return bankTransactionRepository.findByDealershipIdAndStatusOrderByDateDesc(
            DEALERSHIP_ID, "UNMATCHED"
        );
    }
    
    /**
     * Get all bank transactions
     */
    public List<BankTransaction> getAllBankTransactions() {
        return bankTransactionRepository.findByDealershipId(DEALERSHIP_ID);
    }
    
    /**
     * Get bank transaction by ID
     */
    public BankTransaction getBankTransactionById(String txnId) {
        return bankTransactionRepository.findById(txnId)
            .orElseThrow(() -> new RuntimeException("Bank transaction not found: " + txnId));
    }
    
    /**
     * Mark transaction as matched
     */
    public void markTransactionAsMatched(String txnId) {
        BankTransaction txn = getBankTransactionById(txnId);
        txn.setStatus("MATCHED");
        bankTransactionRepository.save(txn);
        log.info("Marked transaction as matched: {}", txn.getTransactionId());
    }
}

