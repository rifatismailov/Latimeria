package org.example.nlp;

import java.io.IOException;

public class ClassifierModel {
    private final TextClassifier mainClassifier;  // Основний класифікатор тексту
    private final TextClassifier tipClassifier;   // Додатковий класифікатор для "підказок"

    public ClassifierModel(TextClassifier mainClassifier, TextClassifier tipClassifier) {
        this.mainClassifier = mainClassifier;
        this.tipClassifier = tipClassifier;
    }

    public ClassifierModel(String modelFile, String modelFileTip) throws IOException, ClassNotFoundException {
        this.mainClassifier = new TextClassifier();
        this.mainClassifier.loadModel(modelFile);
        this.tipClassifier = new TextClassifier();
        this.tipClassifier.loadModel(modelFileTip);
    }

    public TextClassifier getMainClassifier() {
        return mainClassifier;
    }

    public TextClassifier getTipClassifier() {
        return tipClassifier;
    }
}
