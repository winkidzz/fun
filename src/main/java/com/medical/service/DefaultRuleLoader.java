package com.medical.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.model.DiagnosisRule;
import com.medical.repository.DiagnosisRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class DefaultRuleLoader implements CommandLineRunner {

    @Autowired
    private DiagnosisRuleRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        // Check if rules already exist
        if (repository.count() == 0) {
            loadDefaultRules();
        }
    }

    private void loadDefaultRules() throws IOException {
        ClassPathResource resource = new ClassPathResource("default-rules.json");
        DefaultRules defaultRules = objectMapper.readValue(resource.getInputStream(), DefaultRules.class);
        
        for (DiagnosisRule rule : defaultRules.getRules()) {
            repository.save(rule);
        }
    }

    private static class DefaultRules {
        private List<DiagnosisRule> rules;

        public List<DiagnosisRule> getRules() {
            return rules;
        }

        public void setRules(List<DiagnosisRule> rules) {
            this.rules = rules;
        }
    }
} 