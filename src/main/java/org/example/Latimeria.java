package org.example;

import org.example.configuration.ConfigSaver;
import org.example.reader.DocumentReader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class Latimeria {

    private static final String CONFIG_FILE = "config.json";

    public static void main(String[] args) {
        System.setProperty("java.util.logging.config.file", "src/main/resources/logging.properties");

        Map<String, String> config = null;

        try {
            // Спроба завантажити конфігурацію з файлу JSON
            config = ConfigSaver.loadConfig(CONFIG_FILE);
        } catch (IOException e) {
            // Якщо немає файлу конфігурації, перевіряємо аргументи
            if (args.length >= 8) {
                // Створюємо нову конфігурацію з аргументів командного рядка
                //classifier_model.dat classifier_model_tip.dat http://192.168.51.131:9001 admin 27Zeynalov 192.168.51.131 9200 http

                config = new HashMap<>();
                config.put("classifier.model", args[0]);         // Перший аргумент: classifier_model
                config.put("classifier.model_tip", args[1]);  // Другий аргумент: classifier_model_tip
                config.put("minio.url", args[2]);         // Перший аргумент: URL
                config.put("minio.access-key", args[3]);  // Другий аргумент: access key
                config.put("minio.secret-key", args[4]);  // Третій аргумент: secret key
                config.put("elc.host", args[5]);         // Перший аргумент: URL
                config.put("elc.port", args[6]);  // Другий аргумент: access key
                config.put("elc.scheme", args[7]);  // Третій аргумент: secret key
                try {
                    // Зберігаємо конфігурацію у JSON для майбутніх запусків
                    ConfigSaver.saveConfig(config, CONFIG_FILE);
                    System.err.println("Конфігурація збережена: " + config);

                } catch (IOException ex) {
                    System.err.println("Не вдалося зберегти конфігурацію: " + ex.getMessage());
                    return;
                }
            } else {
                System.err.println("Не вказані необхідні параметри під час запуску програми.");
                return;
            }
        }

        // Перетворюємо Map<String, String> на Map<String, Object>
        Map<String, Object> properties = new HashMap<>(config);

        // Налаштовуємо Spring з параметрами конфігурації
        SpringApplication app = new SpringApplication(Latimeria.class);
        app.setDefaultProperties(properties);
        app.run(args);
    }
}

