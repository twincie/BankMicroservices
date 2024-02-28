package com.example.bankwallet.external;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class Transaction {
    private long id;
    private TransactionType type;
    private BigDecimal amount;
    private LocalDate date = LocalDate.now();
    private LocalTime time = LocalTime.now();
}
