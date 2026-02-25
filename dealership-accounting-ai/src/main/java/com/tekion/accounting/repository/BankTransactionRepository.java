package com.tekion.accounting.repository;

import com.tekion.accounting.model.BankTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for BankTransaction entity
 * 
 * Provides CRUD operations and custom queries for bank transactions
 */
@Repository
public interface BankTransactionRepository extends MongoRepository<BankTransaction, String> {
    
    /**
     * Find all bank transactions for a dealership
     */
    List<BankTransaction> findByDealershipId(String dealershipId);
    
    /**
     * Find bank transactions by dealership and status
     */
    List<BankTransaction> findByDealershipIdAndStatus(String dealershipId, String status);
    
    /**
     * Find unmatched transactions for a dealership
     * Used for AI reconciliation matching
     */
    List<BankTransaction> findByDealershipIdAndStatusOrderByDateDesc(
        String dealershipId, 
        String status
    );
    
    /**
     * Find bank transactions by dealership and date range
     */
    List<BankTransaction> findByDealershipIdAndDateBetween(
        String dealershipId,
        LocalDate startDate,
        LocalDate endDate
    );
    
    /**
     * Find bank transaction by transaction ID
     */
    Optional<BankTransaction> findByTransactionId(String transactionId);
    
    /**
     * Find bank transactions by dealership and type (CREDIT/DEBIT)
     */
    List<BankTransaction> findByDealershipIdAndType(String dealershipId, String type);
    
    /**
     * Count unmatched transactions
     */
    long countByDealershipIdAndStatus(String dealershipId, String status);
    
    /**
     * Check if transaction ID exists
     */
    boolean existsByTransactionId(String transactionId);

    /**
     * Find bank transactions by dealership, status, and type
     * Used for AI reconciliation (find unmatched credits)
     */
    List<BankTransaction> findByDealershipIdAndStatusAndType(
        String dealershipId,
        String status,
        String type
    );

    /**
     * Delete all bank transactions for a dealership
     * Used for seeding demo data
     */
    void deleteByDealershipId(String dealershipId);
}

