package org.example.filemanagement;

import java.io.*;
import java.util.logging.Logger;

/**
 * Клас FileManager надає методи для роботи з файлами,
 * такі як перевірка існування файлу, видалення, створення файлу та створення потоку для читання об'єктів із файлу.
 */
public class FileManager {
    // Логер для запису інформаційних повідомлень та попереджень
    private static final Logger LOGGER = Logger.getLogger(FileManager.class.getName());

    /**
     * Перевіряє, чи існує файл, чи є він файлом (а не директорією) та чи має він непустий розмір.
     *
     * @param file Файл для перевірки.
     * @return true, якщо файл існує, є файлом і не порожній; false інакше.
     */
    public static boolean isFileValid(File file) {
        return file.exists() && file.isFile() && file.length() > 0;
    }

    /**
     * Видаляє вказаний файл. Логує результат операції.
     *
     * @param file Файл, який потрібно видалити.
     */
    public static void deleteFile(File file) {
        if (file.delete()) {
            LOGGER.info("Файл був успішно видалений: " + file);
        } else {
            LOGGER.warning("Файл не може бути видалений: " + file);
        }
    }

    /**
     * Створює новий файл, якщо він не існує. Логує результат операції.
     *
     * @param file Файл, який потрібно створити.
     */
    public static void createFile(File file) {
        try {
            if (file.createNewFile()) {
                LOGGER.info("Файл успішно створено: " + file);
            } else {
                LOGGER.warning("Файл вже існує: " + file);
            }
        } catch (IOException e) {
            LOGGER.severe("Помилка під час створення файлу: " + file + " - " + e.getMessage());
        }
    }

    /**
     * Створює потік для читання об'єктів із вказаного файлу.
     *
     * @param file Шлях до файлу, з якого потрібно читати.
     * @return ObjectInputStream для читання об'єктів із файлу.
     * @throws IOException Якщо виникає помилка під час відкриття файлу або створення потоку.
     */
    public static ObjectInputStream fileOIStream(String file) throws IOException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(file)));
        return ois;
    }
}
