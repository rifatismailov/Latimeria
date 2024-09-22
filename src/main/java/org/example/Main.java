package org.example;

import org.example.nlp.TextClassifier;
import org.example.reader.DocumentReader;
import org.example.reader.Reader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Main {
    public  static void main(String[] args) throws IOException, ClassNotFoundException {
        MalletClassifier mainClassifier=new MalletClassifier(); // Основний класифікатор тексту
        //mainClassifier.loadModel("classifier_model.dat");\
        mainClassifier.trainModel("trainingDataALL.csv");
        // Вкажіть шлях до папки
        Path folderPath = Paths.get("docx/");
        try (Stream<Path> paths = Files.walk(folderPath)) {
            // Проходимо по кожному файлу та виводимо його назву
            paths.filter(Files::isRegularFile).forEach(file -> {
                String text = new DocumentReader(file.toString()).reader().toString();
                String status = mainClassifier.classifyTexts(text).replaceAll(",", "");
                System.out.println(status);
                // Використовуємо file.toString() для отримання повного шляху

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}