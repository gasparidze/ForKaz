package org.example.infrastructure;

import org.example.dto.ClientInfoDto;
import org.example.entity.ClientInfo;
import org.example.repository_interface.ClientInfoRepositoryI;
import org.example.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class ClientInfoRepository implements ClientInfoRepositoryI {
    private static final String INSERT_CLIENT_INFO_SQL = """
        INSERT INTO client_service.client_info (client_id, phone, email)
        VALUES (?, ?, ?)      
    """;
    private static final String GET_CLIENT_INFO_BY_PHONE_SQL = """
        SELECT 
            id
        FROM client_service.client_info
        WHERE phone = ?       
    """;
    private static final String GET_CLIENT_INFO_BY_EMAIL_SQL = """
        SELECT 
            id
        FROM client_service.client_info
        WHERE email = ?       
    """;
    private static final String GET_CLIENTS_INFO_BY_PHONE_OR_EMAIL_SQL
            = GET_CLIENT_INFO_BY_PHONE_SQL + " OR email = ?";

    private static final String UPDATE_CLIENT_INFO_PHONE_SQL = """
        UPDATE client_service.client_info
        SET phone = ?
        WHERE phone = ?    
    """;
    private static final String UPDATE_CLIENT_INFO_EMAIL_SQL = """
        UPDATE client_service.client_info
        SET email = ?
        WHERE email = ?        
    """;
    private static final String UPDATE_CLIENT_INFO_EMAIL_TO_NULL_SQL = """
        UPDATE client_service.client_info
        SET email = NULL
        WHERE email = ?        
    """;

    private static final String UPDATE_CLIENT_INFO_PHONE_TO_NULL_SQL = """
        UPDATE client_service.client_info
        SET phone = NULL
        WHERE phone = ?        
    """;

    private static final String GET_CLIENTS_INFO_BY_CLIENT_ID_AND_PHONE_SQL = """
        SELECT 
            id,
            client_id,
            phone,
            email
        FROM client_service.client_info
        WHERE client_id = ?
        AND phone IS NOT NULL
    """;

    private static final String GET_CLIENTS_INFO_BY_CLIENT_ID_AND_EMAIL_SQL = """
        SELECT 
            id,
            client_id,
            phone,
            email
        FROM client_service.client_info
        WHERE client_id = ?
        AND email IS NOT NULL
    """;

    @Override
    public ClientInfo insertClientInfo(ClientInfo clientInfo) {
        try (Connection connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(INSERT_CLIENT_INFO_SQL, RETURN_GENERATED_KEYS)) {
            preparedStatement.setObject(1, clientInfo.getClientId());
            preparedStatement.setObject(2, clientInfo.getPhone());
            preparedStatement.setObject(3, clientInfo.getEmail());

            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                clientInfo.setId(generatedKeys.getObject("id", Integer.class));
            }

            return clientInfo;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean findClientsInfoWithLinkedPhoneOrEmail(String phone, String email) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_CLIENTS_INFO_BY_PHONE_OR_EMAIL_SQL)) {
            preparedStatement.setObject(1, phone);
            preparedStatement.setObject(2, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean findClientInfoWithLinkedContact(String contact, String contactType) {
        boolean isPhone = contactType == "phone";
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = isPhone
                     ? connection.prepareStatement(GET_CLIENT_INFO_BY_PHONE_SQL)
                     : connection.prepareStatement(GET_CLIENT_INFO_BY_EMAIL_SQL)) {
            preparedStatement.setObject(1, contact);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateClientContact(String replacedContact, String newContact, String contactType) {
        boolean isPhoneUpdated = contactType == "phone";
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement
                     = isPhoneUpdated
                     ? connection.prepareStatement(UPDATE_CLIENT_INFO_PHONE_SQL)
                     : connection.prepareStatement(UPDATE_CLIENT_INFO_EMAIL_SQL)) {
            if(isPhoneUpdated){
                preparedStatement.setObject(1, newContact);
                preparedStatement.setObject(2, replacedContact);
            } else {
                preparedStatement.setObject(1, newContact);
                preparedStatement.setObject(2, replacedContact);
            }

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteClientContact(String contact, String contactType){
        boolean isPhone = contactType == "phone";
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = isPhone
                     ? connection.prepareStatement(UPDATE_CLIENT_INFO_PHONE_TO_NULL_SQL)
                     : connection.prepareStatement(UPDATE_CLIENT_INFO_EMAIL_TO_NULL_SQL)) {
            preparedStatement.setObject(1, contact);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ClientInfo> findClientsInfoByClientIdAndContact(ClientInfo clientInfo, String contactType){
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = contactType == "phone"
                     ? connection.prepareStatement(GET_CLIENTS_INFO_BY_CLIENT_ID_AND_PHONE_SQL)
                     : connection.prepareStatement(GET_CLIENTS_INFO_BY_CLIENT_ID_AND_EMAIL_SQL)) {
            preparedStatement.setObject(1, clientInfo.getClientId());

            ResultSet resultSet = preparedStatement.executeQuery();
            List<ClientInfo> clientInfoList = new ArrayList<>();
            while (resultSet.next()){
                clientInfoList.add(buildEntity(resultSet));
            }

            return clientInfoList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ClientInfo buildEntity(ResultSet resultSet) throws SQLException {
        return ClientInfo
                .builder()
                .clientId(resultSet.getObject("client_id", Integer.class))
                .phone(resultSet.getObject("phone", String.class))
                .email(resultSet.getObject("email", String.class))
                .build();
    }
}
