package com.medical.service;

import com.medical.dto.DiagnosisRequest;
import com.medical.dto.DiagnosisResponse;
import com.medical.model.DiagnosisRule;
import com.medical.repository.DiagnosisRuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiagnosisRuleServiceTest {

    @Mock
    private DiagnosisRuleRepository repository;

    private DiagnosisRuleService service;

    private DiagnosisRule testRule;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new DiagnosisRuleService(repository);
        testRule = new DiagnosisRule();
        testRule.setId("1");
        testRule.setIcdCodes(Arrays.asList("E11.9", "F41.9"));
        testRule.setNdcCodes(Arrays.asList("0002-8215-01", "0002-8215-02"));
        testRule.setMinAge(18);
        testRule.setMaxAge(65);
        testRule.setRecommendation("Test recommendation");
        testRule.setConfidenceScore(0.85);
        testRule.setActive(true);
    }

    @Test
    void createRule_ShouldSetActiveAndSave() {
        when(repository.save(any(DiagnosisRule.class))).thenReturn(testRule);

        DiagnosisRule result = service.createRule(testRule);

        assertTrue(result.getActive());
        verify(repository).save(testRule);
    }

    @Test
    void updateRule_WhenRuleExists_ShouldUpdateAndSave() {
        when(repository.findById("1")).thenReturn(Optional.of(testRule));
        when(repository.save(any(DiagnosisRule.class))).thenReturn(testRule);

        DiagnosisRule updatedRule = new DiagnosisRule();
        updatedRule.setRecommendation("Updated recommendation");
        
        DiagnosisRule result = service.updateRule("1", updatedRule);

        assertEquals("1", result.getId());
        assertEquals("Updated recommendation", result.getRecommendation());
        verify(repository).save(any(DiagnosisRule.class));
    }

    @Test
    void updateRule_WhenRuleDoesNotExist_ShouldThrowException() {
        when(repository.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.updateRule("1", testRule));
        verify(repository, never()).save(any(DiagnosisRule.class));
    }

    @Test
    void deleteRule_ShouldCallRepositoryDelete() {
        service.deleteRule("1");
        verify(repository).deleteById("1");
    }

    @Test
    void getRule_WhenRuleExists_ShouldReturnRule() {
        when(repository.findById("1")).thenReturn(Optional.of(testRule));

        DiagnosisRule result = service.getRule("1");

        assertEquals(testRule, result);
    }

    @Test
    void getRule_WhenRuleDoesNotExist_ShouldThrowException() {
        when(repository.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.getRule("1"));
    }

    @Test
    void getAllRules_ShouldReturnAllRules() {
        List<DiagnosisRule> expectedRules = Arrays.asList(testRule);
        when(repository.findAll()).thenReturn(expectedRules);

        List<DiagnosisRule> result = service.getAllRules();

        assertEquals(expectedRules, result);
    }

    @Test
    void getRecommendation_WhenMatchingRuleExists_ShouldReturnRecommendation() {
        DiagnosisRequest request = new DiagnosisRequest();
        request.setIcdCode("E11.9");
        request.setMedicationNdcCode("0002-8215-01");
        request.setPatientDob(LocalDate.now().minusYears(30));

        when(repository.findByActiveTrueAndIcdCodesContainingAndNdcCodesContaining(
                request.getIcdCode(), request.getMedicationNdcCode()))
                .thenReturn(Arrays.asList(testRule));

        DiagnosisResponse result = service.getRecommendation(request);

        assertEquals(testRule.getRecommendation(), result.getRecommendation());
        assertEquals(testRule.getConfidenceScore(), result.getConfidenceScore());
        assertEquals(testRule.getId(), result.getMatchedRuleId());
    }

    @Test
    void getRecommendation_WhenNoMatchingRule_ShouldReturnDefaultResponse() {
        DiagnosisRequest request = new DiagnosisRequest();
        request.setIcdCode("E11.9");
        request.setMedicationNdcCode("0002-8215-01");
        request.setPatientDob(LocalDate.now().minusYears(30));

        when(repository.findByActiveTrueAndIcdCodesContainingAndNdcCodesContaining(
                request.getIcdCode(), request.getMedicationNdcCode()))
                .thenReturn(Arrays.asList());

        DiagnosisResponse result = service.getRecommendation(request);

        assertEquals("No matching diagnosis rule found", result.getRecommendation());
        assertEquals(0.0, result.getConfidenceScore());
        assertNull(result.getMatchedRuleId());
    }

    @Test
    void getRecommendation_WhenAgeOutOfRange_ShouldReturnDefaultResponse() {
        DiagnosisRequest request = new DiagnosisRequest();
        request.setIcdCode("E11.9");
        request.setMedicationNdcCode("0002-8215-01");
        request.setPatientDob(LocalDate.now().minusYears(70)); // Age outside range

        when(repository.findByActiveTrueAndIcdCodesContainingAndNdcCodesContaining(
                request.getIcdCode(), request.getMedicationNdcCode()))
                .thenReturn(Arrays.asList(testRule));

        DiagnosisResponse result = service.getRecommendation(request);

        assertEquals("No matching diagnosis rule found", result.getRecommendation());
        assertEquals(0.0, result.getConfidenceScore());
        assertNull(result.getMatchedRuleId());
    }
} 