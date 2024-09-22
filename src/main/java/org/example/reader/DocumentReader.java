package org.example.reader;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.example.nlp.TextClassifier;
import org.example.nlp.TextClassifierTip;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DocumentReader {
    String filePath;

    public DocumentReader(String filePath) {
        this.filePath = filePath;
    }

    public StringBuilder reader() {
        File file = new File(filePath);

        try (InputStream inputStream = new FileInputStream(file)) {
            // Використовуємо AutoDetectParser для автоматичного визначення формату файлу
            AutoDetectParser parser = new AutoDetectParser();
            BodyContentHandler handler = new BodyContentHandler(-1); // Без обмежень на розмір вмісту
            Metadata metadata = new Metadata();
            ParseContext context = new ParseContext();
            // Парсимо файл
            parser.parse(inputStream, handler, metadata, context);
            // Отримуємо текстовий вміст документа
            StringBuilder content = new StringBuilder();
            content.append(handler);
            return content;


        } catch (IOException | TikaException | SAXException e) {
            System.out.println("Помилка під час класифікації тексту: " + e.getMessage());
        }
        return null;
    }

}

