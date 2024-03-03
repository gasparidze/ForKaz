package org.example.application_service;

import org.example.dto.ClientAccountDto;
import org.example.entity.ClientAccount;

import java.util.List;

public interface ClientAccountServiceI {
    ClientAccount createClientAccount(ClientAccountDto clientAccountDto);
    void accrueInterestToClients();

    boolean transferMoney(Integer senderId, Integer recipientId,  Double amount);
}