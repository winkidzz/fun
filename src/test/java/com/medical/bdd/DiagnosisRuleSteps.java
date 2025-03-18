package com.medical.bdd;

import com.medical.dto.DiagnosisRequest;
import com.medical.dto.DiagnosisResponse;
import com.medical.model.DiagnosisRule;
import com.medical.repository.DiagnosisRuleRepository;
import com.medical.service.DiagnosisRuleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class DiagnosisRuleSteps {

    @Mock
    private DiagnosisRuleRepository repository;

    @Mock
    private ObjectMapper objectMapper;

    private DiagnosisRuleService service;
    private DiagnosisRule createdRule;
    private DiagnosisResponse recommendation;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new DiagnosisRuleService(repository, objectMapper);
        
        // Default mock behavior
        when(repository.save(any(DiagnosisRule.class))).thenAnswer(invocation -> {
            DiagnosisRule rule = invocation.getArgument(0);
            rule.setId("test-id");
            return rule;
        });
    }

    @Given("the Elasticsearch server is running")
    public void theElasticsearchServerIsRunning() {
        // No need to check actual server with mocks
    }

    @Given("the diagnosis rules index is empty")
    public void theDiagnosisRulesIndexIsEmpty() {
        // Clear any existing mock responses
        reset(repository);
    }

    @When("I create a diagnosis rule with the following details:")
    public void iCreateADiagnosisRuleWithTheFollowingDetails(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        Map<String, String> data = rows.get(0);

        DiagnosisRule rule = new DiagnosisRule();
        rule.setIcdCodes(Arrays.asList(data.get("icdCode")));
        rule.setNdcCodes(Arrays.asList(data.get("ndcCode")));
        rule.setMinAge(Integer.parseInt(data.get("minAge")));
        rule.setMaxAge(Integer.parseInt(data.get("maxAge")));
        rule.setRecommendation(data.get("recommendation"));
        rule.setConfidenceScore(Double.parseDouble(data.get("confidenceScore")));
        rule.setActive(true);

        createdRule = service.createRule(rule);
    }

    @Then("the rule should be successfully created")
    public void theRuleShouldBeSuccessfullyCreated() {
        assertNotNull(createdRule);
        assertNotNull(createdRule.getId());
        verify(repository).save(any(DiagnosisRule.class));
    }

    @Then("I should be able to retrieve the rule by its ID")
    public void iShouldBeAbleToRetrieveTheRuleByItsId() {
        when(repository.findById(createdRule.getId())).thenReturn(Optional.of(createdRule));
        
        DiagnosisRule retrievedRule = service.getRule(createdRule.getId());
        assertNotNull(retrievedRule);
        assertEquals(createdRule.getId(), retrievedRule.getId());
        verify(repository).findById(createdRule.getId());
    }

    @Then("the retrieved rule should have the following details:")
    public void theRetrievedRuleShouldHaveTheFollowingDetails(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        Map<String, String> expectedData = rows.get(0);

        when(repository.findById(createdRule.getId())).thenReturn(Optional.of(createdRule));
        
        DiagnosisRule retrievedRule = service.getRule(createdRule.getId());
        assertEquals(expectedData.get("icdCode"), retrievedRule.getIcdCodes().get(0));
        assertEquals(expectedData.get("ndcCode"), retrievedRule.getNdcCodes().get(0));
        assertEquals(Integer.parseInt(expectedData.get("minAge")), retrievedRule.getMinAge());
        assertEquals(Integer.parseInt(expectedData.get("maxAge")), retrievedRule.getMaxAge());
        assertEquals(expectedData.get("recommendation"), retrievedRule.getRecommendation());
        assertEquals(Double.parseDouble(expectedData.get("confidenceScore")), retrievedRule.getConfidenceScore());
    }

    @Given("a diagnosis rule exists with the following details:")
    public void aDiagnosisRuleExistsWithTheFollowingDetails(DataTable dataTable) {
        iCreateADiagnosisRuleWithTheFollowingDetails(dataTable);
        
        // Set up mock for findByActiveTrueAndIcdCodesContainingAndNdcCodesContaining
        when(repository.findByActiveTrueAndIcdCodesContainingAndNdcCodesContaining(
            createdRule.getIcdCodes().get(0), 
            createdRule.getNdcCodes().get(0)
        )).thenReturn(Arrays.asList(createdRule));
    }

    @When("I request a recommendation with:")
    public void iRequestARecommendationWith(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        Map<String, String> data = rows.get(0);

        DiagnosisRequest request = new DiagnosisRequest();
        request.setIcdCode(data.get("icdCode"));
        request.setMedicationNdcCode(data.get("ndcCode"));
        request.setPatientDob(LocalDate.now().minusYears(Integer.parseInt(data.get("patientAge"))));

        recommendation = service.getRecommendation(request);
    }

    @Then("I should receive a recommendation with:")
    public void iShouldReceiveARecommendationWith(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        Map<String, String> expectedData = rows.get(0);

        assertNotNull(recommendation);
        assertEquals(expectedData.get("recommendation"), recommendation.getRecommendation());
        assertEquals(Double.parseDouble(expectedData.get("confidenceScore")), recommendation.getConfidenceScore());
    }
} 