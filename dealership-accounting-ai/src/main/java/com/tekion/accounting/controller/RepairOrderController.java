package com.tekion.accounting.controller;

import com.tekion.accounting.model.RepairOrder;
import com.tekion.accounting.service.RepairOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * REST Controller for Repair Orders
 */
@RestController
@RequestMapping("/api/repair-orders")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class RepairOrderController {
    
    private final RepairOrderService repairOrderService;
    
    /**
     * Create a new repair order
     * POST /api/repair-orders
     */
    @PostMapping
    public ResponseEntity<RepairOrder> createRepairOrder(@Valid @RequestBody RepairOrder repairOrder) {
        log.info("REST: Creating repair order");
        RepairOrder created = repairOrderService.createRepairOrder(repairOrder);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    /**
     * Get all repair orders
     * GET /api/repair-orders
     */
    @GetMapping
    public ResponseEntity<List<RepairOrder>> getAllRepairOrders(
            @RequestParam(required = false) String status) {
        log.info("REST: Getting all repair orders, status: {}", status);
        
        List<RepairOrder> repairOrders;
        if (status != null) {
            repairOrders = repairOrderService.getRepairOrdersByStatus(status);
        } else {
            repairOrders = repairOrderService.getAllRepairOrders();
        }
        
        return ResponseEntity.ok(repairOrders);
    }
    
    /**
     * Get repair order by ID
     * GET /api/repair-orders/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<RepairOrder> getRepairOrderById(@PathVariable String id) {
        log.info("REST: Getting repair order by ID: {}", id);
        RepairOrder repairOrder = repairOrderService.getRepairOrderById(id);
        return ResponseEntity.ok(repairOrder);
    }
    
    /**
     * Close a repair order
     * PUT /api/repair-orders/{id}/close
     */
    @PutMapping("/{id}/close")
    public ResponseEntity<RepairOrder> closeRepairOrder(@PathVariable String id) {
        log.info("REST: Closing repair order: {}", id);
        RepairOrder closed = repairOrderService.closeRepairOrder(id);
        return ResponseEntity.ok(closed);
    }
}

