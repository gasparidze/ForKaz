package org.example.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientInfoDto {
    private Integer clientId;
    private String phone;
    private String email;
}
