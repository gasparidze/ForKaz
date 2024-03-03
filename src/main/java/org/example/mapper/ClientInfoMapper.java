package org.example.mapper;

import lombok.NoArgsConstructor;
import org.example.dto.ClientDto;
import org.example.dto.ClientInfoDto;
import org.example.entity.ClientInfo;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ClientInfoMapper implements Mapper<ClientInfoDto, ClientInfo>{
    private static final ClientInfoMapper INSTANCE = new ClientInfoMapper();
    public static ClientInfoMapper getInstance() {
        return INSTANCE;
    }
    @Override
    public ClientInfo map(ClientInfoDto object) {
        return ClientInfo.builder()
                .clientId(object.getClientId())
                .phone(object.getPhone())
                .email(object.getEmail())
                .build();
    }
}
