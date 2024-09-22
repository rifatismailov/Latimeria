package org.example.filemanagement;

import java.io.*;
import java.util.logging.Logger;

public class FileManager {
    private static final Logger LOGGER = Logger.getLogger(FileManager.class.getName());

    // Метод перевірки існування файлу та його непустого розміру
    public static boolean isFileValid(File file) {
        return file.exists() && file.isFile() && file.length() > 0;
    }

    public static void deleteFile(File file) {
        if (file.delete()) {
            LOGGER.info("Файл був успішно видалений: " + file);
        } else {
            LOGGER.warning("Файл не може бути видалений: " + file);
        }
    }
    public static void createFile(File file) {}
    public static ObjectInputStream fileOIStream(String file) throws IOException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(file)));
            return ois;
    }
}


