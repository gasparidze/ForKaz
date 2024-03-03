package org.example.application_service;

import org.example.dto.ClientInfoDto;
import org.example.entity.ClientInfo;

public interface ClientInfoServiceI {
    ClientInfo createClientInfo(ClientInfoDto clientInfoDto);
    void addPhoneToClient(ClientInfoDto clientInfoDto);
    void changeClientPhone(String replacedPhone, String phone);
    void deleteClientPhone(ClientInfoDto clientInfoDto);
    void addEmailToClient(ClientInfoDto clientInfoDto);
    void changeClientEmail(String replacedEmail, String email);
    void deleteClientEmail(ClientInfoDto clientInfoDto);
    boolean haveClientsInfoWithLinkedPhoneOrEmail(String phone, String email);
}