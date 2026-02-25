package com.tekion.accounting.service;

import com.tekion.accounting.model.Receipt;
import com.tekion.accounting.model.RepairOrder;
import com.tekion.accounting.repository.ReceiptRepository;
import com.tekion.accounting.repository.RepairOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for Receipt business logic
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReceiptService {
    
    private final ReceiptRepository receiptRepository;
    private final RepairOrderRepository repairOrderRepository;
    
    private static final String DEALERSHIP_ID = "DEALER-001";
    
    /**
     * Generate receipt from closed repair order
     */
    public Receipt generateReceiptFromRO(String roId) {
        log.info("Generating receipt for RO: {}", roId);
        
        // Get repair order
        RepairOrder ro = repairOrderRepository.findById(roId)
            .orElseThrow(() -> new RuntimeException("Repair order not found: " + roId));
        
        // Validate RO is closed
        if (!"CLOSED".equals(ro.getStatus())) {
            throw new RuntimeException("Cannot generate receipt - RO not closed: " + ro.getRoNumber());
        }
        
        // Check if receipt already exists for this RO
        List<Receipt> existingReceipts = receiptRepository.findByRoId(roId);
        if (!existingReceipts.isEmpty()) {
            throw new RuntimeException("Receipt already exists for RO: " + ro.getRoNumber());
        }
        
        // Create receipt
        Receipt receipt = Receipt.builder()
            .dealershipId(DEALERSHIP_ID)
            .receiptNumber(generateReceiptNumber())
            .roId(ro.getId())
            .roNumber(ro.getRoNumber())  // Denormalized for display
            .amount(ro.getTotal())
            .status("UNBATCHED")
            .build();
        
        Receipt saved = receiptRepository.save(receipt);
        log.info("Generated receipt: {} for RO: {}", saved.getReceiptNumber(), ro.getRoNumber());
        
        return saved;
    }
    
    /**
     * Get all unbatched receipts
     */
    public List<Receipt> getUnbatchedReceipts() {
        return receiptRepository.findByDealershipIdAndStatusOrderByCreatedAtAsc(DEALERSHIP_ID, "UNBATCHED");
    }
    
    /**
     * Get all receipts
     */
    public List<Receipt> getAllReceipts() {
        return receiptRepository.findByDealershipId(DEALERSHIP_ID);
    }
    
    /**
     * Get receipt by ID
     */
    public Receipt getReceiptById(String receiptId) {
        return receiptRepository.findById(receiptId)
            .orElseThrow(() -> new RuntimeException("Receipt not found: " + receiptId));
    }
    
    /**
     * Update receipt status to BATCHED
     */
    public void markReceiptAsBatched(String receiptId) {
        Receipt receipt = getReceiptById(receiptId);
        receipt.setStatus("BATCHED");
        receiptRepository.save(receipt);
        log.info("Marked receipt as batched: {}", receipt.getReceiptNumber());
    }
    
    /**
     * Generate next receipt number (RCT-001, RCT-002, etc.)
     */
    private String generateReceiptNumber() {
        long count = receiptRepository.count();
        return String.format("RCT-%03d", count + 1);
    }
}

