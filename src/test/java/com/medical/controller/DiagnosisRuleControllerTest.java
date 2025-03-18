package com.medical.controller;

import com.medical.dto.DiagnosisRequest;
import com.medical.dto.DiagnosisResponse;
import com.medical.model.DiagnosisRule;
import com.medical.service.DiagnosisRuleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiagnosisRuleControllerTest {

    @Mock
    private DiagnosisRuleService service;

    @InjectMocks
    private DiagnosisRuleController controller;

    private DiagnosisRule testRule;
    private DiagnosisRequest testRequest;
    private DiagnosisResponse testResponse;

    @BeforeEach
    void setUp() {
        testRule = new DiagnosisRule();
        testRule.setId("1");
        testRule.setIcdCodes(Arrays.asList("E11.9", "F41.9"));
        testRule.setNdcCodes(Arrays.asList("0002-8215-01", "0002-8215-02"));
        testRule.setMinAge(18);
        testRule.setMaxAge(65);
        testRule.setRecommendation("Test recommendation");
        testRule.setConfidenceScore(0.85);
        testRule.setActive(true);

        testRequest = new DiagnosisRequest();
        testRequest.setIcdCode("E11.9");
        testRequest.setMedicationNdcCode("0002-8215-01");
        testRequest.setPatientDob(LocalDate.now().minusYears(30));

        testResponse = DiagnosisResponse.builder()
                .recommendation("Test recommendation")
                .confidenceScore(0.85)
                .matchedRuleId("1")
                .build();
    }

    @Test
    void createRule_ShouldReturnCreatedRule() {
        when(service.createRule(any(DiagnosisRule.class))).thenReturn(testRule);

        ResponseEntity<DiagnosisRule> response = controller.createRule(testRule);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testRule, response.getBody());
        verify(service).createRule(testRule);
    }

    @Test
    void updateRule_ShouldReturnUpdatedRule() {
        when(service.updateRule(eq("1"), any(DiagnosisRule.class))).thenReturn(testRule);

        ResponseEntity<DiagnosisRule> response = controller.updateRule("1", testRule);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testRule, response.getBody());
        verify(service).updateRule("1", testRule);
    }

    @Test
    void deleteRule_ShouldReturnOk() {
        ResponseEntity<Void> response = controller.deleteRule("1");

        assertEquals(200, response.getStatusCodeValue());
        verify(service).deleteRule("1");
    }

    @Test
    void getRule_ShouldReturnRule() {
        when(service.getRule("1")).thenReturn(testRule);

        ResponseEntity<DiagnosisRule> response = controller.getRule("1");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testRule, response.getBody());
        verify(service).getRule("1");
    }

    @Test
    void getAllRules_ShouldReturnAllRules() {
        List<DiagnosisRule> expectedRules = Arrays.asList(testRule);
        when(service.getAllRules()).thenReturn(expectedRules);

        ResponseEntity<List<DiagnosisRule>> response = controller.getAllRules();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedRules, response.getBody());
        verify(service).getAllRules();
    }

    @Test
    void getRecommendation_ShouldReturnRecommendation() {
        when(service.getRecommendation(any(DiagnosisRequest.class))).thenReturn(testResponse);

        ResponseEntity<DiagnosisResponse> response = controller.getRecommendation(testRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testResponse, response.getBody());
        verify(service).getRecommendation(testRequest);
    }
} 