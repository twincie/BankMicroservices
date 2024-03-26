package com.example.banktransaction.repository;

import com.example.banktransaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByWalletId(Long id);

    Transaction findByIdAndWalletId(Long Id, Long walletId);

    Boolean existsByIdAndWalletId(Long Id, Long walletId);

//    List<Transaction> findByDate(LocalDate date);

    List<Transaction> findByDateBetween(LocalDate firstDayOfMonth, LocalDate lastDayOfMonth);


    List<Transaction> findByWalletIdAndDateBetween(Long id, LocalDate startDate, LocalDate endDate);
}
