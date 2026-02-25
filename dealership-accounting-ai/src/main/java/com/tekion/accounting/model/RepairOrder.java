package com.tekion.accounting.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Repair Order - Main service document
 * 
 * Design: Embedded documents (Customer, Vehicle, LineItems)
 * Why: Always accessed together, atomic updates, better read performance
 * 
 * Multi-tenancy: dealershipId field (indexed)
 */
@Document(collection = "repair_orders")
@CompoundIndex(name = "dealership_status_created_idx", 
               def = "{'dealershipId': 1, 'status': 1, 'createdAt': -1}")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepairOrder {
    
    @Id
    private String id;
    
    // Multi-tenancy field
    @Indexed
    @Field("dealership_id")
    private String dealershipId;
    
    @Indexed(unique = true)
    @Field("ro_number")
    private String roNumber;  // Auto-generated: RO-001, RO-002, etc.
    
    // Embedded customer (not a separate collection)
    @NotNull(message = "Customer information is required")
    @Valid
    @Field("customer")
    private Customer customer;
    
    // Embedded vehicle (not a separate collection)
    @NotNull(message = "Vehicle information is required")
    @Valid
    @Field("vehicle")
    private Vehicle vehicle;
    
    // Embedded line items (array of subdocuments)
    @Field("line_items")
    @Builder.Default
    private List<@Valid LineItem> lineItems = new ArrayList<>();
    
    // Denormalized totals (calculated and stored for performance)
    @Field("subtotal")
    private Double subtotal;
    
    @Field("tax")
    private Double tax;
    
    @Field("total")
    private Double total;
    
    @Indexed
    @Field("status")
    private String status;  // OPEN, CLOSED
    
    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Calculate totals from line items
     * Called before saving to denormalize data
     */
    public void calculateTotals(double taxRate) {
        // Calculate subtotal from line items
        this.subtotal = lineItems.stream()
            .mapToDouble(item -> {
                item.calculateAmount();
                return item.getAmount();
            })
            .sum();
        
        // Calculate tax    
        this.tax = subtotal * taxRate;
        
        // Calculate total
        this.total = subtotal + tax;
    }
}

