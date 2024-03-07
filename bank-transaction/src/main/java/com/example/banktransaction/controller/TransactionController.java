package com.example.banktransaction.controller;

import com.example.banktransaction.entity.Transaction;
import com.example.banktransaction.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public Transaction createTransaction(@RequestBody Transaction transaction){
        return transactionService.create(transaction);
    }
    @GetMapping("/all")
    public List<Transaction> readAllTransactions(){
        return transactionService.readAll();
    }
    @GetMapping("/{transactionId}")
    public ResponseEntity<Optional<Transaction>> readOneTransaction(@RequestParam Long walletId, @PathVariable Long transactionId, @RequestHeader("loggedInWalletId") String walletid){
        return transactionService.readOne(walletId,transactionId, walletid);
    }
    @GetMapping
    public List<Transaction> readAllUserTransactions(@RequestParam Long walletId, @RequestHeader("loggedInWalletId") String walletid){
        return transactionService.readOneUserTransactions(walletId, walletid);
    }
    @GetMapping("/all/wallet")
    public List<Transaction> walletReadAllUserTransactions(@RequestParam Long walletId){
        return transactionService.walletReadOneUserTransactions(walletId);
    }
    @PutMapping("/{transactionId}")
    public Transaction updateTransaction(@RequestParam Long walletId, @PathVariable Long transactionId, @RequestBody Transaction transaction, @RequestHeader("loggedInWalletId") String walletid){
        return transactionService.update(walletId, transactionId, transaction, walletid);
    }
    @DeleteMapping("/{transactionId}")
    public void deleteTransaction(@RequestParam Long walletId, @PathVariable Long transactionId, @RequestHeader("loggedInWalletId") String walletid){
        transactionService.delete(walletId, transactionId, walletid);
    }
}
