package com.automatica.factcheckstandalone.factcheck;

import com.automatica.factcheckstandalone.dto.GeminiResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;
// Client responsabil cu comunicarea către Gemini AI.
@Component
public class GeminiClient { //comunica cu serviciul extern Gemini

    private final RestClient restClient; //ob care trimite request uri HTTP
    private final ObjectMapper mapper;

    //ia val din app.properties
    public GeminiClient(@Value("${gemini.api-key}") String apiKey, @Value("${gemini.model:gemini-3-pro-preview}") String model, ObjectMapper mapper) {
        this.restClient = RestClient.builder().baseUrl("https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey).build();
        this.mapper = mapper;
    }

    public GeminiResponse assessClaim(String text) {

        String prompt = """
                You are a fact-check assistant.
                
                Estimate if the claim is true or false using general knowledge.
                Do NOT invent sources. If unsure, use "uncertain".
                
                Return ONLY JSON:
                {
                  "verdict": "likely_true" | "likely_false" | "uncertain",
                  "confidence": 0.0-1.0,
                  "whatToVerify": "short checklist",
                  "rationale": "brief explanation (2-4 sentences)"
                }
                
                Claim:
                """ + text;

        Map<String, Object> body = Map.of("contents", new Object[]{Map.of("parts", new Object[]{Map.of("text", prompt)})});//constructie json cerunt de api ul gemini

        try {
            String response = restClient.post().body(body).retrieve().body(String.class);//se trimite requestul

            JsonNode root = mapper.readTree(response);//transf textul in json

            String modelText = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();//extrage rasp generat de gemini

            JsonNode ai = mapper.readTree(extractJson(modelText));//extrage json ul din text 

            return new GeminiResponse(ai.path("verdict").asText("uncertain"), ai.path("confidence").asDouble(0.3), ai.path("whatToVerify").asText("Check credible sources / official statements."), ai.path("rationale").asText("Uncertain based on limited information."));

        } catch (Exception e) {
            return new GeminiResponse("uncertain", 0.2, "Look for official sources and credible reporting.", "AI analysis failed: " + e.getMessage());
        }
    }

    private String extractJson(String text) {
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return text.substring(start, end + 1);
        }
        return "{}";
    }

}
