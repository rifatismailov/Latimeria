package org.example.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TextExecutor {
    /**
     * @texts.length: Це кількість текстів, які потрібно класифікувати.
     * @Runtime.getRuntime().availableProcessors(): Цей метод повертає кількість процесорних ядер,
     * доступних для JVM (Java Virtual Machine), тобто кількість потоків, які можуть працювати одночасно для виконання завдань.
     * @Math.min(): Ця функція повертає менше з двох значень: кількість текстів або кількість доступних процесорів.
     * Це гарантує, що не буде створено більше потоків, ніж потрібно або доступно для обробки текстів.
     **/
    public static ExecutorService executor(String[] texts) {
        int threadCount = Math.min(texts.length, Runtime.getRuntime().availableProcessors());
        return Executors.newFixedThreadPool(threadCount);
    }
}
