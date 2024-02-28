package com.example.bankusers.external;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Random;

@Setter
@Getter
public class Wallet {
    private Long id;
    private String accountNumber;
    private BigDecimal amount;

    @Override
    public String toString() {
        return "Id: "+ id +
                " account number: "+ accountNumber +
                " ammount: "+ amount;
    }
}
