package com.automatica.fakenews.dto;
// DTO folosit pentru preluarea datelor introduse de utilizator
import jakarta.validation.constraints.NotBlank;
//cand utilizatorul completeaza reportul spring pune val intr un reportform

public class ReportForm {

    @NotBlank(message = "News source is required")
    private String newsSource;

    @NotBlank(message = "URL is required")
    private String url;

    @NotBlank(message = "Category is required")
    private String category;

    private String description;

    public String getNewsSource() {
        return newsSource;
    }

    public void setNewsSource(String newsSource) {
        this.newsSource = newsSource;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
