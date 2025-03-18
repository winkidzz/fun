package com.medical.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Data
@Document(indexName = "diagnosis_rules")
public class DiagnosisRule {
    @Id
    private String id;
    
    @Field(type = FieldType.Keyword)
    private List<String> icdCodes;
    
    @Field(type = FieldType.Integer)
    private Integer minAge;
    
    @Field(type = FieldType.Integer)
    private Integer maxAge;
    
    @Field(type = FieldType.Keyword)
    private List<String> ndcCodes;
    
    @Field(type = FieldType.Text)
    private String recommendation;
    
    @Field(type = FieldType.Double)
    private Double confidenceScore;
    
    @Field(type = FieldType.Boolean)
    private Boolean active;
} 