package org.example.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.Date;

@Value
@Builder
public class ClientDto {
    private String fio;
    private LocalDate birthDate;
    private String login;
    private String password;
}
