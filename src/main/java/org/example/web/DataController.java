package org.example.web;

import org.example.manager.MinioManager;
import org.example.nlp.ClassifierModel;
import org.example.reader.Reader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class DataController {
    private final ClassifierModel classifierModel;
    MinioManager minioManager;
    // Інжектимо бін ClassifierModel через конструктор
    @Autowired
    public DataController(ClassifierModel classifierModel,MinioManager minioManager) {
        this.classifierModel = classifierModel;
        this.minioManager = minioManager;
    }

    @PostMapping("/HostInfo")
    public ResponseEntity<String> receiveData(@RequestBody HostInfo hostInfo) throws IOException, InterruptedException, ClassNotFoundException {

        // Отримання  URL
        String resignedUrl = minioManager.getPresignedUrl(hostInfo.getUrlFile());
        if (resignedUrl != null) {
            // Завантажуємо файл
            String file = minioManager.downloadFile(resignedUrl);
            // Якщо файл існує і не порожній, починаємо обробку
            Reader reader = new Reader(classifierModel,minioManager, hostInfo.getUrlFile(), hostInfo, file);
            reader.startReading();
        }
        return new ResponseEntity<>("Дані успішно отримано!", HttpStatus.OK);
    }
}

