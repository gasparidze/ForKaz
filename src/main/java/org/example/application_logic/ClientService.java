package org.example.application_logic;

import org.example.application_service.ClientServiceI;
import org.example.dto.ClientDto;
import org.example.entity.Client;
import org.example.mapper.ClientMapper;
import org.example.repository_interface.ClientRepositoryI;
import org.example.util.ValidationUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ClientService implements ClientServiceI {
    private final ClientMapper clientMapper;
    private final ClientRepositoryI clientRepository;

    public ClientService(ClientRepositoryI clientRepository){
        clientMapper = ClientMapper.getInstance();
        this.clientRepository = clientRepository;
    }

    @Override
    public Client createClient(ClientDto clientDto) {
        Client client = clientMapper.map(clientDto);
        return clientRepository.insertClient(client);
    }

    @Override
    public boolean hasClientWithLogin(String login) {
        return clientRepository.findClientByLogin(login).isPresent();
    }

    @Override
    public List<Client> getClientsByBirthDate(LocalDate birthDate, String sortBy) {
        return clientRepository.findClientsByBirthDate(birthDate, sortBy);
    }

    @Override
    public Optional<Client> getClientByPhone(String phone) {
        ValidationUtil.checkPhone(phone);
        return clientRepository.findClientByPhone(phone);
    }

    @Override
    public Optional<Client> getClientByEmail(String email) {
        ValidationUtil.checkEmail(email);
        return clientRepository.findClientByEmail(email);
    }

    @Override
    public List<Client> getClientByFIO(String fio) {
        return clientRepository.findClientByFIO(fio);
    }
}
