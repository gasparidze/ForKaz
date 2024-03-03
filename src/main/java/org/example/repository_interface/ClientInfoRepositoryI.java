package org.example.repository_interface;

import org.example.dto.ClientInfoDto;
import org.example.entity.ClientInfo;

import java.util.List;

public interface ClientInfoRepositoryI {
    ClientInfo insertClientInfo(ClientInfo clientInfo);
    boolean findClientsInfoWithLinkedPhoneOrEmail(String phone, String email);
    boolean findClientInfoWithLinkedContact(String contact, String contactType);
    void updateClientContact(String replacedContact, String newContact, String contactType);
    void deleteClientContact(String contact, String contactType);
    List<ClientInfo> findClientsInfoByClientIdAndContact(ClientInfo clientInfo, String contactType);
}
