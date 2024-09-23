package org.example.nlp;

import cc.mallet.classify.Classification;
import cc.mallet.classify.Classifier;
import org.example.executor.TextExecutor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Клас для класифікації текстів за допомогою моделі класифікатора.
 * Клас підтримує завантаження моделі з файлу та класифікацію окремого тексту або масиву текстів.
 */
public class TextClassifier {
    private static final Logger LOGGER = Logger.getLogger(TextClassifier.class.getName());

    // Поле для зберігання класифікатора
    private Classifier classifier;

    /**
     * Метод для класифікації одного тексту.
     * Повертає категорію тексту на основі передбачення класифікатора.
     *
     * @param text Текст для класифікації.
     * @return Назва категорії, до якої належить текст.
     * У випадку помилки класифікації повертається "String index error" або "Classification error".
     */
    public String classifyText(String text) {
        try {
            Classification classification = classifier.classify(text);
            return classification.getLabeling().getBestLabel().toString();
        } catch (StringIndexOutOfBoundsException e) {
            LOGGER.log(Level.WARNING, "Индекс строки выходит за пределы: {0}", e.getMessage());
            return "Ошибка индекса строки";
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Ошибка классификации текста: {0}", e.getMessage());
            return "Ошибка классификации";
        }
    }

    /**
     * Метод для завантаження моделі класифікатора з файлу.
     * Завантажує серіалізований об'єкт моделі з файлу та перевіряє, чи він успішно завантажений.
     *
     * @param file Шлях до файлу моделі, який потрібно завантажити.
     * @throws IOException            У випадку, якщо виникають проблеми з читанням файлу.
     * @throws ClassNotFoundException У випадку, якщо клас класифікатора не знайдено при десеріалізації.
     * @throws IllegalStateException  У випадку, якщо завантажений класифікатор є null.
     */
    public void loadModel(String file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            classifier = (Classifier) ois.readObject();
            // Логування успішного завантаження моделі
            LOGGER.info("Модель успешно загружена из: " + file);
        } catch (FileNotFoundException e) {
            // Логування та виняток у випадку, якщо файл не знайдено
            LOGGER.severe("Файл модели не найден: " + file);
            throw e;
        } catch (IOException | ClassNotFoundException e) {
            // Логування інших помилок при завантаженні
            LOGGER.severe("Ошибка загрузки модели: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Метод для класифікації одного тексту.
     * Викликає метод класифікації та повертає результат у форматі рядка з категорією тексту.
     *
     * @param text Текст, який потрібно класифікувати.
     * @return Рядок у форматі "[категорія]", що відповідає передбаченій категорії.
     */
    public String classifyTexts(String text) {
        String predictedCategory;
        try {
            predictedCategory = classifyText(text);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Ошибка классификации текста: {0}", e.getMessage());
            predictedCategory = "Ошибка классификации текста";
        }
        return "[" + predictedCategory + "]";
    }

    /**
     * Метод для класифікації масиву текстів із використанням багатопоточності.
     * Якщо кількість текстів велика або процес класифікації займає багато часу,
     * цей метод виконує паралельну обробку для підвищення продуктивності.
     *
     * @param texts Масив текстів для класифікації.
     * @return Список категорій, передбачених для кожного тексту.
     * @Опис: Цей метод класифікує масив текстів за допомогою багатопоточності.
     * Він створює стільки потоків, скільки доступно процесорів, або стільки,
     * скільки текстів необхідно класифікувати, залежно від того, що менше.
     * Це дозволяє ефективно використовувати ресурси для великих масивів даних або складних класифікацій.
     */
    public List<String> classifyTexts(String[] texts) {
        ExecutorService executor = TextExecutor.executor(texts);
        List<Future<String>> futures = new ArrayList<>();
        List<String> results = new ArrayList<>();

        try {
            // Створення потоків для паралельної обробки кожного тексту
            for (String text : texts) {
                futures.add(executor.submit(() -> classifyText(text)));
            }

            // Отримання результатів після обробки
            for (Future<String> future : futures) {
                try {
                    results.add(future.get());
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Ошибка получения результата классификации: {0}", e.getMessage());
                    results.add("Ошибка классификации");
                }
            }
        } finally {
            // Завершення роботи з потоками
            executor.shutdown();
        }

        return results;
    }
}
