package org.example.util;

import java.io.IOException;
import java.util.Properties;

/**
 * Utility класс для работы с properties файлом
 */
public final class PropertiesUtil {
    /**
     * объект Properties
     */
    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }
    private PropertiesUtil(){}

    /**
     * получение значения свойства по ключу из Properties
     * @param key - ключ
     * @return - значение свойства по ключу
     */
    public static String get(String key){
        return PROPERTIES.getProperty(key);
    }

    /**
     * метод, загружающий объект Properties
     */
    private static void loadProperties(){
        try(var inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream("application.properties")){
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}