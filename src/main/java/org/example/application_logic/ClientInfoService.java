package org.example.application_logic;

import org.apache.commons.validator.routines.EmailValidator;
import org.example.application_service.ClientInfoServiceI;
import org.example.application_service.exception.ClientInfoException;
import org.example.application_service.exception.ValidationException;
import org.example.dto.ClientInfoDto;
import org.example.entity.ClientInfo;
import org.example.mapper.ClientInfoMapper;
import org.example.repository_interface.ClientInfoRepositoryI;
import org.example.util.ValidationUtil;
import org.example.validator.PhoneValidator;

import java.util.List;
import java.util.stream.Collectors;

public class ClientInfoService implements ClientInfoServiceI {
    private final ClientInfoMapper clientInfoMapper;
    private final ClientInfoRepositoryI clientInfoRepository;

    public ClientInfoService(ClientInfoRepositoryI clientInfoRepository){
        clientInfoMapper = ClientInfoMapper.getInstance();
        this.clientInfoRepository = clientInfoRepository;
    }

    @Override
    public ClientInfo createClientInfo(ClientInfoDto clientInfoDto) {
        ValidationUtil.checkPhone(clientInfoDto.getPhone());
        ValidationUtil.checkEmail(clientInfoDto.getEmail());
        ClientInfo clientInfo = clientInfoMapper.map(clientInfoDto);
        return clientInfoRepository.insertClientInfo(clientInfo);
    }

    @Override
    public void addPhoneToClient(ClientInfoDto clientInfoDto) {
        ValidationUtil.checkPhone(clientInfoDto.getPhone());
        if(clientInfoRepository.findClientInfoWithLinkedContact(clientInfoDto.getPhone(), "phone")){
            throw new ClientInfoException("Данный номер уже зарегистрирован в системе");
        }
        createClientInfo(clientInfoDto);
    }

    @Override
    public void addEmailToClient(ClientInfoDto clientInfoDto) {
        ValidationUtil.checkEmail(clientInfoDto.getEmail());
        if(clientInfoRepository.findClientInfoWithLinkedContact(clientInfoDto.getEmail(), "email")){
            throw new ClientInfoException("Данный email уже зарегистрирован в системе");
        }
        createClientInfo(clientInfoDto);
    }

    @Override
    public void changeClientPhone(String replacedPhone, String newPhone) {
        ValidationUtil.checkPhone(replacedPhone);
        ValidationUtil.checkPhone(newPhone);
        clientInfoRepository.updateClientContact(replacedPhone, newPhone, "phone");
    }

    @Override
    public void changeClientEmail(String replacedEmail, String newEmail) {
        ValidationUtil.checkEmail(replacedEmail);
        ValidationUtil.checkEmail(newEmail);
        clientInfoRepository.updateClientContact(replacedEmail, newEmail, "email");
    }

    @Override
    public void deleteClientPhone(ClientInfoDto clientInfoDto) {
        ValidationUtil.checkPhone(clientInfoDto.getPhone());
        ClientInfo clientInfo = clientInfoMapper.map(clientInfoDto);
        List<ClientInfo> clientsInfoByClientId
                = clientInfoRepository.findClientsInfoByClientIdAndContact(clientInfo, "phone");
        List<String> clientPhones = clientsInfoByClientId.stream().map(el -> el.getPhone()).collect(Collectors.toList());
        if(clientPhones.size() == 1){
            throw new ClientInfoException("Нельзя удалить единственный номер телефона клиента");
        }
        clientInfoRepository.deleteClientContact(clientInfoDto.getPhone(), "phone");
    }

    @Override
    public void deleteClientEmail(ClientInfoDto clientInfoDto) {
        ValidationUtil.checkEmail(clientInfoDto.getEmail());
        ClientInfo clientInfo = clientInfoMapper.map(clientInfoDto);
        List<ClientInfo> clientsInfoByClientId
                = clientInfoRepository.findClientsInfoByClientIdAndContact(clientInfo, "email");
        List<String> clientPhones = clientsInfoByClientId.stream().map(el -> el.getEmail()).collect(Collectors.toList());
        if(clientPhones.size() == 1){
            throw new ClientInfoException("Нельзя удалить единственный email клиента");
        }
        clientInfoRepository.deleteClientContact(clientInfoDto.getEmail(), "email");
    }

    @Override
    public boolean haveClientsInfoWithLinkedPhoneOrEmail(String phone, String email) {
        ValidationUtil.checkPhone(phone);
        ValidationUtil.checkEmail(email);
        if(clientInfoRepository.findClientsInfoWithLinkedPhoneOrEmail(phone, email)){
            throw new ClientInfoException("Клиент с таким телефоном и/или почтой уже существует!");
        }

        return false;
    }
}
