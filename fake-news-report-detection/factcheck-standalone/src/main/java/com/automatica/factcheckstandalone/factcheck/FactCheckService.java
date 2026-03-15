package com.automatica.factcheckstandalone.factcheck;

import com.automatica.factcheckstandalone.dto.GoogleResponse;
import com.automatica.factcheckstandalone.kafka.dto.FactCheckRequestMessage;
import com.automatica.factcheckstandalone.kafka.dto.FactCheckResponseMessage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

@Service
public class FactCheckService {

    private final GoogleFactCheckClient google;
    private final GeminiClient gemini;

    public FactCheckService(GoogleFactCheckClient google, GeminiClient gemini) {
        this.google = google;
        this.gemini = gemini;
    }

    public FactCheckResponseMessage process(FactCheckRequestMessage message) {
        try {
            String contentToCheck = normalizeText(message.getText());

            GoogleResponse response = google.searchClaims(contentToCheck);

            if (response == null || response.claims == null || response.claims.isEmpty()) {
                var ai = gemini.assessClaim(contentToCheck);

                return new FactCheckResponseMessage(message.getRequestId(), message.getReportId(), "DONE", ai.getVerdict(), ai.getConfidence(), "GEMINI", null, null, ai.getRationale(), ai.getWhatToVerify(), null);
            }

            var claim = response.claims.get(0);

            if (claim.claimReview == null || claim.claimReview.isEmpty()) {
                var ai = gemini.assessClaim(claim.text);

                return new FactCheckResponseMessage(message.getRequestId(), message.getReportId(), "DONE", ai.getVerdict(), ai.getConfidence(), "GEMINI", null, null, ai.getRationale(), ai.getWhatToVerify(), null);
            }

            var review = claim.claimReview.get(0);

            String verdict = review.textualRating;
            String url = review.url;
            String publisher = (review.publisher != null) ? review.publisher.name : null;

            return new FactCheckResponseMessage(message.getRequestId(), message.getReportId(), "DONE", verdict, null,
                    "GOOGLE_FACTCHECK", publisher, url, null, null, null);

        } catch (Exception e) {
            return new FactCheckResponseMessage(message.getRequestId(), message.getReportId(), "FAILED", null, null, "WORKER", null, null, null, null, e.getMessage());
        }
    }

    private String normalizeText(String text) {
        if (text == null) return "";

        //daca e url
        if (text.startsWith("http://") || text.startsWith("https://")) {
            try {
                Document doc = Jsoup.connect(text)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                        .timeout(10000)
                        .followRedirects(true)
                        .get();

                String extracted = doc.body().text();

                if (extracted.length() > 2000) {
                    extracted = extracted.substring(0, 2000);
                }

                return extracted;

            } catch (Exception e) {
                return text;
            }
        }

        return text;
    }
}

