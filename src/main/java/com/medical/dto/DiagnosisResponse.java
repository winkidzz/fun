package com.medical.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class DiagnosisResponse {
    private String recommendation;
    private Double confidenceScore;
    private String matchedRuleId;
} 