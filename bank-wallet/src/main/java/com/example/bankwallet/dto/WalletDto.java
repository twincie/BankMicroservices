package com.example.bankwallet.dto;

import com.example.bankwallet.external.Transaction;
import com.example.bankwallet.external.Users;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class WalletDto {
    private Long id;
    private String accountNumber;
    private BigDecimal amount;
    private List<Transaction> transaction;
}
