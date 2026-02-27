package com.automatica.factcheckstandalone.controller;



import com.automatica.factcheckstandalone.dto.GoogleResponse;
import com.automatica.factcheckstandalone.factcheck.GeminiClient;
import com.automatica.factcheckstandalone.factcheck.GoogleFactCheckClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
public class FactCheckController {

    private static final Logger log = LoggerFactory.getLogger(FactCheckController.class);

    private final GoogleFactCheckClient client;
    private final GeminiClient gemini;
    private final ObjectMapper mapper;

    public FactCheckController(GoogleFactCheckClient client, GeminiClient gemini, ObjectMapper mapper) {
        this.client = client;
        this.gemini = gemini;
        this.mapper = mapper;
    }

    private FactCheckResponse respond(FactCheckResponse response) {
        try {
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
            log.info("\n===== FACT CHECK RESPONSE =====\n{}\n===============================", json);
        } catch (JsonProcessingException e) {
            log.error("Nu pot serializa raspunsul in JSON", e);
        }
        return response;
    }

    @GetMapping("/api/factcheck")
    public FactCheckResponse factCheck(@RequestParam String text) {

        String contentToCheck = text;

        if (text.startsWith("http://") || text.startsWith("https://")) {
            try {
                Document doc = Jsoup.connect(text).get();
                contentToCheck = doc.body().text();

                if (contentToCheck.length() > 2000) {
                    contentToCheck = contentToCheck.substring(0, 2000);
                }

            } catch (IOException e) {
                return respond(new FactCheckResponse(false, null, "Nu am putut citi link-ul", null, text));
            }
        }

        GoogleResponse response = client.searchClaims(contentToCheck);


        if (response == null || response.claims == null || response.claims.isEmpty()) {
            var ai = gemini.assessClaim(contentToCheck);
            return respond(new FactCheckResponse(false, contentToCheck, null, null, null, ai.getVerdict(), ai.getConfidence(), ai.getWhatToVerify(), ai.getRationale()));
        }

        var claim = response.claims.get(0);

        if (claim.claimReview == null || claim.claimReview.isEmpty()) {
            var ai = gemini.assessClaim(claim.text);
            return respond(new FactCheckResponse(true, claim.text, null, null, null, ai.getVerdict(), ai.getConfidence(), ai.getWhatToVerify(), ai.getRationale()));
        }

        var review = claim.claimReview.get(0);

        String verdict = review.textualRating;
        String url = review.url;
        String publisher = (review.publisher != null) ? review.publisher.name : null;

        return respond(new FactCheckResponse(true, claim.text, verdict, publisher, url));
    }

}
