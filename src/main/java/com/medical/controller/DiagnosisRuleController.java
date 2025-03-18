package com.medical.controller;

import com.medical.dto.DiagnosisRequest;
import com.medical.dto.DiagnosisResponse;
import com.medical.model.DiagnosisRule;
import com.medical.service.DiagnosisRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diagnosis-rules")
@RequiredArgsConstructor
public class DiagnosisRuleController {
    private final DiagnosisRuleService service;

    @PostMapping
    public ResponseEntity<DiagnosisRule> createRule(@RequestBody DiagnosisRule rule) {
        return ResponseEntity.ok(service.createRule(rule));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiagnosisRule> updateRule(
            @PathVariable String id,
            @RequestBody DiagnosisRule rule) {
        return ResponseEntity.ok(service.updateRule(id, rule));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable String id) {
        service.deleteRule(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiagnosisRule> getRule(@PathVariable String id) {
        return ResponseEntity.ok(service.getRule(id));
    }

    @GetMapping
    public ResponseEntity<List<DiagnosisRule>> getAllRules() {
        return ResponseEntity.ok(service.getAllRules());
    }

    @PostMapping("/recommend")
    public ResponseEntity<DiagnosisResponse> getRecommendation(@RequestBody DiagnosisRequest request) {
        return ResponseEntity.ok(service.getRecommendation(request));
    }
} 