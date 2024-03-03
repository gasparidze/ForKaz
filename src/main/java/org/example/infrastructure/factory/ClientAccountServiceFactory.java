package org.example.infrastructure.factory;

import lombok.NoArgsConstructor;
import org.example.application_logic.ClientAccountService;
import org.example.application_logic.ClientService;
import org.example.application_service.ClientAccountServiceI;
import org.example.application_service.ClientServiceI;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ClientAccountServiceFactory {
    private static ClientAccountServiceI clientAccountServiceInstance;

    public static ClientAccountServiceI getInstance() {
        if (clientAccountServiceInstance == null) {
            clientAccountServiceInstance = new ClientAccountService(ClientAccountRepositoryFactory.getInstance());
        }
        return clientAccountServiceInstance;
    }
}
