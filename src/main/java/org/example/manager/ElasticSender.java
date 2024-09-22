package org.example.manager;

import org.elasticsearch.client.RequestOptions;
import org.example.web.Result;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Клас ElasticSender відповідає за відправку даних до Elasticsearch.
 */
@Service
public class ElasticSender {
    private static final Logger LOGGER = Logger.getLogger(ElasticSender.class.getName());

    // Клієнт для взаємодії з Elasticsearch.
    private RestHighLevelClient client;

    /**
     * Конструктор для ініціалізації ElasticSender з RestHighLevelClient.
     *
     * @param client клієнт для роботи з Elasticsearch.
     */
    public ElasticSender(RestHighLevelClient client) {
        this.client = client;
    }

    /**
     * Метод для відправки даних до Elasticsearch.
     *
     * @param index  назва індексу, куди будуть відправлені дані.
     * @param id     унікальний ідентифікатор документа.
     * @param result об'єкт Result, що містить дані для відправки.
     * @throws IOException у разі помилки при відправці даних до Elasticsearch.
     */
    public void sendData(String index, String id, @NotNull Result result) throws IOException {
        try {
            // Конвертуємо об'єкт Result у Map для відправки.
            Map<String, Object> jsonMap = result.toMap();

            // Створюємо запит для індексації даних у Elasticsearch.
            IndexRequest request = new IndexRequest(index)
                    .id(id)
                    .source(jsonMap);  // Передаємо готовий Map замість об'єкта

            // Виконуємо запит до Elasticsearch і отримуємо відповідь.
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            LOGGER.info("Дані відправлено: " + response.getId());
            LOGGER.info("Response: " + response.toString());
        } catch (IOException e) {
            // Логування помилки, якщо відправка не вдалася.
            LOGGER.severe("Помилка при відправці даних: " + e.getMessage());
        }
    }
}
