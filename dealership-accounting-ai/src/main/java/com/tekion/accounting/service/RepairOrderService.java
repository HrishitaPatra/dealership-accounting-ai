package com.tekion.accounting.service;

import com.tekion.accounting.model.RepairOrder;
import com.tekion.accounting.repository.RepairOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for RepairOrder business logic
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RepairOrderService {
    
    private final RepairOrderRepository repairOrderRepository;
    
    @Value("${app.tax-rate}")
    private double taxRate;
    
    private static final String DEALERSHIP_ID = "DEALER-001";
    
    /**
     * Create a new repair order
     */
    public RepairOrder createRepairOrder(RepairOrder repairOrder) {
        log.info("Creating repair order for dealership: {}", DEALERSHIP_ID);
        
        // Set dealership ID (multi-tenancy)
        repairOrder.setDealershipId(DEALERSHIP_ID);
        
        // Generate RO number
        String roNumber = generateRoNumber();
        repairOrder.setRoNumber(roNumber);
        
        // Set initial status
        repairOrder.setStatus("OPEN");
        
        // Calculate totals
        repairOrder.calculateTotals(taxRate);
        
        RepairOrder saved = repairOrderRepository.save(repairOrder);
        log.info("Created repair order: {}", saved.getRoNumber());
        
        return saved;
    }
    
    /**
     * Close a repair order
     */
    public RepairOrder closeRepairOrder(String roId) {
        log.info("Closing repair order: {}", roId);
        
        RepairOrder ro = repairOrderRepository.findById(roId)
            .orElseThrow(() -> new RuntimeException("Repair order not found: " + roId));
        
        if ("CLOSED".equals(ro.getStatus())) {
            throw new RuntimeException("Repair order already closed: " + ro.getRoNumber());
        }
        
        ro.setStatus("CLOSED");
        
        RepairOrder saved = repairOrderRepository.save(ro);
        log.info("Closed repair order: {}", saved.getRoNumber());
        
        return saved;
    }
    
    /**
     * Get all repair orders for dealership
     */
    public List<RepairOrder> getAllRepairOrders() {
        return repairOrderRepository.findByDealershipIdOrderByCreatedAtDesc(DEALERSHIP_ID);
    }
    
    /**
     * Get repair orders by status
     */
    public List<RepairOrder> getRepairOrdersByStatus(String status) {
        return repairOrderRepository.findByDealershipIdAndStatus(DEALERSHIP_ID, status);
    }
    
    /**
     * Get repair order by ID
     */
    public RepairOrder getRepairOrderById(String roId) {
        return repairOrderRepository.findById(roId)
            .orElseThrow(() -> new RuntimeException("Repair order not found: " + roId));
    }
    
    /**
     * Generate next RO number (RO-001, RO-002, etc.)
     */
    private String generateRoNumber() {
        long count = repairOrderRepository.count();
        return String.format("RO-%03d", count + 1);
    }
}

