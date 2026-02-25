package com.tekion.accounting.service;

import com.tekion.accounting.repository.BankTransactionRepository;
import com.tekion.accounting.repository.DepositBatchRepository;
import com.tekion.accounting.repository.ExceptionRepository;
import com.tekion.accounting.repository.ReceiptRepository;
import com.tekion.accounting.repository.RepairOrderRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for Dashboard metrics
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {
    
    private final RepairOrderRepository repairOrderRepository;
    private final ReceiptRepository receiptRepository;
    private final DepositBatchRepository depositBatchRepository;
    private final BankTransactionRepository bankTransactionRepository;
    private final ExceptionRepository exceptionRepository;
    
    private static final String DEALERSHIP_ID = "DEALER-001";
    
    /**
     * Get dashboard metrics
     */
    public DashboardMetrics getDashboardMetrics() {
        log.info("Calculating dashboard metrics for dealership: {}", DEALERSHIP_ID);
        
        // Count open ROs
        long openROs = repairOrderRepository.countByDealershipIdAndStatus(DEALERSHIP_ID, "OPEN");
        
        // Count unbatched receipts
        long unbatchedReceipts = receiptRepository.countByDealershipIdAndStatus(DEALERSHIP_ID, "UNBATCHED");
        
        // Count unreconciled batches
        long unreconciledBatches = depositBatchRepository.countByDealershipIdAndReconciled(DEALERSHIP_ID, false);
        
        // Count unmatched bank transactions
        long unmatchedTransactions = bankTransactionRepository.countByDealershipIdAndStatus(DEALERSHIP_ID, "UNMATCHED");
        
        // Count open exceptions
        long openExceptions = exceptionRepository.countByDealershipIdAndStatus(DEALERSHIP_ID, "OPEN");
        
        // Calculate close readiness percentage
        // Close is ready when: unbatched receipts = 0, unreconciled batches = 0, open exceptions = 0
        int closeReadiness = calculateCloseReadiness(unbatchedReceipts, unreconciledBatches, openExceptions);
        
        DashboardMetrics metrics = DashboardMetrics.builder()
            .openRepairOrders(openROs)
            .unbatchedReceipts(unbatchedReceipts)
            .unreconciledBatches(unreconciledBatches)
            .unmatchedBankTransactions(unmatchedTransactions)
            .openExceptions(openExceptions)
            .closeReadinessPercentage(closeReadiness)
            .build();
        
        log.info("Dashboard metrics: {}", metrics);
        
        return metrics;
    }
    
    /**
     * Calculate close readiness percentage
     * 100% = ready to close (all reconciled, no exceptions)
     * 0% = not ready (many items pending)
     */
    private int calculateCloseReadiness(long unbatchedReceipts, long unreconciledBatches, long openExceptions) {
        int score = 100;
        
        // Deduct points for pending items
        if (unbatchedReceipts > 0) {
            score -= 30;  // Unbatched receipts are critical
        }
        
        if (unreconciledBatches > 0) {
            score -= 40;  // Unreconciled batches are most critical
        }
        
        if (openExceptions > 0) {
            score -= 30;  // Open exceptions block close
        }
        
        return Math.max(0, score);
    }
    
    /**
     * Dashboard metrics DTO
     */
    @Data
    @Builder
    @AllArgsConstructor
    public static class DashboardMetrics {
        private long openRepairOrders;
        private long unbatchedReceipts;
        private long unreconciledBatches;
        private long unmatchedBankTransactions;
        private long openExceptions;
        private int closeReadinessPercentage;
    }
}

