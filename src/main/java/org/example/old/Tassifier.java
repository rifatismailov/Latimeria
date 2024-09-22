package org.example.old;

import cc.mallet.classify.Classification;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.MaxEntTrainer;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.types.InstanceList;
import org.example.filemanagement.FileManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static org.example.filemanagement.FileManager.fileOIStream;

public class Tassifier {
    private static final Logger LOGGER = Logger.getLogger(Tassifier.class.getName());

    private Classifier classifier;

    /**
     * Метод для завантаження моделі класифікатора з файлу.
     * Завантажує серіалізований об'єкт моделі з файлу та перевіряє, чи він успішно завантажений.
     *
     * @param file Шлях до файлу моделі, який потрібно завантажити.
     * @throws IOException              У випадку, якщо виникають проблеми з читанням файлу.
     * @throws ClassNotFoundException   У випадку, якщо клас класифікатора не знайдено при десеріалізації.
     * @throws IllegalStateException    У випадку, якщо завантажений класифікатор є null.
     */
    public void loadModel(String file) throws IOException, ClassNotFoundException {
        try {
            // Завантажуємо об'єкт класифікатора з файлу
            classifier = (Classifier) fileOIStream(file).readObject();

            // Перевіряємо, чи завантажений класифікатор не є null
            if (classifier == null) {
                throw new IllegalStateException("Loaded classifier is null.");
            }

            // Логування успішного завантаження моделі
            LOGGER.info("Model loaded successfully from: " + file);
        } catch (FileNotFoundException e) {
            // Логування та виняток у випадку, якщо файл не знайдено
            LOGGER.severe("Model file not found: " + file);
            throw e;
        } catch (IOException | ClassNotFoundException e) {
            // Логування інших помилок при завантаженні
            LOGGER.severe("Error loading model: " + e.getMessage());
            throw e;
        }
    }

    // Створення послідовності Pipes для обробки тексту
    private Pipe buildPipe() {
        ArrayList<Pipe> pipeList = new ArrayList<>();

        // Перетворюємо текст у токени
        pipeList.add(new Target2Label());  // Позначаємо цільові метки
        pipeList.add(new CharSequence2TokenSequence(Pattern.compile("\\p{L}+"))); // Токенізація
        pipeList.add(new TokenSequenceLowercase());  // Зниження регістру
        pipeList.add(new TokenSequenceRemoveStopwords(false, false));  // Видалення стоп-слів
        pipeList.add(new TokenSequence2FeatureSequence());  // Перетворення токенів у послідовність ознак
        pipeList.add(new FeatureSequence2FeatureVector());  // Перетворення послідовності ознак у вектор ознак

        return new SerialPipes(pipeList);
    }
    // Навчання моделі на основі CSV-файлу
    public void trainModel(String trainingDataFilePath) throws IOException {
        Pipe pipe = buildPipe();  // Створюємо послідовність Pipe для обробки даних
        InstanceList instances = new InstanceList(pipe);

        // CSV формат: category, text
        instances.addThruPipe(new CsvIterator(new FileReader(new File(trainingDataFilePath)),
                Pattern.compile("^(\\S*)[\\s,]*(.*)$"), 2, 1, -1));

        ClassifierTrainer<?> trainer = new MaxEntTrainer();
        classifier = trainer.train(instances);
    }

    // Класифікація тексту
    public String classifyText(String text) {
        if (text == null || text.isEmpty()) {
            LOGGER.warning("Text cannot be null or empty.");
            return "Invalid text input";
        }

        try {
            // Класифікація тексту
            Classification classification = classifier.classify(text);
            return classification.getLabeling().getBestLabel().toString();
        } catch (StringIndexOutOfBoundsException e) {
            LOGGER.log(Level.WARNING, "String index out of bounds: {0}", e.getMessage());
            return "String index error";
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error classifying text: {0}", e.getMessage());
            return "Classification error";
        }
    }

    // Збереження моделі
    public void saveModel(String modelFilePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(modelFilePath))) {
            oos.writeObject(classifier);
        }
    }

    // Очищення тексту
    public String cleanText(String text) {
        return text.replaceAll("[^\\p{Print}]", " ").trim();
    }

    // Класифікація масиву текстів
    public List<String> classifyTexts(String[] texts) {
        List<String> results = new ArrayList<>();
        for (String text : texts) {
            try {
                String cleanText = cleanText(text); // Очищення тексту перед класифікацією
                String predictedCategory = classifyText(cleanText);
                results.add(predictedCategory);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error classifying text: {0}", e.getMessage());
                results.add("Error classifying text");
            }
        }
        return results;
    }


    // Класифікація одного тексту з повертанням у вигляді рядка
    public String classifyTexts(String text) {
        // String cleanText = cleanText(text);
        String predictedCategory;
        try {
            predictedCategory = classifyText(text);
        } catch (Exception e) {
            //  LOGGER.log(Level.WARNING, "Error classifying text: {0}", e.getMessage());
            LOGGER.log(Level.WARNING, "Error classifying text: {0}", e.getMessage());
            predictedCategory = "Error classifying text";
        }
        return "[" + predictedCategory + "]";
    }
}
