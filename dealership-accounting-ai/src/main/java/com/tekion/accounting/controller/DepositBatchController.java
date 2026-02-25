package com.tekion.accounting.controller;

import com.tekion.accounting.model.DepositBatch;
import com.tekion.accounting.service.DepositBatchService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Deposit Batches
 */
@RestController
@RequestMapping("/api/deposit-batches")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DepositBatchController {
    
    private final DepositBatchService depositBatchService;
    
    /**
     * Create deposit batch from receipts
     * POST /api/deposit-batches
     */
    @PostMapping
    public ResponseEntity<DepositBatch> createDepositBatch(@RequestBody CreateBatchRequest request) {
        log.info("REST: Creating deposit batch with {} receipts", request.getReceiptIds().size());
        DepositBatch batch = depositBatchService.createDepositBatch(request.getReceiptIds());
        return ResponseEntity.status(HttpStatus.CREATED).body(batch);
    }
    
    /**
     * Get all deposit batches
     * GET /api/deposit-batches
     */
    @GetMapping
    public ResponseEntity<List<DepositBatch>> getAllDepositBatches() {
        log.info("REST: Getting all deposit batches");
        List<DepositBatch> batches = depositBatchService.getAllDepositBatches();
        return ResponseEntity.ok(batches);
    }
    
    /**
     * Get unreconciled deposit batches
     * GET /api/deposit-batches/unreconciled
     */
    @GetMapping("/unreconciled")
    public ResponseEntity<List<DepositBatch>> getUnreconciledBatches() {
        log.info("REST: Getting unreconciled deposit batches");
        List<DepositBatch> batches = depositBatchService.getUnreconciledBatches();
        return ResponseEntity.ok(batches);
    }
    
    /**
     * Mark batch as deposited
     * PUT /api/deposit-batches/{id}/mark-deposited
     */
    @PutMapping("/{id}/mark-deposited")
    public ResponseEntity<DepositBatch> markBatchAsDeposited(@PathVariable String id) {
        log.info("REST: Marking batch as deposited: {}", id);
        DepositBatch batch = depositBatchService.markBatchAsDeposited(id);
        return ResponseEntity.ok(batch);
    }
    
    /**
     * Request DTO for creating batch
     */
    @Data
    public static class CreateBatchRequest {
        private List<String> receiptIds;
    }
}

