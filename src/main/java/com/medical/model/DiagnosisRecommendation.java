package com.medical.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "diagnosis_recommendations")
public class DiagnosisRecommendation {
    @Id
    private String id;
    
    @Field(type = FieldType.Keyword)
    private String icdCode;
    
    @Field(type = FieldType.Integer)
    private Integer patientAge;
    
    @Field(type = FieldType.Keyword)
    private String medicationNdcCode;
    
    @Field(type = FieldType.Text)
    private String recommendation;
    
    @Field(type = FieldType.Double)
    private Double confidenceScore;
} 