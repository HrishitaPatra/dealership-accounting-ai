package com.tekion.accounting.controller;

import com.tekion.accounting.model.Receipt;
import com.tekion.accounting.service.ReceiptService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Receipts
 */
@RestController
@RequestMapping("/api/receipts")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ReceiptController {
    
    private final ReceiptService receiptService;
    
    /**
     * Generate receipt from repair order
     * POST /api/receipts
     */
    @PostMapping
    public ResponseEntity<Receipt> generateReceipt(@RequestBody GenerateReceiptRequest request) {
        log.info("REST: Generating receipt for RO: {}", request.getRoId());
        Receipt receipt = receiptService.generateReceiptFromRO(request.getRoId());
        return ResponseEntity.status(HttpStatus.CREATED).body(receipt);
    }
    
    /**
     * Get all receipts
     * GET /api/receipts
     */
    @GetMapping
    public ResponseEntity<List<Receipt>> getAllReceipts() {
        log.info("REST: Getting all receipts");
        List<Receipt> receipts = receiptService.getAllReceipts();
        return ResponseEntity.ok(receipts);
    }
    
    /**
     * Get unbatched receipts
     * GET /api/receipts/unbatched
     */
    @GetMapping("/unbatched")
    public ResponseEntity<List<Receipt>> getUnbatchedReceipts() {
        log.info("REST: Getting unbatched receipts");
        List<Receipt> receipts = receiptService.getUnbatchedReceipts();
        return ResponseEntity.ok(receipts);
    }
    
    /**
     * Request DTO for generating receipt
     */
    @Data
    public static class GenerateReceiptRequest {
        private String roId;
    }
}

