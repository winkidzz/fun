package com.medical.bdd;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.context.annotation.Import;

@CucumberContextConfiguration
@SpringBootTest(properties = {
    "spring.elasticsearch.rest.uris=http://localhost:9200"
})
@Import(TestConfig.class)
public class CucumberSpringConfiguration {
    
    @MockBean
    private ElasticsearchOperations elasticsearchOperations;
    
    @MockBean
    private ElasticsearchTemplate elasticsearchTemplate;
} 