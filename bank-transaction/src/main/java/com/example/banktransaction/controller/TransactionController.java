package com.example.banktransaction.controller;

import com.example.banktransaction.entity.Response;
import com.example.banktransaction.entity.Transaction;
import com.example.banktransaction.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Response> readAllTransactions(@RequestHeader("role") String role){
        Response response = transactionService.readAll(role);
            return ResponseEntity.ok(response);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<Response> readOneTransaction(@PathVariable Long transactionId, @RequestHeader("loggedInWalletId") String walletId){
        Response response = transactionService.readOne(transactionId, walletId);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/details")
    public ResponseEntity<Response> readAllUserTransactions(@RequestHeader("loggedInWalletId") String walletId){
        Response response = transactionService.readOneUserTransactions(walletId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<Response> updateTransaction(@RequestHeader("loggedInWalletId") String walletId, @PathVariable Long transactionId, @RequestBody Transaction transaction){
        Response response = transactionService.update(walletId, transactionId, transaction);
        return  ResponseEntity.ok(response);
    }
    @DeleteMapping("/{transactionId}")
    public void deleteTransaction(@RequestHeader("loggedInWalletId") String walletId, @PathVariable Long transactionId){
        transactionService.delete(walletId, transactionId);
    }
}
