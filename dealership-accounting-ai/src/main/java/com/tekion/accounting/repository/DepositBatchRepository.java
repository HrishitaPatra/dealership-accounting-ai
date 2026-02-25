package com.tekion.accounting.repository;

import com.tekion.accounting.model.DepositBatch;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for DepositBatch entity
 * 
 * Provides CRUD operations and custom queries for deposit batches
 */
@Repository
public interface DepositBatchRepository extends MongoRepository<DepositBatch, String> {
    
    /**
     * Find all deposit batches for a dealership
     */
    List<DepositBatch> findByDealershipId(String dealershipId);
    
    /**
     * Find deposit batches by dealership and status
     */
    List<DepositBatch> findByDealershipIdAndStatus(String dealershipId, String status);
    
    /**
     * Find unreconciled batches for a dealership
     * Used for AI reconciliation matching
     */
    List<DepositBatch> findByDealershipIdAndReconciledOrderByDepositedDateDesc(
        String dealershipId, 
        Boolean reconciled
    );
    
    /**
     * Find deposited but unreconciled batches
     */
    List<DepositBatch> findByDealershipIdAndStatusAndReconciledOrderByDepositedDateDesc(
        String dealershipId,
        String status,
        Boolean reconciled
    );
    
    /**
     * Find deposit batch by batch number
     */
    Optional<DepositBatch> findByBatchNumber(String batchNumber);
    
    /**
     * Count unreconciled batches
     */
    long countByDealershipIdAndReconciled(String dealershipId, Boolean reconciled);
    
    /**
     * Check if batch number exists
     */
    boolean existsByBatchNumber(String batchNumber);

    /**
     * Find deposit batches by dealership and reconciled status
     * Used for AI reconciliation
     */
    List<DepositBatch> findByDealershipIdAndReconciled(
        String dealershipId,
        Boolean reconciled
    );
}

