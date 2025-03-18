package com.medical.repository;

import com.medical.model.DiagnosisRule;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiagnosisRuleRepository extends ElasticsearchRepository<DiagnosisRule, String> {
    List<DiagnosisRule> findByActiveTrueAndIcdCodesContainingAndNdcCodesContaining(
            String icdCode, String ndcCode);
} 