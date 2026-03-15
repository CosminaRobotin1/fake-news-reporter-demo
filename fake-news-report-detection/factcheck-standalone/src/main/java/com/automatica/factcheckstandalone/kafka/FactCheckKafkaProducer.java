package com.automatica.factcheckstandalone.kafka;


import com.automatica.factcheckstandalone.kafka.dto.FactCheckResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FactCheckKafkaProducer {

    private final KafkaTemplate<String, FactCheckResponseMessage> kafkaTemplate;

    @Value("${app.kafka.topics.responses}")
    private String topic;

    public void send(FactCheckResponseMessage message) {
        kafkaTemplate.send(topic, message.getReportId().toString(), message);
    }

}
