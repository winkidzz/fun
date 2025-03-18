package com.medical.integration;

import com.medical.dto.DiagnosisRequest;
import com.medical.dto.DiagnosisResponse;
import com.medical.model.DiagnosisRule;
import com.medical.repository.DiagnosisRuleRepository;
import com.medical.service.DiagnosisRuleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class DiagnosisRuleIntegrationTest {

    @Container
    static ElasticsearchContainer elasticsearchContainer = new ElasticsearchContainer(
            DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:8.12.2")
                    .asCompatibleSubstituteFor("elasticsearch:8.12.2"))
            .withExposedPorts(9200)
            .withEnv("discovery.type", "single-node")
            .withEnv("xpack.security.enabled", "false");

    @DynamicPropertySource
    static void elasticsearchProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.elasticsearch.rest.uris", elasticsearchContainer::getHttpHostAddress);
    }

    @Autowired
    private DiagnosisRuleRepository repository;

    @Autowired
    private DiagnosisRuleService service;

    private DiagnosisRule testRule;

    @BeforeEach
    void setUp() {
        // Clear existing data
        repository.deleteAll();

        // Create test rule
        testRule = new DiagnosisRule();
        testRule.setIcdCodes(Arrays.asList("E11.9", "F41.9"));
        testRule.setNdcCodes(Arrays.asList("0002-8215-01", "0002-8215-02"));
        testRule.setMinAge(18);
        testRule.setMaxAge(65);
        testRule.setRecommendation("Test recommendation");
        testRule.setConfidenceScore(0.85);
        testRule.setActive(true);
    }

    @Test
    void createRule_ShouldSaveToElasticsearch() {
        DiagnosisRule savedRule = service.createRule(testRule);

        assertNotNull(savedRule.getId());
        assertTrue(savedRule.getActive());
        
        DiagnosisRule retrievedRule = service.getRule(savedRule.getId());
        assertEquals(testRule.getIcdCodes(), retrievedRule.getIcdCodes());
        assertEquals(testRule.getNdcCodes(), retrievedRule.getNdcCodes());
        assertEquals(testRule.getRecommendation(), retrievedRule.getRecommendation());
    }

    @Test
    void updateRule_ShouldUpdateInElasticsearch() {
        DiagnosisRule savedRule = service.createRule(testRule);
        savedRule.setRecommendation("Updated recommendation");
        
        DiagnosisRule updatedRule = service.updateRule(savedRule.getId(), savedRule);
        
        assertEquals("Updated recommendation", updatedRule.getRecommendation());
        
        DiagnosisRule retrievedRule = service.getRule(savedRule.getId());
        assertEquals("Updated recommendation", retrievedRule.getRecommendation());
    }

    @Test
    void deleteRule_ShouldRemoveFromElasticsearch() {
        DiagnosisRule savedRule = service.createRule(testRule);
        
        service.deleteRule(savedRule.getId());
        
        assertThrows(RuntimeException.class, () -> service.getRule(savedRule.getId()));
    }

    @Test
    void getRecommendation_ShouldMatchRulesCorrectly() {
        // Create multiple rules
        DiagnosisRule rule1 = service.createRule(testRule);
        
        DiagnosisRule rule2 = new DiagnosisRule();
        rule2.setIcdCodes(Arrays.asList("E11.9"));
        rule2.setNdcCodes(Arrays.asList("0002-8215-01"));
        rule2.setMinAge(18);
        rule2.setMaxAge(65);
        rule2.setRecommendation("Alternative recommendation");
        rule2.setConfidenceScore(0.75);
        rule2.setActive(true);
        service.createRule(rule2);

        // Test with matching request
        DiagnosisRequest request = new DiagnosisRequest();
        request.setIcdCode("E11.9");
        request.setMedicationNdcCode("0002-8215-01");
        request.setPatientDob(LocalDate.now().minusYears(30));

        DiagnosisResponse response = service.getRecommendation(request);

        assertNotNull(response);
        assertEquals(rule1.getRecommendation(), response.getRecommendation());
        assertEquals(rule1.getConfidenceScore(), response.getConfidenceScore());
        assertEquals(rule1.getId(), response.getMatchedRuleId());
    }

    @Test
    void getRecommendation_WhenNoMatch_ShouldReturnDefaultResponse() {
        DiagnosisRequest request = new DiagnosisRequest();
        request.setIcdCode("E11.9");
        request.setMedicationNdcCode("0002-8215-01");
        request.setPatientDob(LocalDate.now().minusYears(30));

        DiagnosisResponse response = service.getRecommendation(request);

        assertNotNull(response);
        assertEquals("No matching diagnosis rule found", response.getRecommendation());
        assertEquals(0.0, response.getConfidenceScore());
        assertNull(response.getMatchedRuleId());
    }

    @Test
    void getRecommendation_WhenAgeOutOfRange_ShouldReturnDefaultResponse() {
        DiagnosisRule savedRule = service.createRule(testRule);

        DiagnosisRequest request = new DiagnosisRequest();
        request.setIcdCode("E11.9");
        request.setMedicationNdcCode("0002-8215-01");
        request.setPatientDob(LocalDate.now().minusYears(70)); // Age outside range

        DiagnosisResponse response = service.getRecommendation(request);

        assertNotNull(response);
        assertEquals("No matching diagnosis rule found", response.getRecommendation());
        assertEquals(0.0, response.getConfidenceScore());
        assertNull(response.getMatchedRuleId());
    }
} 