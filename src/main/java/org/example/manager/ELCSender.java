package org.example.manager;

import org.elasticsearch.client.RequestOptions;
import org.example.web.Result;
import org.springframework.stereotype.Service;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class ELCSender {
    private static final Logger LOGGER = Logger.getLogger(ELCSender.class.getName());

    private RestHighLevelClient client;

    public ELCSender(RestHighLevelClient client) {
        this.client = client;
    }

    public void sendData(String index, String id, Result result) {
        try {
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("latimeria", result.toString());
            IndexRequest request = new IndexRequest(index).id(id).source(jsonMap);
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            LOGGER.info("Дані відправлено: " + response.getId());
            LOGGER.info("Response: " + response);
        } catch (IOException e) {
            LOGGER.severe("Помилка при відправці даних: " + e.getMessage());
        }
    }
}

