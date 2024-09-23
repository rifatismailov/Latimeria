package org.example.manager;

import org.elasticsearch.client.RequestOptions;
import org.example.web.Result;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.action.index.IndexRequest;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            client.index(request, RequestOptions.DEFAULT);

        } catch (IOException e) {
            LOGGER.severe("Інший тип помилки :" + e.getMessage());
            String regex = "requestLine=(.*?), host=(.*?), response=(.*?)}";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(e.getMessage());

            if (matcher.find()) {
                String requestLine = matcher.group(1);
                String host = matcher.group(2);
                String response = matcher.group(3);

                LOGGER.info("Запрос строки: " + requestLine);
                LOGGER.info("Host: " + host);
                LOGGER.info("Відповідь: " + response);
            } else {
                LOGGER.severe("Не вдалося розпарсити деталі з повідомлення: " + e.getMessage());
            }
        }
    }
}
