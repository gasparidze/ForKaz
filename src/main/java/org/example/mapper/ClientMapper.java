package org.example.mapper;

import lombok.NoArgsConstructor;
import org.example.dto.ClientDto;
import org.example.entity.Client;

import java.util.ArrayList;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ClientMapper implements Mapper<ClientDto, Client> {
    private static final ClientMapper INSTANCE = new ClientMapper();
    public static ClientMapper getInstance() {
        return INSTANCE;
    }
    @Override
    public Client map(ClientDto object) {
        return Client.builder()
                .fio(object.getFio())
                .birthDate(object.getBirthDate())
                .login(object.getLogin())
                .password(object.getPassword())
                .build();
    }
}
