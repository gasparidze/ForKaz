package org.example.infrastructure.factory;

import lombok.NoArgsConstructor;
import org.example.application_logic.ClientInfoService;
import org.example.application_logic.ClientService;
import org.example.application_service.ClientInfoServiceI;
import org.example.application_service.ClientServiceI;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ClientServiceFactory {
    private static ClientServiceI clientServiceInstance;

    public static ClientServiceI getInstance() {
        if (clientServiceInstance == null) {
            clientServiceInstance = new ClientService(ClientRepositoryFactory.getInstance());
        }
        return clientServiceInstance;
    }
}
