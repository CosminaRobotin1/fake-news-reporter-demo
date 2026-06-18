package com.automatica.factcheckstandalone.dto;

public class PythonFactCheckRequest {

    private String requestId;
    private Long reportId;
    private String text;

    public PythonFactCheckRequest() {
    }

    public PythonFactCheckRequest(String requestId, Long reportId, String text) {
        this.requestId = requestId;
        this.reportId = reportId;
        this.text = text;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

