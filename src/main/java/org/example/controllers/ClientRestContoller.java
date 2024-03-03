package org.example.controllers;

import org.apache.commons.validator.routines.EmailValidator;
import org.example.util.JDBCStarter;
import org.example.application_service.ClientAccountServiceI;
import org.example.application_service.ClientInfoServiceI;
import org.example.application_service.ClientServiceI;
import org.example.application_service.exception.ValidationException;
import org.example.dto.ClientAccountDto;
import org.example.dto.ClientDto;
import org.example.dto.ClientInfoDto;
import org.example.entity.Client;
import org.example.infrastructure.factory.ClientAccountServiceFactory;
import org.example.infrastructure.factory.ClientInfoServiceFactory;
import org.example.infrastructure.factory.ClientServiceFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ClientRestContoller {
    private final ClientServiceI clientService;
    private final ClientAccountServiceI clientAccountService;
    private final ClientInfoServiceI clientInfoService;

    public ClientRestContoller(){
        clientService = ClientServiceFactory.getInstance();
        clientAccountService = ClientAccountServiceFactory.getInstance();
        clientInfoService = ClientInfoServiceFactory.getInstance();
    }

    public static void main(String[] args) {
        JDBCStarter.prepareDatabase(false);
        ClientRestContoller clientRestContoller = new ClientRestContoller();

    }

    void registerNewClient(
            String fio,
            LocalDate birthDate,
            String login,
            String password,
            Integer balance,
            String phone,
            String email){

        boolean hasClientWithLogin = clientService.hasClientWithLogin(login);
        boolean haveClientsInfoWithLinkedPhoneOrEmail
                = clientInfoService.haveClientsInfoWithLinkedPhoneOrEmail(phone, email);

        if(!hasClientWithLogin && !haveClientsInfoWithLinkedPhoneOrEmail){
            ClientDto clientDto = getClientDto(fio, birthDate, login, password);
            Client registeredClient = clientService.createClient(clientDto);

            ClientAccountDto clientAccountDto = getClientAccountDto(new BigDecimal(balance), registeredClient.getId());
            clientAccountService.createClientAccount(clientAccountDto);

            ClientInfoDto clientInfoDto = getClientInfoDto(phone, email, registeredClient.getId());
            clientInfoService.createClientInfo(clientInfoDto);
        }
    }

    void addNewPhoneToClient(int clientId, String phone){
        ClientInfoDto clientInfoDto = ClientInfoDto
                .builder()
                .clientId(clientId)
                .phone(phone)
                .build();
        clientInfoService.addPhoneToClient(clientInfoDto);
    }

    void addNewEmailToClient(int clientId, String email){
        ClientInfoDto clientInfoDto = ClientInfoDto
                .builder()
                .clientId(clientId)
                .email(email)
                .build();
        clientInfoService.addPhoneToClient(clientInfoDto);
    }

    void changePhone(String replacedPhone, String newPhone){
        clientInfoService.changeClientPhone(replacedPhone, newPhone);
    }

    void changeEmail(String replacedEmail, String newEmail){
        clientInfoService.changeClientEmail(replacedEmail, newEmail);
    }

    void deleteClientPhone(Integer clientId, String phone){
        ClientInfoDto clientInfoDto = ClientInfoDto
                .builder()
                .clientId(clientId)
                .phone(phone)
                .build();
        clientInfoService.deleteClientPhone(clientInfoDto);
    }

    void deleteClientEmail(Integer clientId, String email){
        ClientInfoDto clientInfoDto = ClientInfoDto
                .builder()
                .clientId(clientId)
                .email(email)
                .build();
        clientInfoService.deleteClientEmail(clientInfoDto);
    }

    void getClientsByBirthDate(LocalDate date, String sortBy){
        List<Client> clientsByBirthDate = clientService.getClientsByBirthDate(date, sortBy);
        System.out.println("Клиенты с датой рождения старше " + date + ":");
        clientsByBirthDate.forEach(client -> {
            int i = 0;
            System.out.println(++i + ". " + client);}
        );
        System.out.println("\n");
    }

    void getClientByPhone(String phone){
        System.out.println("Клиент с данным номером телефона: " + clientService.getClientByPhone(phone).orElse(null));
    }

    void getClientByEmail(String email){
        System.out.println("Клиент с данным email: " + clientService.getClientByEmail(email).orElse(null));
    }

    void getClientByFIO(String fio){
        List<Client> clientsByFIO = clientService.getClientByFIO(fio);
        System.out.println("Клиенты с похожим ФИО :");
        clientsByFIO.forEach(client -> {
            int i = 0;
            System.out.println(++i + ". " + client);}
        );
        System.out.println("\n");
    }

    void transferMoney(Integer senderId, Integer recipientId,  Double amount){
        if(clientAccountService.transferMoney(senderId, recipientId, amount)){
            System.out.println("Деньги успешно переведены!");
        }
    }

    void accrueInterestToClients(){
        clientAccountService.accrueInterestToClients();
    }

    private ClientInfoDto getClientInfoDto(String phone, String email, int id) {
        return ClientInfoDto
                .builder()
                .clientId(id)
                .phone(phone)
                .email(email)
                .build();
    }

    private ClientAccountDto getClientAccountDto(BigDecimal balance, int id) {
        return ClientAccountDto.builder()
                .clientId(id)
                .balance(balance)
                .build();
    }

    private ClientDto getClientDto(String fio, LocalDate birthDate, String login, String password) {
        return ClientDto.builder()
                .fio(fio)
                .birthDate(birthDate)
                .login(login)
                .password(password)
                .build();
    }
}
