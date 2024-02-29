package com.example.bankusers.external;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class Wallet {
    private Long id;
    private String accountNumber;
    private BigDecimal amount;
}
