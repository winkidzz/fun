package com.medical.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DiagnosisRequest {
    private String icdCode;
    private LocalDate patientDob;
    private String medicationNdcCode;
} 