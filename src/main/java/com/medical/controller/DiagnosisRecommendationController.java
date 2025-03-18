package com.medical.controller;

import com.medical.model.DiagnosisRecommendation;
import com.medical.service.DiagnosisRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class DiagnosisRecommendationController {
    private final DiagnosisRecommendationService service;

    @GetMapping
    public ResponseEntity<List<DiagnosisRecommendation>> getRecommendations(
            @RequestParam String icdCode,
            @RequestParam Integer patientAge,
            @RequestParam String medicationNdcCode) {
        return ResponseEntity.ok(service.getRecommendations(icdCode, patientAge, medicationNdcCode));
    }

    @PostMapping
    public ResponseEntity<DiagnosisRecommendation> createRecommendation(
            @RequestBody DiagnosisRecommendation recommendation) {
        return ResponseEntity.ok(service.saveRecommendation(recommendation));
    }
} 