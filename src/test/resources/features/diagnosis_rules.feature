Feature: Diagnosis Rules Management
  As a healthcare provider
  I want to manage diagnosis rules
  So that I can provide accurate diagnosis recommendations

  Background:
    Given the Elasticsearch server is running
    And the diagnosis rules index is empty

  Scenario: Create and retrieve a diagnosis rule
    When I create a diagnosis rule with the following details:
      | icdCode     | ndcCode      | minAge | maxAge | recommendation      | confidenceScore |
      | E11.9       | 0002-8215-01 | 18     | 65     | Test recommendation| 0.85           |
    Then the rule should be successfully created
    And I should be able to retrieve the rule by its ID
    And the retrieved rule should have the following details:
      | icdCode     | ndcCode      | minAge | maxAge | recommendation      | confidenceScore |
      | E11.9       | 0002-8215-01 | 18     | 65     | Test recommendation| 0.85           |

  Scenario: Get recommendation for a patient
    Given a diagnosis rule exists with the following details:
      | icdCode     | ndcCode      | minAge | maxAge | recommendation      | confidenceScore |
      | E11.9       | 0002-8215-01 | 18     | 65     | Test recommendation| 0.85           |
    When I request a recommendation with:
      | icdCode     | ndcCode      | patientAge |
      | E11.9       | 0002-8215-01 | 30        |
    Then I should receive a recommendation with:
      | recommendation      | confidenceScore |
      | Test recommendation| 0.85           | 