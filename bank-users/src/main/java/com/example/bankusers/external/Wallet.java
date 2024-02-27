package com.example.bankusers.external;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Random;

@Setter
@Getter
public class Wallet {
    private Long id;
    private String accountNumber = accountNumberGenerator().toString();
    private BigDecimal amount = BigDecimal.ZERO;

    public Long accountNumberGenerator(){
        Random random = new Random();
        int randomNumber = 1000000000 + random.nextInt(900000000);
        return (long) randomNumber;
    }
}
