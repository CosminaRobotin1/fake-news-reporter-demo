package com.automatica.fakenews.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class FactCheckResponseMessage {

    private String requestId;
    private Long reportId;

    private String status;
    private String verdict;
    private Double confidence;

    private String provider;
    private String publisher;
    private String url;

    private String rationale;
    private String whatToVerify;

    private String errorMessage;
}
