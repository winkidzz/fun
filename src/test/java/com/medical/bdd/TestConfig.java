package com.medical.bdd;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.time.Duration;

@Configuration
@EnableAutoConfiguration(exclude = {ElasticsearchRestClientAutoConfiguration.class})
@EnableElasticsearchRepositories(basePackages = "com.medical.repository")
public class TestConfig {

    @Bean
    @Primary
    public ElasticsearchOperations elasticsearchOperations() {
        // Connect to Elasticsearch through port forwarding
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
            .connectedTo("localhost:9200")
            .withConnectTimeout(Duration.ofSeconds(5))
            .withSocketTimeout(Duration.ofSeconds(3))
            .build();

        RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200, "http")).build();
        ElasticsearchClient elasticsearchClient = new ElasticsearchClient(
            new RestClientTransport(restClient, new JacksonJsonpMapper())
        );

        return new ElasticsearchTemplate(elasticsearchClient);
    }
} 