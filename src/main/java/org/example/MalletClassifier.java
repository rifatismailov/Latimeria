package org.example;

import cc.mallet.classify.Classification;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.MaxEntTrainer;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.types.InstanceList;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class MalletClassifier {

    private Classifier classifier;

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
        Classification classification = classifier.classify(text);
        return classification.getLabeling().getBestLabel().toString();
    }
    public String classifyText(Classification classification,String text) {
        classification = classifier.classify(text);
        return classification.getLabeling().getBestLabel().toString();
    }
    // Збереження моделі
    public void saveModel(String modelFilePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(modelFilePath))) {
            oos.writeObject(classifier);
        }
    }

    // Завантаження моделі
    public void loadModel(String modelFilePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(modelFilePath))) {
            classifier = (Classifier) ois.readObject();
        }
    }
    // Класифікація одного тексту з повертанням у вигляді рядка
    public String classifyTexts(String text) {
       // String cleanText = cleanText(text);
        String predictedCategory;
        try {
            predictedCategory = classifyText(text);
        } catch (Exception e) {
          //  LOGGER.log(Level.WARNING, "Error classifying text: {0}", e.getMessage());
            predictedCategory = "Error classifying text";
        }
        return "[" + predictedCategory + "]";
    }
}
