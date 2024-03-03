package org.example.repository_interface;

import org.example.dto.ClientAccountDto;
import org.example.entity.Client;
import org.example.entity.ClientAccount;

import java.util.List;
import java.util.Optional;

public interface ClientAccountRepositoryI {
    ClientAccount insertClientAccount(ClientAccount clientAccount);
    List<ClientAccount> findAllClientAccounts();
    void updateAccountsBalanceByRate(List<ClientAccount> clientAccounts);
    boolean updateAccountsBalanceByTransfering(Integer senderId, Integer recipientId,  Double amount);
}