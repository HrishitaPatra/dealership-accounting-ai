package com.tekion.accounting.service;

import com.tekion.accounting.model.Exception;
import com.tekion.accounting.repository.ExceptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for managing accounting exceptions
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExceptionService {
    
    private final ExceptionRepository exceptionRepository;
    
    private static final String DEALERSHIP_ID = "DEALER-001";
    
    /**
     * Create a new exception
     */
    public Exception createException(
            String type,
            String description,
            Double amount,
            String bankTransactionId,
            String depositBatchId,
            String aiGeneratedMemo,
            String suggestedGlAccount
    ) {
        log.info("Creating exception: type={}, amount={}", type, amount);
        
        Exception exception = new Exception();
        exception.setDealershipId(DEALERSHIP_ID);
        exception.setExceptionNumber(generateExceptionNumber());
        exception.setType(type);
        exception.setDescription(description);
        exception.setAmount(amount);
        exception.setBankTransactionId(bankTransactionId);
        exception.setDepositBatchId(depositBatchId);
        exception.setAiGeneratedMemo(aiGeneratedMemo);
        exception.setSuggestedGlAccount(suggestedGlAccount);
        exception.setStatus("OPEN");
        
        Exception saved = exceptionRepository.save(exception);
        log.info("Created exception: {}", saved.getExceptionNumber());
        
        return saved;
    }
    
    /**
     * Get all open exceptions
     */
    public List<Exception> getOpenExceptions() {
        log.info("Getting open exceptions for dealership: {}", DEALERSHIP_ID);
        return exceptionRepository.findByDealershipIdAndStatus(DEALERSHIP_ID, "OPEN");
    }
    
    /**
     * Get all resolved exceptions
     */
    public List<Exception> getResolvedExceptions() {
        log.info("Getting resolved exceptions for dealership: {}", DEALERSHIP_ID);
        return exceptionRepository.findByDealershipIdAndStatus(DEALERSHIP_ID, "RESOLVED");
    }

    /**
     * Get exception by ID
     */
    public Exception getExceptionById(String exceptionId) {
        log.info("Getting exception by ID: {}", exceptionId);
        return exceptionRepository.findById(exceptionId).orElse(null);
    }
    
    /**
     * Resolve an exception
     */
    public Exception resolveException(String exceptionId, String resolutionNotes) {
        log.info("Resolving exception: {}", exceptionId);

        Exception exception = exceptionRepository.findById(exceptionId)
                .orElseThrow(() -> new RuntimeException("Exception not found: " + exceptionId));

        exception.setStatus("RESOLVED");
        exception.setResolutionNotes(resolutionNotes);
        exception.setResolvedAt(java.time.LocalDateTime.now());
        exception.setResolvedBy("DEMO-USER");

        Exception saved = exceptionRepository.save(exception);
        log.info("Resolved exception: {}", saved.getExceptionNumber());

        return saved;
    }
    
    /**
     * Generate exception number (EXC-001, EXC-002, etc.)
     */
    private String generateExceptionNumber() {
        long count = exceptionRepository.count();
        return String.format("EXC-%03d", count + 1);
    }
    
    /**
     * Count open exceptions (for dashboard)
     */
    public long countOpenExceptions() {
        return exceptionRepository.findByDealershipIdAndStatus(DEALERSHIP_ID, "OPEN").size();
    }
}

