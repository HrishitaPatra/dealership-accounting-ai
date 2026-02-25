package com.tekion.accounting.controller;

import com.tekion.accounting.model.BankTransaction;
import com.tekion.accounting.service.BankTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Bank Transactions
 */
@RestController
@RequestMapping("/api/bank-transactions")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class BankTransactionController {
    
    private final BankTransactionService bankTransactionService;
    
    /**
     * Seed demo bank transactions
     * POST /api/bank-transactions/seed
     */
    @PostMapping("/seed")
    public ResponseEntity<List<BankTransaction>> seedDemoBankTransactions() {
        log.info("REST: Seeding demo bank transactions");
        List<BankTransaction> transactions = bankTransactionService.seedDemoBankTransactions();
        return ResponseEntity.status(HttpStatus.CREATED).body(transactions);
    }

    /**
     * Get all bank transactions
     * GET /api/bank-transactions
     */
    @GetMapping
    public ResponseEntity<List<BankTransaction>> getAllBankTransactions() {
        log.info("REST: Getting all bank transactions");
        List<BankTransaction> transactions = bankTransactionService.getAllBankTransactions();
        return ResponseEntity.ok(transactions);
    }
    
    /**
     * Get unmatched bank transactions
     * GET /api/bank-transactions/unmatched
     */
    @GetMapping("/unmatched")
    public ResponseEntity<List<BankTransaction>> getUnmatchedTransactions() {
        log.info("REST: Getting unmatched bank transactions");
        List<BankTransaction> transactions = bankTransactionService.getUnmatchedTransactions();
        return ResponseEntity.ok(transactions);
    }
}

