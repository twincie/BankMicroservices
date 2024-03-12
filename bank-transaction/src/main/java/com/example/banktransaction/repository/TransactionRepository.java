package com.example.banktransaction.repository;

import com.example.banktransaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByWalletId(Long id);

    Transaction findByIdAndWalletId(Long Id, Long walletId);
}
