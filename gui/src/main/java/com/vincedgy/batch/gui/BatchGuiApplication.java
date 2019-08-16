package com.vincedgy.batch.gui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BatchGuiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BatchGuiApplication.class, args);
    }

}
