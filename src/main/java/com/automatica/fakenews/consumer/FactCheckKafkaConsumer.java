package com.automatica.fakenews.consumer;

import com.automatica.fakenews.kafka.dto.FactCheckRequestMessage;
import com.automatica.fakenews.kafka.dto.FactCheckResponseMessage;
import com.automatica.fakenews.model.FakeNewsReport;
import com.automatica.fakenews.service.FakeNewsReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FactCheckKafkaConsumer {

    private final FakeNewsReportService fakeNewsReportService;

    @KafkaListener(topics = "${app.kafka.topics.responses}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(FactCheckResponseMessage message) {
        System.out.println("RECEIVED RESPONSE: " + message);
        fakeNewsReportService.applyFactCheckResult(message);
    }
}
