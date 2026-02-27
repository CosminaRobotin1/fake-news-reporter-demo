package com.automatica.fakenews.kafka.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FactCheckRequestMessage {

    private String requestId;
    private Long reportId;
    private String text;


}
