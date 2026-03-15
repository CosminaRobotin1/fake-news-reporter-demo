package com.automatica.factcheckstandalone.kafka;

import com.automatica.factcheckstandalone.factcheck.FactCheckService;
import com.automatica.factcheckstandalone.kafka.dto.FactCheckRequestMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class FactCheckKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(FactCheckKafkaConsumer.class);
    public final FactCheckKafkaProducer producer;
    private final FactCheckService factCheckService;

    public FactCheckKafkaConsumer(FactCheckService factCheckService, FactCheckKafkaProducer producer) {
        this.factCheckService = factCheckService;
        this.producer = producer;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.requests:factcheck.requests}",
            groupId = "${spring.kafka.consumer.group-id:factcheck-worker}"
    )

    public void onMessage(FactCheckRequestMessage msg) {
        log.info("Received Kafka request: requestId={}, reportId={}", msg.getRequestId(), msg.getReportId());

        var response = factCheckService.process(msg);

        producer.send(response);

        try {
            log.info("FACTCHECK RESULT JSON:\n{}", new ObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(response));
        } catch (Exception e) {
            log.warn("Could not print response as JSON", e);
        }
    }
}
