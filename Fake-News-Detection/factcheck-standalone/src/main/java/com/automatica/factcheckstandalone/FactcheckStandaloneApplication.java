package com.automatica.factcheckstandalone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class FactcheckStandaloneApplication {

    public static void main(String[] args) {
        SpringApplication.run(FactcheckStandaloneApplication.class, args);
    }

}
