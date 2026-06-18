package com.automatica.fakenews.producer;

import com.automatica.fakenews.kafka.dto.FactCheckRequestMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
// Producer Kafka care trimite cereri de fact-check
// catre aplicatia FactCheckStandalone.
@Service
@RequiredArgsConstructor
public class FactCheckKafkaProducer {

    // Obiectul folosit pentru trimiterea mesajelor catre Kafka
    private final KafkaTemplate<String, FactCheckRequestMessage> kafkaTemplate;
    // Citeste numele topicului din application.yml
    @Value("${app.kafka.topics.requests}")
    private String topic;

    // Trimite o cerere de fact-check catre Kafka
    public void send(FactCheckRequestMessage message) {
        kafkaTemplate.send(topic, message.getReportId().toString(), message);
        System.out.println(" Sent to Kafka: " + message);
    }
}
