package org.example.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Клас Result представляє результат обробки даних і містить інформацію про хост, статус та тип.
 */
public class Result {

    private HostInfo hostInfo;
    private String status;
    private String tip;
    private Map<String, Object> metadata;
    private Map<String, Object> addInfo;

    /**
     * Конструктор для ініціалізації об'єкта Result.
     *
     * @param hostInfo інформація про хост (ім'я хоста, IP-адреса, MAC-адреса тощо).
     * @param status   статус результату (наприклад, positive або negative).
     * @param tip      тип результату (повідомлення, схема тощо).
     * @param metadata методінні фалів яки були ідентіфіковані
     * @param addInfo зберігає данні які були знайдені за допомогою регулятора виразів
     */
    public Result(HostInfo hostInfo, String status, String tip, Map<String, Object> metadata,Map<String, Object> addInfo) {
        this.hostInfo = hostInfo;
        this.status = status;
        this.tip = tip;
        this.metadata = metadata;
        this.addInfo = addInfo;
    }

    /**
     * Метод для конвертації об'єкта Result у карту (Map), яку можна використовувати для відправки
     * або подальшої серіалізації в JSON.
     *
     * @return карта, що містить ключі та значення для полів hostInfo, status і tip.
     */
    public Map<String, Object> toMap() throws JsonProcessingException {

        Map<String, Object> map = new HashMap<>();
        Map<String, Object> hostInfoMap = new HashMap<>();
        hostInfoMap.put("hostName", hostInfo.getHostName());
        hostInfoMap.put("hostAddress", hostInfo.getHostAddress());
        hostInfoMap.put("macAddress", hostInfo.getMacAddress());
        hostInfoMap.put("remoteAddr", hostInfo.getRemoteAddr());
        hostInfoMap.put("urlFile", hostInfo.getUrlFile());
        hostInfoMap.put("addInfo", addInfo);
        map.put("hostInfo", hostInfoMap);
        map.put("metadata", metadata);
        map.put("status", status);
        map.put("tip", tip);
        return map;
    }
}
