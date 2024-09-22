package org.example.nlp;


import cc.mallet.classify.Classification;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.MaxEntTrainer;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.types.InstanceList;
import org.springframework.lang.NonNull;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TextClassifierTip {

    private Classifier classifier;

    @NonNull
    public String classifyText(String text) {
        Classification classification = classifier.classify(text);
        return classification.getLabeling().getBestLabel().toString();
    }


    public String classifyText(Classifier classifier, String text) {
        return classifier.classify(text).getLabeling().getBestLabel().toString();
    }

    // Завантаження моделі
    @NonNull
    public void loadModel(String modelFilePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(modelFilePath))) {
            classifier = (Classifier) ois.readObject();
        }
    }

    public static List<String> classifier(String[] texts) {
        List<String> list = new ArrayList<>();
        try {
            TextClassifierTip classifier = new TextClassifierTip();
            // Завантажуємо навчальну модель
            classifier.loadModel("classifier_model_tip.dat");
            // Класифікуємо кожен текст і додаємо результат у список
            for (String text : texts) {
                String predictedCategory = classifier.classifyText(text);
                list.add(predictedCategory);
            }
        } catch (Exception e) {
           // e.printStackTrace();
        }
        return list;
    }

}
