package org.example.util;

import lombok.NoArgsConstructor;
import org.example.util.ConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;

import static lombok.AccessLevel.PRIVATE;

/**
 * Класс необходимый для инициализации всех сущностей БД
 */
@NoArgsConstructor(access = PRIVATE)
public final class JDBCStarter {
    /**
     * sql запрос на создание всех сущностей БД
     */
    private static String CREATE_SQL = """
            CREATE SCHEMA IF NOT EXISTS client_service;
                                    
            CREATE TABLE IF NOT EXISTS client_service.client
            (
                id         SERIAL PRIMARY KEY,
                fio        VARCHAR(100) NOT NULL,
                birth_date DATE NOT NULL,
                login      VARCHAR(50) UNIQUE NOT NULL,
                password   VARCHAR(50) NOT NULL
            );
                        
            CREATE TABLE IF NOT EXISTS client_service.client_account
            (
                account_number BIGINT DEFAULT FLOOR(random()*(10000000000 - 1000000000)) + 10000000000 PRIMARY KEY,
                client_id      INT NOT NULL REFERENCES client_service.client (id),
                balance        NUMERIC NOT NULL CHECK (balance > 0)
            );
                        
            CREATE TABLE IF NOT EXISTS client_service.client_info(
                id SERIAL PRIMARY KEY,
                client_id INT NOT NULL REFERENCES client_service.client (id),
                phone VARCHAR(20) UNIQUE,
                email VARCHAR(50) UNIQUE
            );
            """;

    /**
     * sql запрос на очищение БД
     * используется для интеграционных тестов, чтобы тесты не зависили от состояния БД
     */
    private static String DELETE_SQL = """
        DELETE FROM client_service.client_account;
        DELETE FROM client_service.client_info;
        DELETE FROM client_service.client;
    """;


    /**
     * метод, создающий сущности БД
     * @param isTest - параметр для определения, для какого сценария происходит подготовка БД: для тестового или обычного
     */
    public static void prepareDatabase(boolean isTest){
        try (Connection connection = ConnectionManager.get();
             var statement = connection.createStatement()) {
            statement.execute(CREATE_SQL);
            if(isTest){
                statement.execute(DELETE_SQL);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
