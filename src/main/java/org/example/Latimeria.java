package org.example;

import org.example.reader.DocumentReader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class Latimeria {
    public static void main(String[] args) {
        System.setProperty("java.util.logging.config.file", "src/main/resources/logging.properties");
        SpringApplication.run(Latimeria.class, args);

    }
}
