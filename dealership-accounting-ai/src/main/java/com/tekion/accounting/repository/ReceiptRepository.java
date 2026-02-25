package com.tekion.accounting.repository;

import com.tekion.accounting.model.Receipt;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Receipt entity
 * 
 * Provides CRUD operations and custom queries for receipts
 */
@Repository
public interface ReceiptRepository extends MongoRepository<Receipt, String> {
    
    /**
     * Find all receipts for a dealership
     */
    List<Receipt> findByDealershipId(String dealershipId);
    
    /**
     * Find receipts by dealership and status
     */
    List<Receipt> findByDealershipIdAndStatus(String dealershipId, String status);
    
    /**
     * Find unbatched receipts for a dealership
     * Used for creating deposit batches
     */
    List<Receipt> findByDealershipIdAndStatusOrderByCreatedAtAsc(String dealershipId, String status);
    
    /**
     * Find receipt by receipt number
     */
    Optional<Receipt> findByReceiptNumber(String receiptNumber);
    
    /**
     * Find receipts by RO ID
     */
    List<Receipt> findByRoId(String roId);
    
    /**
     * Count receipts by dealership and status
     */
    long countByDealershipIdAndStatus(String dealershipId, String status);
    
    /**
     * Check if receipt number exists
     */
    boolean existsByReceiptNumber(String receiptNumber);
}

