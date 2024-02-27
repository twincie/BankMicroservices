package com.example.banktransaction.entity;

import com.example.banktransaction.entity.TransactionType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private TransactionType type;
    private BigDecimal amount;
    private LocalDate date = LocalDate.now();
    private LocalTime time = LocalTime.now();
    private Long walletId;

//    @JsonIgnore
//    @ManyToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name="wallet_id")
//    private Wallet wallet;
}
