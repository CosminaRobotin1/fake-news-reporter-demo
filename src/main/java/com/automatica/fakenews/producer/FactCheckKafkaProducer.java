package com.automatica.fakenews.producer;

import com.automatica.fakenews.kafka.dto.FactCheckRequestMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FactCheckKafkaProducer {

    private final KafkaTemplate<String, FactCheckRequestMessage> kafkaTemplate;
    @Value("${app.kafka.topics.requests}")
    private String topic;

    public void send(FactCheckRequestMessage message) {
        kafkaTemplate.send(topic, message.getReportId().toString(), message);
        System.out.println(" Sent to Kafka: " + message);
    }
}
