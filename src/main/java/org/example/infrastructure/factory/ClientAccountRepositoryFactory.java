package org.example.infrastructure.factory;

import lombok.NoArgsConstructor;
import org.example.infrastructure.ClientAccountRepository;
import org.example.infrastructure.ClientRepository;
import org.example.repository_interface.ClientAccountRepositoryI;
import org.example.repository_interface.ClientRepositoryI;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ClientAccountRepositoryFactory {
    private static ClientAccountRepositoryI clientAccountRepositoryInstance;

    public static ClientAccountRepositoryI getInstance() {
        if (clientAccountRepositoryInstance == null) {
            clientAccountRepositoryInstance = new ClientAccountRepository();
        }
        return clientAccountRepositoryInstance;
    }
}
