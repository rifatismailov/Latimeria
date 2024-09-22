package org.example.reader;

import org.example.manager.MinioManager;
import org.example.filemanagement.FileManager;
import org.example.nlp.ClassifierModel;
import org.example.nlp.TextClassifier;
import org.example.web.HostInfo;
import org.example.web.Result;

import java.io.File;
import java.io.IOException;
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

    /**
     * Конструктор класу Reader.
     *
     * @param minioManager   клієнт Minio для роботи з хмарним сховищем.
     * @param objectName    назва об'єкта (файлу) в бакеті Minio.
     * @param hostInfo      інформація про хост.
     * @param filePath      шлях до локального файлу для читання.
     * @throws IOException              у разі проблем з файлом або мережею.
     * @throws ClassNotFoundException   у разі проблем із завантаженням класифікаторів.
     */
    public Reader(ClassifierModel classifierModel, MinioManager minioManager, String objectName, HostInfo hostInfo, String filePath) throws IOException, ClassNotFoundException {
        this.minioManager = minioManager;
        this.objectName = objectName;
        this.hostInfo = hostInfo;
        this.filePath = filePath;
        // Завантажуємо моделі для класифікації
        this.mainClassifier=classifierModel.getMainClassifier();
        this.tipClassifier=classifierModel.getTipClassifier();
    }

    /**
     * Метод для читання та обробки файлу.
     * Він перевіряє, чи є файл дійсним, потім читає його та класифікує текст.
     *
     * @throws IOException              у разі проблем із читанням файлу.
     * @throws ClassNotFoundException   у разі проблем із завантаженням моделі класифікатора.
     */
    private void readFile() throws IOException, ClassNotFoundException {
        File file = new File(filePath);

        if (FileManager.isFileValid(file)) {
            String text = new DocumentReader(filePath).reader().toString();
            String status = mainClassifier.classifyTexts(text).replaceAll(",", "");
            processClassificationResult(status, text);
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
     * @param status  результат класифікації (статус).
     * @param text    текст, що був класифікований.
     * @throws IOException у разі проблем із видаленням файлу з Minio.
     */
    private void processClassificationResult(String status, String text) throws IOException {
        if (status.contains("positive")) {
            String tip = tipClassifier.classifyTexts(text).toString().replaceAll(",", "");
            LOGGER.info("ПОЗИТИВНИЙ " + new Result(hostInfo.toString(), status, tip));
        } else {
            LOGGER.info("НЕГАТИВНИЙ " + filePath + " " + status);
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
