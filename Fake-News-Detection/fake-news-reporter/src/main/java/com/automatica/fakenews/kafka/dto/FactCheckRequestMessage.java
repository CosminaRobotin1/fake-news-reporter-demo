package com.automatica.fakenews.kafka.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
// Mesaj Kafka folosit pentru trimiterea unei cereri de fact-check
// de la FakeNewsReporter catre FactCheckStandalone.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FactCheckRequestMessage {

    private String requestId;
    private Long reportId;
    private String text;


}
