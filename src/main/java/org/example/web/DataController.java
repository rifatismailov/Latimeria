package org.example.web;

import org.example.service.MinioManager;
import org.example.nlp.ClassifierModel;
import org.example.reader.Reader;
import org.example.service.ElasticSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Контролер для обробки HTTP-запитів на отримання даних.
 */
@RestController
@RequestMapping("/api")
public class DataController {
    private final ClassifierModel classifierModel;
    private final MinioManager minioManager;
    private final ElasticSender elasticSender; // Додаємо Sender
    private String key_phrases;

    /**
     * Конструктор контролера для інжекції залежностей.
     *
     * @param classifierModel модель класифікації.
     * @param minioManager менеджер для роботи з MinIO.
     * @param elasticSender менеджер для відправки даних в Elasticsearch.
     */
    @Autowired
    public DataController(ClassifierModel classifierModel, MinioManager minioManager, ElasticSender elasticSender,String key_phrases) {
        this.classifierModel = classifierModel;
        this.minioManager = minioManager;
        this.elasticSender = elasticSender; // Інжектуємо Sender
        this.key_phrases = key_phrases;
    }

    /**
     * Метод для отримання даних від клієнта.
     *
     * @param hostInfo об'єкт, що містить інформацію про хост.
     * @return ResponseEntity з повідомленням про успішне отримання даних.
     * @throws IOException у разі проблем з введенням/виведенням.
     * @throws InterruptedException у разі переривання потоку.
     * @throws ClassNotFoundException у разі проблем із завантаженням класів.
     */
    @PostMapping("/HostInfo")
    public ResponseEntity<String> receiveData(@RequestBody HostInfo hostInfo){
        // Отримання URL для завантаження файлу
        String resignedUrl = minioManager.getPresignedUrl(hostInfo.getUrlFile());
        if (resignedUrl != null) {
            // Завантажуємо файл за отриманим URL
            String file = minioManager.downloadFile(resignedUrl);
            // Якщо файл існує і не порожній, починаємо обробку
            Reader reader = new Reader(classifierModel, minioManager, hostInfo.getUrlFile(), hostInfo, file, elasticSender,key_phrases);
            reader.startReading();
        }
        return new ResponseEntity<>("Дані успішно отримано!", HttpStatus.OK);
    }
}
