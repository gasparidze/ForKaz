package org.example.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientInfo {
    private Integer id;
    private Integer clientId;
    private String phone;
    private String email;
}
