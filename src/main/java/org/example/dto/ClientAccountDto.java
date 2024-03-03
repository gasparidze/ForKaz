package org.example.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class ClientAccountDto {
    private Integer clientId;
    private BigDecimal balance;
}
