package org.example.application_service;

import org.example.dto.ClientDto;
import org.example.entity.Client;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ClientServiceI {
    Client createClient(ClientDto clientDto);
    boolean hasClientWithLogin(String login);
    List<Client> getClientsByBirthDate(LocalDate birthDate, String sortBy);
    Optional<Client> getClientByPhone(String phone);
    Optional<Client> getClientByEmail(String email);
    List<Client> getClientByFIO(String fio);
}