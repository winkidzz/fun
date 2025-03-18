package com.medical.service;

import com.medical.model.DiagnosisRecommendation;
import com.medical.repository.DiagnosisRecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiagnosisRecommendationService {
    private final DiagnosisRecommendationRepository repository;

    public List<DiagnosisRecommendation> getRecommendations(String icdCode, Integer patientAge, String medicationNdcCode) {
        return repository.findByIcdCodeAndPatientAgeAndMedicationNdcCode(icdCode, patientAge, medicationNdcCode);
    }

    public DiagnosisRecommendation saveRecommendation(DiagnosisRecommendation recommendation) {
        return repository.save(recommendation);
    }
} 