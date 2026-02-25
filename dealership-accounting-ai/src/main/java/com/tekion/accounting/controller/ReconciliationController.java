package com.tekion.accounting.controller;

import com.tekion.accounting.model.ReconciliationMatch;
import com.tekion.accounting.service.ReconciliationService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Reconciliation
 */
@RestController
@RequestMapping("/api/reconciliation")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ReconciliationController {
    
    private final ReconciliationService reconciliationService;
    
    /**
     * Confirm a match between bank transaction and deposit batch
     * POST /api/reconciliation/confirm-match
     */
    @PostMapping("/confirm-match")
    public ResponseEntity<ReconciliationMatch> confirmMatch(@RequestBody ConfirmMatchRequest request) {
        log.info("REST: Confirming match: Bank TXN {} <-> Batch {}", 
                 request.getBankTransactionId(), request.getDepositBatchId());
        
        ReconciliationMatch match = reconciliationService.confirmMatch(
            request.getBankTransactionId(),
            request.getDepositBatchId(),
            request.isAiSuggested(),
            request.getAiConfidence(),
            request.getAiReasons()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(match);
    }
    
    /**
     * Get all reconciliation matches
     * GET /api/reconciliation/matches
     */
    @GetMapping("/matches")
    public ResponseEntity<List<ReconciliationMatch>> getAllMatches() {
        log.info("REST: Getting all reconciliation matches");
        List<ReconciliationMatch> matches = reconciliationService.getAllMatches();
        return ResponseEntity.ok(matches);
    }
    
    /**
     * Get AI-suggested matches
     * GET /api/reconciliation/ai-suggested
     */
    @GetMapping("/ai-suggested")
    public ResponseEntity<List<ReconciliationMatch>> getAISuggestedMatches() {
        log.info("REST: Getting AI-suggested matches");
        List<ReconciliationMatch> matches = reconciliationService.getAISuggestedMatches();
        return ResponseEntity.ok(matches);
    }
    
    /**
     * Request DTO for confirming match
     */
    @Data
    @AllArgsConstructor
    public static class ConfirmMatchRequest {
        private String bankTransactionId;
        private String depositBatchId;
        private boolean aiSuggested;
        private Integer aiConfidence;
        private List<String> aiReasons;
    }
}

