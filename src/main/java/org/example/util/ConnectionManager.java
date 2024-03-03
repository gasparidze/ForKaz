package org.example.util;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Класс, отвечающий за управления соединениями в Connection Pool
 */
public final class ConnectionManager {
    /**
     * ключ для получения значения пароля в объекте application.properties
     */
    private static final String PASSWORD_KEY = "db.password";
    /**
     * ключ для получения значения логина в объекте application.properties
     */
    private static final String USERNAME_KEY = "db.username";
    /**
     * ключ для получения значения url в объекте application.properties
     */
    private static final String URL_KEY = "db.url";
    /**
     * ключ для получения значения размера пула соединений в объекте application.properties
     */
    private static final String POOL_SIZE_KEY = "db.pool.size";
    /**
     * дефолтный размер пула соединений
     */
    private static final Integer DEFAULT_POOL_SIZE = 10;

    /**
     * блокирующая очередь
     */
    private static BlockingQueue<Connection> pool;
    /**
     * коллекция для хранения исходных соединений
     */
    private static List<Connection> sourceConnections;

    static {
        initConnectionPool();
    }

    private ConnectionManager() {}

    /**
     * инициализация connection pool
     */
    private static void initConnectionPool() {
        String poolSize = PropertiesUtil.get(POOL_SIZE_KEY);
        int size = poolSize == null ? DEFAULT_POOL_SIZE : Integer.parseInt(poolSize);
        /**
         * т.к. сервис будет работать со многими потоками, необходимо выбрать потокобезопасную коллекцию
         */
        pool = new ArrayBlockingQueue<>(size);
        sourceConnections = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Connection connection = open();
            /**
             * вместо того, чтобы передавать соединение в pool, используем reflection API
             * если вызывался метод close, то мы вместо того, чтобы закрывать соединение, возвращаем его обратно в pool
             * иначе продолжаем выполнение нашего метода.
             */
            Connection proxyConnection = (Connection) Proxy.newProxyInstance(
                    ConnectionManager.class.getClassLoader(),
                    new Class[]{Connection.class},
                    ((proxy, method, args) -> method.getName().equals("close")
                            ? pool.add((Connection) proxy)
                            : method.invoke(connection, args))
            );
            pool.add(proxyConnection);
            sourceConnections.add(connection);
        }
    }
    /**
     * открытым методом мы будем держать тот метод, который достает эти соединения из нашего pool'а.
     * take() возвращает соединение, если оно есть.
     * если pool пустой, то выпадает exception
     *
     * @return - текущее соединение
     */
    public static Connection get() {
        try {
            return pool.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * возможность возвращать в наш pool из тех мест, где нужно закрывать соединение
     * т.е. в блоке try with resources должны вернуть назад в pool, а не закрывать их
     */

    /**
     * нужно закрыть метод, т.е. сделать его private, чтобы никто случайно не создал новое соединение
     * @return - текущее соединение
     */
    private static Connection open(){
        try{
            return DriverManager.getConnection(
                    PropertiesUtil.get(URL_KEY),
                    PropertiesUtil.get(USERNAME_KEY),
                    PropertiesUtil.get(PASSWORD_KEY)
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *  вызов метода close у Proxy возвращает в пул, а не закрывает его
     *  в pool лежат наши proxy объекты, а не сами соединения, поэтому нужна еще одна коллекция, где
     *  будем хранить исходные соединения, которые понадобятся для закрытия в конце.
     */
    public static void closePool(){
        for (Connection sourceConnection : sourceConnections) {
            try {
                sourceConnection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
