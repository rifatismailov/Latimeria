package org.example.reader;

import org.example.service.MinioManager;
import org.example.filemanagement.FileManager;
import org.example.nlp.ClassifierModel;
import org.example.nlp.TextClassifier;
import org.example.service.ElasticSender;
import org.example.web.HostInfo;
import org.example.web.Result;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Клас Reader відповідає за читання файлів, їх класифікацію та подальшу обробку.
 * Він працює в окремому потоці, здійснює класифікацію текстів з використанням моделей,
 * та видаляє файли після обробки.
 */

public class Reader implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(Reader.class.getName());
    private final MinioManager minioManager;
    private final String objectName;        // Назва об'єкта (файлу) в Minio
    private final HostInfo hostInfo;        // Інформація про хост, на якому працює процес
    private final String filePath;          // Локальний шлях до файлу для обробки
    private final TextClassifier mainClassifier;  // Основний класифікатор тексту
    private final TextClassifier tipClassifier;   // Додатковий класифікатор для "підказок"
    private final ElasticSender elasticSender; // Додаємо Sender
    private final String key_phrases;

    /**
     * Конструктор класу Reader.
     *
     * @param minioManager клієнт Minio для роботи з хмарним сховищем.
     * @param objectName   назва об'єкта (файлу) в бакеті Minio.
     * @param hostInfo     інформація про хост.
     * @param filePath     шлях до локального файлу для читання.
     * @param key_phrases
     * @throws IOException            у разі проблем з файлом або мережею.
     * @throws ClassNotFoundException у разі проблем із завантаженням класифікаторів.
     */

    public Reader(@NotNull
                  ClassifierModel classifierModel,
                  MinioManager minioManager,
                  String objectName,
                  HostInfo hostInfo,
                  String filePath,
                  ElasticSender elasticSender,
                  String key_phrases) {
        this.minioManager = minioManager;
        this.objectName = objectName;
        this.hostInfo = hostInfo;
        this.filePath = filePath;
        // Завантажуємо моделі для класифікації
        this.mainClassifier = classifierModel.getMainClassifier();
        this.tipClassifier = classifierModel.getTipClassifier();
        this.elasticSender = elasticSender; // Інжектуємо Sender
        this.key_phrases = key_phrases;
    }


    /**
     * Метод для читання та обробки файлу.
     * Він перевіряє, чи є файл дійсним, потім читає його та класифікує текст.
     *
     * @throws IOException            у разі проблем із читанням файлу.
     * @throws ClassNotFoundException у разі проблем із завантаженням моделі класифікатора.
     */
    private void readFile() throws IOException, ClassNotFoundException {
        File file = new File(filePath);

        if (FileManager.isFileValid(file)) {
            DocumentReader documentReader = new DocumentReader(filePath);
            String text = documentReader.reader().toString();
            Map<String, Object> metadata = documentReader.getMetadataMap();
            String status = mainClassifier.classifyTexts(text).replaceAll(",", "");
            processClassificationResult(status, text, metadata);
            FileManager.deleteFile(file);  // Видаляємо файл після обробки
        } else {
            LOGGER.severe("Файл не знайдено або порожній: " + filePath);
        }
    }

    /**
     * Метод для обробки результату класифікації.
     * Якщо результат класифікації є "позитивним", додається класифікація від додаткового класифікатора.
     * Якщо результат "негативний", файл видаляється з Minio.
     *
     * @param status результат класифікації (статус).
     * @param text   текст, що був класифікований.
     * @throws IOException у разі проблем із видаленням файлу з Minio.
     */
    private void processClassificationResult(@NotNull
                                             String status,
                                             String text,
                                             Map<String, Object> metadata) throws IOException {
        if (status.contains("positive")) {
            String tip = tipClassifier.classifyTexts(text).toString().replaceAll(",", "");
            LOGGER.info("ПОЗИТИВНИЙ " + hostInfo.toString());

            Map<String, Object> map = new AddInfo().loadConfig(key_phrases, text);

            elasticSender.sendData("latimeria", UUID.randomUUID().toString(), new Result(hostInfo, status, tip, metadata, map));
        } else {
            LOGGER.info("НЕГАТИВНИЙ " + hostInfo.toString());
            minioManager.deleteFile(objectName);  // Видаляємо файл із Minio
        }
    }

    /**
     * Основний метод, що виконується при запуску потоку.
     * Читає файл і класифікує текст із затримкою для впевненості в завантаженні файлу.
     */
    @Override
    public void run() {
        try {
            Thread.sleep(200);  // Затримка для впевненості в завантаженні файлу
            readFile();

        } catch (InterruptedException e) {
            LOGGER.severe("Потік було перервано: " + e.getMessage());
            Thread.currentThread().interrupt();  // Відновлюємо стан переривання

        } catch (IOException | ClassNotFoundException e) {
            LOGGER.severe("Помилка при читанні файлу або завантаженні моделі: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            LOGGER.info("Потік завершено.");
        }
    }

    /**
     * Метод для запуску читача в окремому потоці.
     * Створює новий потік і запускає його.
     */
    public void startReading() {
        Thread thread = new Thread(this);
        thread.start();
    }
}
