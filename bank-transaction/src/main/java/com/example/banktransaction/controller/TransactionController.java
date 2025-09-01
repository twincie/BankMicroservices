package com.example.banktransaction.controller;

import com.example.banktransaction.dto.DefaultApiResponse;
import com.example.banktransaction.dto.MonthlyStatementRequestDto;
import com.example.banktransaction.dto.StatementRequestDto;
import com.example.banktransaction.entity.Response;
import com.example.banktransaction.entity.Transaction;
import com.example.banktransaction.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public DefaultApiResponse createTransaction(@RequestBody Transaction transaction){
        return transactionService.create(transaction);
    }

    @GetMapping("/all")
    public DefaultApiResponse readAllTransactions(@RequestHeader("role") String role){
        return transactionService.readAll(role);
    }

    @GetMapping("/{transactionId}")
    public DefaultApiResponse readOneTransaction(@PathVariable Long transactionId, @RequestHeader("loggedInWalletId") String walletId){
        return transactionService.readOne(transactionId, walletId);
    }
    @GetMapping("/details")
    public DefaultApiResponse readAllUserTransactions(@RequestHeader("loggedInWalletId") String walletId){
        return transactionService.readOneUserTransactions(walletId);
    }

    @PutMapping("/{transactionId}")
    public DefaultApiResponse updateTransaction(@RequestHeader("loggedInWalletId") String walletId, @PathVariable Long transactionId, @RequestBody Transaction transaction){
        return transactionService.update(walletId, transactionId, transaction);
    }
    @DeleteMapping("/{transactionId}")
    public DefaultApiResponse deleteTransaction(@RequestHeader("loggedInWalletId") String walletId, @PathVariable Long transactionId){
        return transactionService.delete(walletId, transactionId);
    }

    @GetMapping("monthly/statement")
    public DefaultApiResponse getMonthlyStatement(@RequestHeader("loggedInWalletId") String walletId,
                                                        @RequestBody MonthlyStatementRequestDto monthlyStatementRequestDto) throws IOException {
//        Response response = transactionService.monthlyStatement(walletId, monthlyStatementRequestDto.getYear(), monthlyStatementRequestDto.getMonth());
        return transactionService.monthlyStatement(walletId, monthlyStatementRequestDto.getYear(), monthlyStatementRequestDto.getMonth());
//        return ResponseEntity.ok(response);
    }

    @GetMapping("yearly/statement/{year}")
    public DefaultApiResponse getYearlyStatement(@RequestHeader("loggedInWalletId") String walletId,
                                                       @PathVariable int year){
        return transactionService.yearlyStatement(walletId, year);
    }

    @GetMapping("/statement")
    public DefaultApiResponse getStatement(@RequestHeader("loggedInWalletId") String walletId,
                                                 @RequestBody StatementRequestDto statementRequestDto){
        return transactionService.statementForDates(walletId, statementRequestDto.getStartDate(), statementRequestDto.getStopDate());
    }
}
