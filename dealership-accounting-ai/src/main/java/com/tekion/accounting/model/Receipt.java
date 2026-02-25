package com.tekion.accounting.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Receipt - Payment record
 * 
 * Design: References RepairOrder (not embedded)
 * Why: Different lifecycle (receipt exists after RO closed),
 *      queried independently by accounting
 * 
 * Denormalization: Stores roNumber for display without lookup
 */
@Document(collection = "receipts")
@CompoundIndex(name = "dealership_status_idx", 
               def = "{'dealershipId': 1, 'status': 1}")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Receipt {
    
    @Id
    private String id;
    
    // Multi-tenancy field
    @Indexed
    @Field("dealership_id")
    private String dealershipId;
    
    @Indexed(unique = true)
    @Field("receipt_number")
    private String receiptNumber;  // Auto-generated: RCT-001, RCT-002, etc.
    
    // Reference to RepairOrder (not embedded)
    @NotBlank(message = "RO ID is required")
    @Field("ro_id")
    private String roId;
    
    // Denormalized for display (avoid lookup)
    @Field("ro_number")
    private String roNumber;
    
    @NotNull(message = "Amount is required")
    @Min(value = 0, message = "Amount must be positive")
    @Field("amount")
    private Double amount;
    
    @Indexed
    @Field("status")
    private String status;  // UNBATCHED, BATCHED
    
    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;
}

