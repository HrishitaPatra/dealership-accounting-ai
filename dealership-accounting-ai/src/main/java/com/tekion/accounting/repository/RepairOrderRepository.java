package com.tekion.accounting.repository;

import com.tekion.accounting.model.RepairOrder;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for RepairOrder entity
 * 
 * Provides CRUD operations and custom queries for repair orders
 */
@Repository
public interface RepairOrderRepository extends MongoRepository<RepairOrder, String> {
    
    /**
     * Find all repair orders for a dealership
     */
    List<RepairOrder> findByDealershipId(String dealershipId);
    
    /**
     * Find repair orders by dealership and status
     */
    List<RepairOrder> findByDealershipIdAndStatus(String dealershipId, String status);
    
    /**
     * Find repair orders by dealership, ordered by creation date (newest first)
     */
    List<RepairOrder> findByDealershipIdOrderByCreatedAtDesc(String dealershipId);
    
    /**
     * Find repair order by RO number
     */
    Optional<RepairOrder> findByRoNumber(String roNumber);
    
    /**
     * Find repair order by dealership and RO number
     */
    Optional<RepairOrder> findByDealershipIdAndRoNumber(String dealershipId, String roNumber);
    
    /**
     * Count repair orders by dealership and status
     */
    long countByDealershipIdAndStatus(String dealershipId, String status);
    
    /**
     * Check if RO number exists
     */
    boolean existsByRoNumber(String roNumber);
}

