package org.example.service;

import io.minio.*;
import io.minio.errors.MinioException;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


/**
 * Конструктор для створення об'єкта Storage.
 *
 * @param minioClient Об'єкт MinioClient для взаємодії з MinIO сервером.
 * @param bucketName  Ім'я відра, яке буде використовуватися для зберігання файлів.
 */
public record MinioManager(MinioClient minioClient, String bucketName) {
    private static final Logger LOGGER = Logger.getLogger(MinioManager.class.getName());
    private static String SAVE_DIR;

    /**
     * Створює та повертає об'єкт MinioClient для взаємодії з MinIO сервером.
     *
     * @param endpoint  URL MinIO сервера.
     * @param accessKey Ключ доступу.
     * @param secretKey Секретний ключ.
     * @return Об'єкт MinioClient.
     */
    public static MinioClient minio(String endpoint, String accessKey, String secretKey) {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    /**
     * Перевіряє, чи існує відро з вказаним ім'ям. Якщо не існує, створює його.
     */
    public void checkCreateBucket() {
        try {
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!isExist) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                LOGGER.info("Bucket " + bucketName + " створено.");
            } else {
                LOGGER.info("Bucket " + bucketName + " існує.");
            }
        } catch (MinioException | InvalidKeyException | NoSuchAlgorithmException | IOException e) {
            LOGGER.severe("Помилка при перевірці або створенні відра: " + e);
        }
    }

    /**
     * Отримує тимчасовий DIR для зберігання тимчасових файлів.
     *
     * @param SAVE_DIR Ім'я об'єкта.
     */
    public void checkSaveDir(String SAVE_DIR) {
        MinioManager.SAVE_DIR = SAVE_DIR;
        new File(SAVE_DIR).mkdirs();
    }

    /**
     * Отримує тимчасовий URL для завантаження об'єкта з MinIO.
     *
     * @param objectName Ім'я об'єкта.
     * @return Presigned URL для завантаження об'єкта.
     */
    public String getPresignedUrl(String objectName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(io.minio.http.Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(1, TimeUnit.HOURS) // URL буде дійсний протягом 1 години
                            .build()
            );
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            LOGGER.severe("Помилка при отриманні URL: " + e.getMessage());
            return null;
        }
    }

    /**
     * Завантажує файл до вказаного відра на MinIO сервер.
     *
     * @param fileName Ім'я файлу, яке буде використано в MinIO.
     * @param filePath Шлях до файлу на локальному диску.
     */
    public void send(String fileName, String filePath) {
        try (InputStream fileStream = new FileInputStream(filePath)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(fileStream, fileStream.available(), -1)
                            .contentType("application/octet-stream")
                            .build()
            );
            LOGGER.info("Файл " + fileName + " успішно завантажений.");
        } catch (MinioException | InvalidKeyException | NoSuchAlgorithmException | IOException e) {
            LOGGER.severe("Помилка при завантаженні файлу: " + e.getMessage());
        }
    }

    /**
     * Метод для видалення файлу з MinIO сервер.
     *
     * @param fileName Ім'я файлу, яке буде видалений з MinIO.
     */
    public void deleteFile(String fileName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(fileName).build());
            LOGGER.info("Файл " + fileName + " успішно видалений.");
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            LOGGER.severe("Помилка під час видалення об'єкта: " + e.getMessage());
        }
    }

    // Метод для завантаження файлу за URL
    /**
     * Метод для завантаження файлу за URL.
     *
     * @param fileURL – URL-адреса файлу для завантаження.
     */
    public String downloadFile(String fileURL) {
        // Повертаємо шлях до файлу після успішного завантаження
        return new FileDownloader().download(fileURL, SAVE_DIR);
    }

}
