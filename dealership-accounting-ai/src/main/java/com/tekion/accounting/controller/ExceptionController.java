package com.tekion.accounting.controller;

import com.tekion.accounting.model.Exception;
import com.tekion.accounting.service.ExceptionService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API for managing accounting exceptions
 */
@RestController
@RequestMapping("/api/exceptions")
@RequiredArgsConstructor
@Slf4j
public class ExceptionController {
    
    private final ExceptionService exceptionService;
    
    /**
     * Get all open exceptions
     * GET /api/exceptions/open
     */
    @GetMapping("/open")
    public ResponseEntity<List<Exception>> getOpenExceptions() {
        log.info("Getting open exceptions");
        List<Exception> exceptions = exceptionService.getOpenExceptions();
        return ResponseEntity.ok(exceptions);
    }
    
    /**
     * Get all resolved exceptions
     * GET /api/exceptions/resolved
     */
    @GetMapping("/resolved")
    public ResponseEntity<List<Exception>> getResolvedExceptions() {
        log.info("Getting resolved exceptions");
        List<Exception> exceptions = exceptionService.getResolvedExceptions();
        return ResponseEntity.ok(exceptions);
    }

    /**
     * Get exception by ID
     * GET /api/exceptions/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Exception> getExceptionById(@PathVariable String id) {
        log.info("Getting exception by ID: {}", id);
        Exception exception = exceptionService.getExceptionById(id);
        if (exception == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(exception);
    }
    
    /**
     * Resolve an exception
     * PUT /api/exceptions/{id}/resolve
     */
    @PutMapping("/{id}/resolve")
    public ResponseEntity<Exception> resolveException(@PathVariable String id) {
        log.info("Resolving exception: {}", id);
        Exception exception = exceptionService.resolveException(id, "Resolved by user");
        return ResponseEntity.ok(exception);
    }
}

