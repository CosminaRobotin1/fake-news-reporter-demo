package com.automatica.factcheckstandalone.factcheck;

import com.automatica.factcheckstandalone.dto.PythonFactCheckRequest;
import com.automatica.factcheckstandalone.dto.PythonFactCheckResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Component  //trimite request la serviciul Python
public class PythonFactCheckClient {

    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public PythonFactCheckClient(@Value("${python.factcheck.base-url}") String baseUrl,
                                 ObjectMapper objectMapper) {
        this.baseUrl = baseUrl;
        this.objectMapper = objectMapper;
    }

    //primește request-ul și întoarce răspunsul de la serviciul Python
    public PythonFactCheckResponse check(PythonFactCheckRequest request) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(request);

        System.out.println("=== PYTHON CLIENT HTTPURLCONNECTION ACTIVE ===");
        System.out.println("PYTHON REQUEST JSON = " + jsonBody);

        URL url = new URL(baseUrl + "/factcheck");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");

        byte[] out = jsonBody.getBytes(StandardCharsets.UTF_8);
        conn.setFixedLengthStreamingMode(out.length);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(out);
            os.flush();
        }

        int status = conn.getResponseCode();
        System.out.println("PYTHON RESPONSE STATUS = " + status);

        InputStream is = (status >= 400) ? conn.getErrorStream() : conn.getInputStream();
        String responseBody;

        if (is != null) {
            responseBody = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } else {
            responseBody = "";
        }

        System.out.println("PYTHON RESPONSE BODY = " + responseBody);

        if (status >= 400) {
            throw new RuntimeException("Python service error: " + status + " - " + responseBody);
        }

        return objectMapper.readValue(responseBody, PythonFactCheckResponse.class);
    }
}