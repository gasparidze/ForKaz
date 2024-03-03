package org.example.application_logic;

import org.example.application_service.ClientAccountServiceI;
import org.example.dto.ClientAccountDto;
import org.example.entity.Client;
import org.example.entity.ClientAccount;
import org.example.infrastructure.factory.ClientRepositoryFactory;
import org.example.mapper.ClientAccountMapper;
import org.example.repository_interface.ClientAccountRepositoryI;
import org.example.repository_interface.ClientRepositoryI;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClientAccountService implements ClientAccountServiceI {
    private static boolean isLimitExceeded = false;
    private final BigDecimal limitPercentage = new BigDecimal(2.07);
    private final BigDecimal coefficient = new BigDecimal(1.05);
    private List<ClientAccount> allClientAccounts;
    private List<BigDecimal> limitPercentageForAccounts;
    private ClientAccountMapper clientAccountMapper;
    private ClientAccountRepositoryI clientAccountRepository;
    public ClientAccountService(ClientAccountRepositoryI clientAccountRepository){
        this.clientAccountRepository = clientAccountRepository;
        init(clientAccountRepository);
    }

    private void init(ClientAccountRepositoryI clientAccountRepository) {
        allClientAccounts = clientAccountRepository.findAllClientAccounts();
        limitPercentageForAccounts = allClientAccounts.stream()
                .map(account -> account.getBalance().multiply(limitPercentage))
                .collect(Collectors.toList());
        clientAccountMapper = ClientAccountMapper.getInstance();
    }

    @Override
    public ClientAccount createClientAccount(ClientAccountDto clientAccountDto) {
        ClientAccount clientAccount = clientAccountMapper.map(clientAccountDto);
        return clientAccountRepository.insertClientAccount(clientAccount);
    }


    @Override
    public void accrueInterestToClients() {
        List<ClientAccount> allClientAccounts = clientAccountRepository.findAllClientAccounts();

        allClientAccounts.forEach(clientAccount -> {
            BigDecimal newBalance = clientAccount.getBalance().multiply(coefficient);
            if(newBalance.compareTo(limitPercentageForAccounts.get(allClientAccounts.indexOf(clientAccount))) < 0){
                clientAccount.setBalance(newBalance);
            } else {
              isLimitExceeded = true;
            }
        });

        if(!isLimitExceeded){
            clientAccountRepository.updateAccountsBalanceByRate(allClientAccounts);
        }
    }

    @Override
    public boolean transferMoney(Integer senderId, Integer recipientId,  Double amount){
        return clientAccountRepository.updateAccountsBalanceByTransfering(senderId, recipientId, amount);
    }
}
