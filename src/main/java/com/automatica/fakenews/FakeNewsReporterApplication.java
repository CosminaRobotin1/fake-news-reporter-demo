package com.automatica.fakenews;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;


@SpringBootApplication
@EnableKafka
public class FakeNewsReporterApplication {

    public static void main(String[] args) {
        SpringApplication.run(FakeNewsReporterApplication.class, args);
    }
}
 //Aplicația mare:
//produce request
//consumă response
//Aplicația mică:
//consumă request
//produce response