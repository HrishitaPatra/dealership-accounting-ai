package com.tekion.accounting.service;

import com.tekion.accounting.model.DepositBatch;
import com.tekion.accounting.model.Receipt;
import com.tekion.accounting.repository.DepositBatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for DepositBatch business logic
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DepositBatchService {
    
    private final DepositBatchRepository depositBatchRepository;
    private final ReceiptService receiptService;
    
    private static final String DEALERSHIP_ID = "DEALER-001";
    
    /**
     * Create deposit batch from unbatched receipts
     */
    public DepositBatch createDepositBatch(List<String> receiptIds) {
        log.info("Creating deposit batch with {} receipts", receiptIds.size());
        
        if (receiptIds == null || receiptIds.isEmpty()) {
            throw new RuntimeException("Cannot create batch - no receipts provided");
        }
        
        // Get receipts and validate they're unbatched
        List<Receipt> receipts = receiptIds.stream()
            .map(receiptService::getReceiptById)
            .collect(Collectors.toList());
        
        // Validate all receipts are unbatched
        for (Receipt receipt : receipts) {
            if (!"UNBATCHED".equals(receipt.getStatus())) {
                throw new RuntimeException("Receipt already batched: " + receipt.getReceiptNumber());
            }
        }
        
        // Calculate total
        double total = receipts.stream()
            .mapToDouble(Receipt::getAmount)
            .sum();
        
        // Create batch
        DepositBatch batch = DepositBatch.builder()
            .dealershipId(DEALERSHIP_ID)
            .batchNumber(generateBatchNumber())
            .receiptIds(receiptIds)
            .total(total)
            .status("OPEN")
            .reconciled(false)
            .build();
        
        DepositBatch saved = depositBatchRepository.save(batch);
        
        // Mark receipts as batched
        receiptIds.forEach(receiptService::markReceiptAsBatched);
        
        log.info("Created deposit batch: {} with total: ${}", saved.getBatchNumber(), total);
        
        return saved;
    }
    
    /**
     * Mark batch as deposited
     */
    public DepositBatch markBatchAsDeposited(String batchId) {
        log.info("Marking batch as deposited: {}", batchId);
        
        DepositBatch batch = depositBatchRepository.findById(batchId)
            .orElseThrow(() -> new RuntimeException("Deposit batch not found: " + batchId));
        
        if ("DEPOSITED".equals(batch.getStatus())) {
            throw new RuntimeException("Batch already deposited: " + batch.getBatchNumber());
        }
        
        batch.setStatus("DEPOSITED");
        batch.setDepositedDate(LocalDateTime.now());
        
        DepositBatch saved = depositBatchRepository.save(batch);
        log.info("Marked batch as deposited: {}", saved.getBatchNumber());
        
        return saved;
    }
    
    /**
     * Get all unreconciled batches
     */
    public List<DepositBatch> getUnreconciledBatches() {
        return depositBatchRepository.findByDealershipIdAndStatusAndReconciledOrderByDepositedDateDesc(
            DEALERSHIP_ID, "DEPOSITED", false
        );
    }
    
    /**
     * Get all deposit batches
     */
    public List<DepositBatch> getAllDepositBatches() {
        return depositBatchRepository.findByDealershipId(DEALERSHIP_ID);
    }
    
    /**
     * Get deposit batch by ID
     */
    public DepositBatch getDepositBatchById(String batchId) {
        return depositBatchRepository.findById(batchId)
            .orElseThrow(() -> new RuntimeException("Deposit batch not found: " + batchId));
    }
    
    /**
     * Mark batch as reconciled
     */
    public void markBatchAsReconciled(String batchId) {
        DepositBatch batch = getDepositBatchById(batchId);
        batch.setReconciled(true);
        depositBatchRepository.save(batch);
        log.info("Marked batch as reconciled: {}", batch.getBatchNumber());
    }
    
    /**
     * Generate next batch number (BATCH-001, BATCH-002, etc.)
     */
    private String generateBatchNumber() {
        long count = depositBatchRepository.count();
        return String.format("BATCH-%03d", count + 1);
    }
}

