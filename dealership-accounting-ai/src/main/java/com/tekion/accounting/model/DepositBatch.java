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

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Deposit Batch - Collection of receipts deposited together
 * 
 * Design: Array of receipt IDs (not embedded receipts)
 * Why: Receipts queried independently, batch size varies
 * 
 * Denormalization: Stores total for fast queries
 */
@Document(collection = "deposit_batches")
@CompoundIndex(name = "dealership_reconciled_deposited_idx", 
               def = "{'dealershipId': 1, 'reconciled': 1, 'depositedDate': -1}")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositBatch {
    
    @Id
    private String id;
    
    // Multi-tenancy field
    @Indexed
    @Field("dealership_id")
    private String dealershipId;
    
    @Indexed(unique = true)
    @Field("batch_number")
    private String batchNumber;  // Auto-generated: BATCH-001, BATCH-002, etc.
    
    // Array of receipt IDs
    @Field("receipt_ids")
    @Builder.Default
    private List<String> receiptIds = new ArrayList<>();
    
    // Denormalized total (sum of receipt amounts)
    @NotNull(message = "Total is required")
    @Min(value = 0, message = "Total must be positive")
    @Field("total")
    private Double total;
    
    @Indexed
    @Field("status")
    private String status;  // OPEN, DEPOSITED
    
    @Indexed
    @Field("reconciled")
    @Builder.Default
    private Boolean reconciled = false;
    
    @Field("deposited_date")
    private LocalDateTime depositedDate;
    
    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;
}

