package com.tekion.accounting.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Line item (labor or part) embedded in RepairOrder
 * Not a separate MongoDB collection - always part of RO document
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LineItem {
    
    @NotBlank(message = "Line item type is required")
    private String type;  // LABOR or PART
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    @NotNull(message = "Rate is required")
    @Min(value = 0, message = "Rate must be positive")
    private Double rate;
    
    // Calculated field (quantity * rate)
    private Double amount;
    
    /**
     * Calculate amount from quantity and rate
     */
    public void calculateAmount() {
        if (quantity != null && rate != null) {
            this.amount = quantity * rate;
        }
    }
}

