package com.automatica.factcheckstandalone.controller;

public class FactCheckResponse {
    private boolean found;
    private String claim;
    private String verdict;
    private String publisher;
    private String url;

    //gemini
    private String aiVerdict;
    private Double aiConfidence;
    private String aiWhatToVerify;
    private String aiRationale;


    public FactCheckResponse(boolean found, String claim, String verdict, String publisher, String url,
                             String aiVerdict, Double aiConfidence, String aiWhatToVerify, String aiRationale) {
        this.found = found;
        this.claim = claim;
        this.verdict = verdict;
        this.publisher = publisher;
        this.url = url;
        this.aiVerdict = aiVerdict;
        this.aiConfidence = aiConfidence;
        this.aiWhatToVerify = aiWhatToVerify;
        this.aiRationale = aiRationale;

    }

    public FactCheckResponse(boolean found, String claim, String verdict, String publisher, String url) {
        this(found, claim, verdict, publisher, url, null, null, null, null);
    }

    public boolean isFound() {
        return found;
    }

    public String getClaim() {
        return claim;
    }

    public String getVerdict() {
        return verdict;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getUrl() {
        return url;
    }

    public String getAiVerdict() {
        return aiVerdict;
    }

    public Double getAiConfidence() {
        return aiConfidence;
    }

    public String getAiWhatToVerify() {
        return aiWhatToVerify;
    }

    public String getAiRationale() {
        return aiRationale;
    }


}
