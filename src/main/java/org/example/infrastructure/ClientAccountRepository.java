package org.example.infrastructure;

import lombok.SneakyThrows;
import org.example.entity.Client;
import org.example.entity.ClientAccount;
import org.example.repository_interface.ClientAccountRepositoryI;
import org.example.util.ConnectionManager;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class ClientAccountRepository implements ClientAccountRepositoryI {
    private static final String INSERT_CLIENT_ACCOUNT_SQL = """
        INSERT INTO client_service.client_account(client_id, balance)
        VALUES (?, ?)
    """;
    private static final String GET_ALL_CLIENT_ACCOUNTS_SQL = """
        SELECT
            account_number,
            client_id, 
            balance
        FROM client_service.client_account
    """;

    private static final String GET_CLIENT_ACCOUNT_BY_CLIENT_ID_SQL
            = GET_ALL_CLIENT_ACCOUNTS_SQL + " WHERE client_id = ?";

    @Override
    public ClientAccount insertClientAccount(ClientAccount clientAccount) {
        try (Connection connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(INSERT_CLIENT_ACCOUNT_SQL, RETURN_GENERATED_KEYS)) {
            preparedStatement.setObject(1, clientAccount.getClientId());
            preparedStatement.setObject(2, clientAccount.getBalance());

            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                clientAccount.setAccountNumber(generatedKeys.getObject("account_number", Long.class));
            }

            return clientAccount;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ClientAccount> findAllClientAccounts() {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_CLIENT_ACCOUNTS_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<ClientAccount> clientAccounts = new ArrayList<>();
            while (resultSet.next()){
                clientAccounts.add(buildEntity(resultSet));
            }

            return clientAccounts;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public void updateAccountsBalanceByRate(List<ClientAccount> clientAccounts){
        Connection connection = null;
        try {
            connection = ConnectionManager.get();
            var statement = connection.createStatement();
            connection.setAutoCommit(false);

            for (ClientAccount clientAccount : clientAccounts) {
                statement.addBatch(
                        "UPDATE client_service.client_account SET balance = " + clientAccount.getBalance() +
                            " WHERE account_number = " + clientAccount.getAccountNumber());
            }
            statement.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new RuntimeException(e);
        } finally {
            connection.close();
        }
    }

    @SneakyThrows
    @Override
    public boolean updateAccountsBalanceByTransfering(Integer senderId, Integer recipientId, Double amount) {
        ClientAccount senderAccount = findAccountByClientId(senderId).get();
        ClientAccount recipientAccount = findAccountByClientId(recipientId).get();
        Connection connection = null;
        try {
            connection = ConnectionManager.get();
            var statement = connection.createStatement();
            connection.setAutoCommit(false);

            BigDecimal senderBalance = senderAccount.getBalance().subtract(new BigDecimal(amount));
            BigDecimal recipientBalance = recipientAccount.getBalance().add(new BigDecimal(amount));

            String updateSQL = "UPDATE client_service.client_account SET balance = ";
            String updateSenderBalance =  updateSQL + senderBalance + " WHERE client_id = " + senderId;
            String updateRecipientBalance = updateSQL + recipientBalance + " WHERE client_id = " + recipientId;

            statement.addBatch(updateSenderBalance);
            statement.addBatch(updateRecipientBalance);

            statement.executeBatch();
            connection.commit();

            return true;
        } catch (SQLException e) {
            connection.rollback();
            throw new RuntimeException(e);
        } finally {
            connection.close();
        }
    }

    private Optional<ClientAccount> findAccountByClientId(Integer clientId) {
        try (Connection connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(GET_CLIENT_ACCOUNT_BY_CLIENT_ID_SQL)) {
            preparedStatement.setObject(1, clientId);

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next()
                    ? Optional.of(buildEntity(resultSet))
                    : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ClientAccount buildEntity(ResultSet resultSet) throws SQLException {
        return ClientAccount.builder()
                .accountNumber(resultSet.getObject("account_number", Long.class))
                .clientId(resultSet.getObject("client_id", Integer.class))
                .balance(resultSet.getObject("balance", BigDecimal.class))
                .build();
    }
}
