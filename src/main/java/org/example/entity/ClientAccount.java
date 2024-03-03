package org.example.entity;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ClientAccount {
    private Long accountNumber;
    private Integer clientId;
    private BigDecimal balance;
}