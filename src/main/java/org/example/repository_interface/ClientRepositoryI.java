package org.example.repository_interface;

import org.example.entity.Client;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ClientRepositoryI {
    Client insertClient(Client client);
    List<Client> findClientsByBirthDate(LocalDate birthDate, String sortBy);
    Optional<Client> findClientByPhone(String phone);
    Optional<Client> findClientByEmail(String email);
    List<Client> findClientByFIO(String fio);
    Optional<Client> findClientByLogin(String login);
}
