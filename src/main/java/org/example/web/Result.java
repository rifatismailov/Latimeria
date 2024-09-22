package org.example.web;

import java.util.HashMap;
import java.util.Map;

/**
 * Клас Result представляє результат обробки даних і містить інформацію про хост, статус та тип.
 */
public class Result {

    // Інформація про хост (ім'я хоста, адреса тощо).
    private HostInfo hostInfo;

    // Статус результату (наприклад, positive або negative).
    private String status;

    // Тип результату (наприклад, повідомлення або рапорт те що... ).
    private String tip;

    /**
     * Конструктор для ініціалізації об'єкта Result.
     *
     * @param hostInfo інформація про хост (ім'я хоста, IP-адреса, MAC-адреса тощо).
     * @param status статус результату (наприклад, positive або negative).
     * @param tip тип результату (повідомлення, схема тощо).
     */
    public Result(HostInfo hostInfo, String status, String tip) {
        this.hostInfo = hostInfo;
        this.status = status;
        this.tip = tip;
    }

    /**
     * Метод для конвертації об'єкта Result у карту (Map), яку можна використовувати для відправки
     * або подальшої серіалізації в JSON.
     *
     * @return карта, що містить ключі та значення для полів hostInfo, status і tip.
     */
    public Map<String, Object> toMap() {

        // Створюємо мапу для зберігання інформації про Result.
        Map<String, Object> map = new HashMap<>();

        // Створюємо мапу для зберігання інформації про хост.
        Map<String, Object> hostInfoMap = new HashMap<>();
        hostInfoMap.put("hostName", hostInfo.getHostName());
        hostInfoMap.put("hostAddress", hostInfo.getHostAddress());
        hostInfoMap.put("macAddress", hostInfo.getMacAddress());
        hostInfoMap.put("remoteAddr", hostInfo.getRemoteAddr());
        hostInfoMap.put("urlFile", hostInfo.getUrlFile());

        // Додаємо інформацію про хост до основної мапи.
        map.put("hostInfo", hostInfoMap);

        // Додаємо статус та тип результату до мапи.
        map.put("status", status);
        map.put("tip", tip);

        // Повертаємо готову мапу.
        return map;
    }

    /**
     * Повертає об'єкт HostInfo, що містить інформацію про хост.
     *
     * @return об'єкт HostInfo з інформацією про хост.
     */
    public HostInfo getHostInfo() {
        return hostInfo;
    }

    /**
     * Повертає статус результату.
     *
     * @return рядок, що містить статус (наприклад, positive або negative).
     */
    public String getStatus() {
        return status;
    }

    /**
     * Повертає тип результату.
     *
     * @return рядок, що містить тип (наприклад, Message або Order).
     */
    public String getTip() {
        return tip;
    }
}
