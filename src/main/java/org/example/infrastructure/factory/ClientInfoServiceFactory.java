package org.example.infrastructure.factory;

import lombok.NoArgsConstructor;
import org.example.application_logic.ClientInfoService;
import org.example.application_service.ClientInfoServiceI;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ClientInfoServiceFactory {
    private static ClientInfoServiceI clientInfoServiceInstance;

    public static ClientInfoServiceI getInstance() {
        if (clientInfoServiceInstance == null) {
            clientInfoServiceInstance = new ClientInfoService(ClientInfoRepositoryFactory.getInstance());
        }
        return clientInfoServiceInstance;
    }
}
