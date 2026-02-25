package com.tekion.accounting.repository;

import com.tekion.accounting.model.ReconciliationMatch;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ReconciliationMatch entity
 * 
 * Provides CRUD operations and custom queries for reconciliation matches
 */
@Repository
public interface ReconciliationMatchRepository extends MongoRepository<ReconciliationMatch, String> {
    
    /**
     * Find all matches for a dealership
     */
    List<ReconciliationMatch> findByDealershipId(String dealershipId);
    
    /**
     * Find match by bank transaction ID
     */
    Optional<ReconciliationMatch> findByBankTransactionId(String bankTransactionId);
    
    /**
     * Find match by deposit batch ID
     */
    Optional<ReconciliationMatch> findByDepositBatchId(String depositBatchId);
    
    /**
     * Find AI-suggested matches
     */
    List<ReconciliationMatch> findByDealershipIdAndAiSuggested(String dealershipId, Boolean aiSuggested);
    
    /**
     * Find user-confirmed matches
     */
    List<ReconciliationMatch> findByDealershipIdAndUserConfirmed(String dealershipId, Boolean userConfirmed);
    
    /**
     * Find matches by dealership, ordered by match date
     */
    List<ReconciliationMatch> findByDealershipIdOrderByMatchedAtDesc(String dealershipId);
    
    /**
     * Count AI-suggested matches
     */
    long countByDealershipIdAndAiSuggested(String dealershipId, Boolean aiSuggested);
    
    /**
     * Count user-confirmed matches
     */
    long countByDealershipIdAndUserConfirmed(String dealershipId, Boolean userConfirmed);
}

