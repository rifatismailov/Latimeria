package org.example.nlp;

import java.io.IOException;

public class ClassifierModel {
    private final TextClassifier mainClassifier;  // Основний класифікатор тексту
    private final TextClassifier tipClassifier;   // Додатковий класифікатор для "підказок"

    /**
     * Конструктор для створення класу ClassifierModel з переданими класифікаторами.
     *
     * @param mainClassifier основний класифікатор тексту
     * @param tipClassifier  додатковий класифікатор для "ТІПУ" виконує пошук належності документу
     */
    public ClassifierModel(TextClassifier mainClassifier, TextClassifier tipClassifier) {
        this.mainClassifier = mainClassifier;
        this.tipClassifier = tipClassifier;
    }

    /**
     * Конструктор для створення класу ClassifierModel з файлами моделей.
     *
     * @param modelFile    шлях до файлу основної моделі
     * @param modelFileTip шлях до файлу моделі для "ТИПУ ДОКУМЕНТА"
     * @throws IOException          якщо виникає помилка вводу/виводу при завантаженні моделі
     * @throws ClassNotFoundException якщо клас не знайдено під час завантаження моделі
     */
    public ClassifierModel(String modelFile, String modelFileTip) throws IOException, ClassNotFoundException {
        this.mainClassifier = new TextClassifier();
        this.mainClassifier.loadModel(modelFile); // Завантажуємо основну модель
        this.tipClassifier = new TextClassifier();
        this.tipClassifier.loadModel(modelFileTip); // Завантажуємо модель для підказок
    }

    /**
     * Отримує основний класифікатор тексту.
     *
     * @return основний класифікатор
     */
    public TextClassifier getMainClassifier() {
        return mainClassifier;
    }

    /**
     * Отримує додатковий класифікатор для "ТИПУ ДОКУМЕНТА".
     *
     * @return класифікатор для ТИПУ ДОКУМЕНТА
     */
    public TextClassifier getTipClassifier() {
        return tipClassifier;
    }
}
