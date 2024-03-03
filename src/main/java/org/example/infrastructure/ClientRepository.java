package org.example.infrastructure;

import org.example.entity.Client;
import org.example.repository_interface.ClientRepositoryI;
import org.example.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class ClientRepository implements ClientRepositoryI {
    private static final String INSERT_CLIENT_SQL = """
            INSERT INTO client_service.client (fio, birth_date, login, password)
            VALUES (?, ?, ?, ?)
    """;
    private static final String GET_ALL_CLIENTS_SQL = """
            SELECT
                id,
                fio,
                birth_date,
                login,
                password
            FROM client_service.client
    """;
    private static final String GET_CLIENT_BY_LOGIN_SQL = GET_ALL_CLIENTS_SQL + " WHERE login = ?";

    private static final String GET_CLIENTS_BY_BIRTH_DATE_SQL
            = GET_ALL_CLIENTS_SQL + "  WHERE birth_date > ? ORDER BY ?";

    private static final String GET_CLIENT_BY_FIO_SQL = GET_ALL_CLIENTS_SQL + " WHERE fio LIKE ?";
    private static final String GET_CLIENT_BY_PHONE_SQL = """
            SELECT
                client.id,
                fio,
                birth_date,
                login,
                password
            FROM client_service.client
            JOIN client_service.client_info ci on client.id = ci.client_id
            WHERE phone = ?
    """;
    private static final String GET_CLIENT_BY_EMAIL_SQL = """
            SELECT
                client.id,
                fio,
                birth_date,
                login,
                password
            FROM client_service.client
            JOIN client_service.client_info ci on client.id = ci.client_id
            WHERE email = ?
    """;

    @Override
    public Client insertClient(Client client) {
        try (Connection connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(INSERT_CLIENT_SQL, RETURN_GENERATED_KEYS)) {
            preparedStatement.setObject(1, client.getFio());
            preparedStatement.setObject(2, client.getBirthDate());
            preparedStatement.setObject(3, client.getLogin());
            preparedStatement.setObject(4, client.getPassword());

            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                client.setId(generatedKeys.getObject("id", Integer.class));
            }

            return client;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Client> findClientsByBirthDate(LocalDate birthDate, String sortBy) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_CLIENTS_BY_BIRTH_DATE_SQL)) {
            preparedStatement.setObject(1, birthDate);
            preparedStatement.setObject(2, sortBy);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Client> clients = new ArrayList<>();
            while (resultSet.next()){
                clients.add(buildEntity(resultSet));
            }

            return clients;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Client> findClientByPhone(String phone) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_CLIENT_BY_PHONE_SQL)) {
            preparedStatement.setObject(1, phone);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next()
                    ? Optional.of(buildEntity(resultSet))
                    : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Client> findClientByEmail(String email) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_CLIENT_BY_EMAIL_SQL)) {
            preparedStatement.setObject(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next()
                    ? Optional.of(buildEntity(resultSet))
                    : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Client> findClientByFIO(String fio) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_CLIENT_BY_FIO_SQL)) {
            preparedStatement.setObject(1, "%" + fio + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Client> clients = new ArrayList<>();
            while (resultSet.next()){
                clients.add(buildEntity(resultSet));
            }

            return clients;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Client> findClientByLogin(String login) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_CLIENT_BY_LOGIN_SQL)) {
            preparedStatement.setObject(1, login);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next()
                    ? Optional.of(buildEntity(resultSet))
                    : Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Client buildEntity(ResultSet resultSet) throws SQLException {
        return Client.builder()
                .id(resultSet.getObject("id", Integer.class))
                .fio(resultSet.getObject("fio", String.class))
                .birthDate(resultSet.getObject("birth_date", LocalDate.class))
                .login(resultSet.getObject("login", String.class))
                .password(resultSet.getObject("password", String.class))
                .build();
    }
}