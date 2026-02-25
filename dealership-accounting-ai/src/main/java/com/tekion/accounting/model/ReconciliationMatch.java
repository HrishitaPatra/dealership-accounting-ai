package com.tekion.accounting.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Reconciliation Match - Links bank transaction to deposit batch
 * 
 * Design: Separate collection (not embedded)
 * Why: Audit trail, AI metadata tracking, query match history
 */
@Document(collection = "reconciliation_matches")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReconciliationMatch {
    
    @Id
    private String id;
    
    // Multi-tenancy field
    @Indexed
    @Field("dealership_id")
    private String dealershipId;
    
    @NotBlank(message = "Bank transaction ID is required")
    @Indexed
    @Field("bank_transaction_id")
    private String bankTransactionId;
    
    @NotBlank(message = "Deposit batch ID is required")
    @Indexed
    @Field("deposit_batch_id")
    private String depositBatchId;
    
    // AI metadata (for tracking AI performance)
    @Field("ai_suggested")
    @Builder.Default
    private Boolean aiSuggested = false;
    
    @Field("ai_confidence")
    @Min(value = 0, message = "Confidence must be between 0 and 100")
    @Max(value = 100, message = "Confidence must be between 0 and 100")
    private Integer aiConfidence;  // 0-100
    
    @Field("ai_reasons")
    @Builder.Default
    private List<String> aiReasons = new ArrayList<>();  // ["Exact amount match", "Date within 1 day"]

    // New fields for AI reconciliation
    @Field("match_type")
    private String matchType;  // EXACT_MATCH, MERCHANT_FEE_MATCH, etc.

    @Field("confidence_score")
    private Double confidenceScore;  // 0.0-100.0

    @Field("ai_explanation")
    private String aiExplanation;  // Llama-generated explanation

    @Field("status")
    @Builder.Default
    private String status = "SUGGESTED";  // SUGGESTED, CONFIRMED, REJECTED

    @Field("user_confirmed")
    @Builder.Default
    private Boolean userConfirmed = false;

    @Field("matched_by")
    private String matchedBy;  // "DEMO-USER" for demo

    @CreatedDate
    @Field("matched_at")
    private LocalDateTime matchedAt;
}

