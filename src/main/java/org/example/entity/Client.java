package org.example.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
@Builder
public class Client {
    private Integer id;
    private String fio;
    private LocalDate birthDate;
    private String login;
    private String password;
}
