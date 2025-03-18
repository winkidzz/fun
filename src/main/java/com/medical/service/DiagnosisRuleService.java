package com.medical.service;

import com.medical.dto.DiagnosisRequest;
import com.medical.dto.DiagnosisResponse;
import com.medical.model.DiagnosisRule;
import com.medical.repository.DiagnosisRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DiagnosisRuleService {
    private final DiagnosisRuleRepository repository;

    public DiagnosisRule createRule(DiagnosisRule rule) {
        rule.setActive(true);
        return repository.save(rule);
    }

    public DiagnosisRule updateRule(String id, DiagnosisRule rule) {
        DiagnosisRule existingRule = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rule not found"));
        
        existingRule.setIcdCodes(rule.getIcdCodes());
        existingRule.setNdcCodes(rule.getNdcCodes());
        existingRule.setMinAge(rule.getMinAge());
        existingRule.setMaxAge(rule.getMaxAge());
        existingRule.setRecommendation(rule.getRecommendation());
        existingRule.setConfidenceScore(rule.getConfidenceScore());
        existingRule.setActive(rule.getActive());
        
        return repository.save(existingRule);
    }

    public void deleteRule(String id) {
        repository.deleteById(id);
    }

    public DiagnosisRule getRule(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rule not found"));
    }

    public List<DiagnosisRule> getAllRules() {
        return (List<DiagnosisRule>) repository.findAll();
    }

    public DiagnosisResponse getRecommendation(DiagnosisRequest request) {
        List<DiagnosisRule> matchingRules = repository.findByActiveTrueAndIcdCodesContainingAndNdcCodesContaining(
                request.getIcdCode(), request.getMedicationNdcCode());

        int patientAge = calculateAge(request.getPatientDob());

        Optional<DiagnosisRule> matchingRule = matchingRules.stream()
                .filter(rule -> isAgeInRange(patientAge, rule.getMinAge(), rule.getMaxAge()))
                .findFirst();

        if (matchingRule.isPresent()) {
            DiagnosisRule rule = matchingRule.get();
            return DiagnosisResponse.builder()
                    .recommendation(rule.getRecommendation())
                    .confidenceScore(rule.getConfidenceScore())
                    .matchedRuleId(rule.getId())
                    .build();
        }

        return DiagnosisResponse.builder()
                .recommendation("No matching diagnosis rule found")
                .confidenceScore(0.0)
                .matchedRuleId(null)
                .build();
    }

    private int calculateAge(LocalDate dob) {
        return Period.between(dob, LocalDate.now()).getYears();
    }

    private boolean isAgeInRange(int age, Integer minAge, Integer maxAge) {
        if (minAge != null && age < minAge) {
            return false;
        }
        if (maxAge != null && age > maxAge) {
            return false;
        }
        return true;
    }
} 