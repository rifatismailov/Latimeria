package org.example.reader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddInfo {


    public Map<String, Object> loadConfig(String filePath, String text) {
        ObjectMapper objectMapper = new ObjectMapper(); // Створюємо об'єкт ObjectMapper для роботи з Jackson
        Map<String, Object> map = new HashMap<>();
        map.put("time_check", Arrays.asList(new Date().getTime(),new Date().getDate()));
        try {
            // Читаємо JSON файл
            JsonNode rootNode = objectMapper.readTree(new File(filePath));
            // Створюємо мапу для збереження результатів
            // Ітеруємо через всі поля в JSON
            if (rootNode.isObject()) {
                ObjectNode jsonObject = (ObjectNode) rootNode;
                jsonObject.fields().forEachRemaining(entry -> {
                    String key = entry.getKey();
                    JsonNode value = entry.getValue();

                    if (value.isArray()) {
                        // Отримуємо масив для пошуку
                        ArrayNode jsonNode = (ArrayNode) value;
                        String[] arrayRegex = new String[jsonNode.size()];

                        // Заповнюємо масив з ArrayNode
                        for (int i = 0; i < jsonNode.size(); i++) {
                            arrayRegex[i] = jsonNode.get(i).asText();
                        }

                        // Витягуємо текст після ключових фраз
                        Map<String, String> extracted = moreRegex(text, arrayRegex);
                        map.put(key, extracted);

                    } else if (value.isTextual()) {
                        // Якщо значення - це примітивний текст
                        Map<String, String> numb = oneRegex(text, value.asText());
                        map.put(key, numb);

                    } else {
                    }
                });
            }

            // Виводимо результати
            if (!map.isEmpty()) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        // map.put("key_position", Arrays.asList("офіцер", "менеджер"));

        return map;
    }


    // Метод для витягування тексту після ключових фраз
    public Map<String, String> moreRegex(String text, String[] KEY) {
        // Створюємо регулярний вираз для пошуку фраз
        Map<String, String> results = new HashMap<>();

        for (String phrase : KEY) {
            // Шаблон для пошуку фраз і тексту після них
            String regex = "(" + phrase + ")\\s*([^\\n]+)";
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);

            // Лічильник для ітерації кожного збігу для даного ключа
            int quantity = 1;

            // Шукаємо всі збіги
            while (matcher.find()) {
                String key = "key_" + quantity + "_" + matcher.group(1).trim();
                String value = matcher.group(2).trim().replaceAll("\\s{2,}", " ");
                ;

                // Додаємо результат з ітерацією до мапи
                results.put(key, value);
                quantity++; // Збільшуємо ітерацію для наступного збігу
            }
        }

        return results;
    }

    public Map<String, String> oneRegex(String text, String regex) {

        Map<String, String> results = new HashMap<>();
        // Створюємо шаблон (Pattern)
        Pattern pattern = Pattern.compile(regex);

        // Створюємо об'єкт Matcher для пошуку в тексті
        Matcher matcher = pattern.matcher(text);
        int quantity = 0;
        // Шукаємо всі збіги
        while (matcher.find()) {
            if (!matcher.group().isEmpty()) {
                quantity++;
                results.put("element" + quantity, matcher.group());
            }
        }
        return results;
    }
}
