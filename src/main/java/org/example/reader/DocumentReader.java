package org.example.reader;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Клас для читання документів з використанням Apache Tika.
 */
public class DocumentReader {
    private static final Logger LOGGER = Logger.getLogger(DocumentReader.class.getName());

    private String filePath; // Шлях до файлу
    Map<String, Object> metadataMap;

    /**
     * Конструктор для ініціалізації шляху до файлу.
     *
     * @param filePath шлях до файлу, який потрібно прочитати.
     */
    public DocumentReader(String filePath) {
        this.filePath = filePath;
    }

    public Map<String, Object> getMetadataMap() {
        return metadataMap;
    }

    /**
     * Метод для читання вмісту документа.
     *
     * @return StringBuilder з текстовим вмістом документа або null у разі помилки.
     */
    public StringBuilder reader() {
        File file = new File(filePath);
        Map<String, Object> metadataMap = new HashMap<>();
        try (InputStream inputStream = new FileInputStream(file)) {
            // Використовуємо AutoDetectParser для автоматичного визначення формату файлу
            AutoDetectParser parser = new AutoDetectParser();
            BodyContentHandler handler = new BodyContentHandler(-1); // Без обмежень на розмір вмісту
            Metadata metadata = new Metadata();
            ParseContext context = new ParseContext();
            // Парсимо файл
            parser.parse(inputStream, handler, metadata, context);
            // Отримуємо метадані (властивості документа)
            for (String name : metadata.names()) {
                metadataMap.put(name, metadata.get(name));
            }

            // Отримуємо текстовий вміст документа
            StringBuilder content = new StringBuilder();
            content.append(handler);
            this.metadataMap = metadataMap;
            return content;

        } catch (IOException | TikaException | SAXException e) {
            LOGGER.severe("Помилка під час класифікації тексту: " + e.getMessage());
        }
        return null; // Повертаємо null у разі помилки
    }
}
