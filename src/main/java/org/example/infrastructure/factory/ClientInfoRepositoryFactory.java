package org.example.infrastructure.factory;

import lombok.NoArgsConstructor;
import org.example.infrastructure.ClientAccountRepository;
import org.example.infrastructure.ClientInfoRepository;
import org.example.repository_interface.ClientAccountRepositoryI;
import org.example.repository_interface.ClientInfoRepositoryI;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ClientInfoRepositoryFactory {
    private static ClientInfoRepositoryI clientInfoRepositoryInstance;

    public static ClientInfoRepositoryI getInstance() {
        if (clientInfoRepositoryInstance == null) {
            clientInfoRepositoryInstance = new ClientInfoRepository();
        }
        return clientInfoRepositoryInstance;
    }
}
