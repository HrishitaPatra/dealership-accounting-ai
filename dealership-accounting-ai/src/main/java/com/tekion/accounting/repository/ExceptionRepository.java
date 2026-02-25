package com.tekion.accounting.repository;

import com.tekion.accounting.model.Exception;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Exception entity
 * 
 * Provides CRUD operations and custom queries for exceptions
 */
@Repository
public interface ExceptionRepository extends MongoRepository<Exception, String> {
    
    /**
     * Find all exceptions for a dealership
     */
    List<Exception> findByDealershipId(String dealershipId);
    
    /**
     * Find exceptions by dealership and status
     */
    List<Exception> findByDealershipIdAndStatus(String dealershipId, String status);
    
    /**
     * Find open exceptions for a dealership
     * Used for exceptions queue display
     */
    List<Exception> findByDealershipIdAndStatusOrderByCreatedAtDesc(
        String dealershipId, 
        String status
    );
    
    /**
     * Find exceptions by dealership and type
     */
    List<Exception> findByDealershipIdAndType(String dealershipId, String type);
    
    /**
     * Find AI-detected exceptions
     */
    List<Exception> findByDealershipIdAndAiDetected(String dealershipId, Boolean aiDetected);
    
    /**
     * Find exception by exception number
     */
    Optional<Exception> findByExceptionNumber(String exceptionNumber);
    
    /**
     * Count exceptions by dealership and status
     */
    long countByDealershipIdAndStatus(String dealershipId, String status);
    
    /**
     * Count exceptions by dealership and type
     */
    long countByDealershipIdAndType(String dealershipId, String type);
    
    /**
     * Check if exception number exists
     */
    boolean existsByExceptionNumber(String exceptionNumber);
}

