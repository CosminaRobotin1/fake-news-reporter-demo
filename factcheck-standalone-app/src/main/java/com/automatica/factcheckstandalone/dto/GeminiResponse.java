package com.automatica.factcheckstandalone.dto;

public class GeminiResponse {

    private String verdict;
    private Double confidence;
    private String whatToVerify;
    private String rationale;

    public GeminiResponse(String verdict, Double confidence, String whatToVerify, String rationale) {
        this.verdict = verdict;
        this.confidence = confidence;
        this.whatToVerify = whatToVerify;
        this.rationale = rationale;
    }

    public String getVerdict() {
        return verdict;
    }

    public Double getConfidence() {
        return confidence;
    }

    public String getWhatToVerify() {
        return whatToVerify;
    }

    public String getRationale() {
        return rationale;
    }
}
