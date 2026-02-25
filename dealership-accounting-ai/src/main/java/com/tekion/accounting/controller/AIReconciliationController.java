package com.tekion.accounting.controller;

import com.tekion.accounting.service.AIReconciliationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST API for AI-powered reconciliation
 */
@RestController
@RequestMapping("/api/ai-reconciliation")
@RequiredArgsConstructor
@Slf4j
public class AIReconciliationController {
    
    private final AIReconciliationService aiReconciliationService;
    
    /**
     * Run AI reconciliation
     * POST /api/ai-reconciliation/run
     */
    @PostMapping("/run")
    public ResponseEntity<AIReconciliationService.ReconciliationResult> runReconciliation() {
        log.info("Running AI reconciliation");
        AIReconciliationService.ReconciliationResult result = aiReconciliationService.runAIReconciliation();
        return ResponseEntity.ok(result);
    }
}

