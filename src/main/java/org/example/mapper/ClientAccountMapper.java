package org.example.mapper;

import lombok.NoArgsConstructor;
import org.example.dto.ClientAccountDto;
import org.example.dto.ClientDto;
import org.example.entity.Client;
import org.example.entity.ClientAccount;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ClientAccountMapper implements Mapper<ClientAccountDto, ClientAccount> {
    private static final ClientAccountMapper INSTANCE = new ClientAccountMapper();
    public static ClientAccountMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public ClientAccount map(ClientAccountDto object) {
        return ClientAccount.builder()
                .clientId(object.getClientId())
                .balance(object.getBalance())
                .build();
    }
}