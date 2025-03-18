package com.medical.repository;

import com.medical.model.DiagnosisRecommendation;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiagnosisRecommendationRepository extends ElasticsearchRepository<DiagnosisRecommendation, String> {
    List<DiagnosisRecommendation> findByIcdCodeAndPatientAgeAndMedicationNdcCode(
            String icdCode, Integer patientAge, String medicationNdcCode);
} 