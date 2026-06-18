package com.automatica.factcheckstandalone.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//Cererea pe care FakeNewsReporter o trimite către FactCheckStandalone
public class FactCheckRequestMessage {

    private String requestId;
    private Long reportId;
    private String text;
}
