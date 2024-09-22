package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Tesct {
    public static void main(String[] args) {
        // Створюємо клієнт для Elasticsearch
        try (RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new org.apache.http.HttpHost("192.168.51.131", 9200, "http")))) {

            // Створюємо IndexRequest для індексації документа
            IndexRequest request = new IndexRequest("people").id("2")
                    .source(XContentType.JSON, "name", "John Doe", "age", 30);

            try {
                // Відправляємо запит і отримуємо відповідь
                IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);

                // Виводимо відповідь у вигляді JSON
                ObjectMapper mapper = new ObjectMapper();
                String responseBody = indexResponse.toString();
                JsonNode rootNode = mapper.readTree(responseBody);
                System.out.println("Отримана відповідь: " + rootNode.toPrettyString());

                // Парсимо відповідь для важливих полів
                String result = rootNode.path("result").asText();
                System.out.println("Результат операції: " + result);

            } catch (ElasticsearchException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
