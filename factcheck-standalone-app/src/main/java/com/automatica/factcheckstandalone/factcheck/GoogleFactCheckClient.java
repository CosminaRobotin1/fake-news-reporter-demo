package com.automatica.factcheckstandalone.factcheck;

import com.automatica.factcheckstandalone.dto.GoogleResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
public class GoogleFactCheckClient {

    private final RestClient restClient = RestClient.create();

    @Value("${factcheck.base-url}")
    private String baseUrl;

    @Value("${factcheck.api-key}")
    private String apiKey;

    public GoogleResponse searchClaims(String query) {
        String url = UriComponentsBuilder
                .fromUriString(baseUrl)
                .queryParam("query", query)
                .queryParam("key", apiKey)
                .build()
                .toUriString();


        return restClient.get()
                .uri(url)
                .retrieve()
                .body(GoogleResponse.class);
    }
}

