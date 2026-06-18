package com.automatica.fakenews.consumer;

import com.automatica.fakenews.kafka.dto.FactCheckRequestMessage;
import com.automatica.fakenews.kafka.dto.FactCheckResponseMessage;
import com.automatica.fakenews.model.FakeNewsReport;
import com.automatica.fakenews.service.FakeNewsReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
// Consumer Kafka care primește rezultatele verificării de la
// FactCheckStandalone și actualizează raportarea din baza de date.
@Component
@RequiredArgsConstructor
public class FactCheckKafkaConsumer {

    // Service-ul care știe să actualizeze raportările în baza de date
    private final FakeNewsReportService fakeNewsReportService;

    @KafkaListener(//Kafka ascultă factcheck.responses
            topics = "${app.kafka.topics.responses}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    // Când apare un mesaj nou, metoda consume() este apelată automat
    public void consume(FactCheckResponseMessage message) {
        System.out.println("RECEIVED RESPONSE: " + message);
        fakeNewsReportService.applyFactCheckResult(message);//Actualizarea bazei de date
    }
}

