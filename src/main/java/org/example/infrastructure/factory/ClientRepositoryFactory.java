package org.example.infrastructure.factory;

import lombok.NoArgsConstructor;
import org.example.infrastructure.ClientRepository;
import org.example.repository_interface.ClientRepositoryI;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ClientRepositoryFactory {
    private static ClientRepositoryI clientRepositoryInstance;

    public static ClientRepositoryI getInstance() {
        if (clientRepositoryInstance == null) {
            clientRepositoryInstance = new ClientRepository();
        }
        return clientRepositoryInstance;
    }
}
