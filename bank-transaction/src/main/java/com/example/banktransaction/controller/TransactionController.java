package com.example.banktransaction.controller;

import com.example.banktransaction.entity.Transaction;
import com.example.banktransaction.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    static class Response{
        private final HttpStatus status;
        private  final String message;
        private final Object body;

        Response(HttpStatus status, String message, Object body) {
            this.status = status;
            this.message = message;
            this.body = body;
        }
        public HttpStatus getStatus() {
            return status;
        }

        public Object getBody() {
            return body;
        }

        public String getMessage() {
            return message;
        }
    }

    @PostMapping
    public Transaction createTransaction(@RequestBody Transaction transaction){
        return transactionService.create(transaction);
    }
    @GetMapping("/all")
    public ResponseEntity<Response> readAllTransactions(@RequestHeader("role") String role){
        if(role.equalsIgnoreCase("admin")){
            List<Transaction> transactionRes = transactionService.readAll();
            return ResponseEntity.ok(new Response(HttpStatus.OK, "Success" , transactionRes));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).
                body(new Response(HttpStatus.FORBIDDEN, "cannot access Resource", null));
    }
    @GetMapping("/transactionId")
    public ResponseEntity<Response> readOneTransaction(@PathVariable Long transactionId, @RequestHeader("loggedInWalletId") String walletId){
        Transaction transaction = transactionService.readOne(transactionId, walletId);
        if (transaction != null) {
            return ResponseEntity.ok(new Response(HttpStatus.OK, "Success" , transaction));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).
                body(new Response(HttpStatus.FORBIDDEN, "wrong transaction Id", null));
    }
    @GetMapping
    public List<Transaction> readAllUserTransactions(@RequestHeader("loggedInWalletId") String walletId){
        return transactionService.readOneUserTransactions(walletId);
    }
//    @GetMapping("/all/wallet")
//    public List<Transaction> walletReadAllUserTransactions(@RequestParam Long walletId){
//        return transactionService.walletReadOneUserTransactions(walletId);
//    }
    @PutMapping("/{transactionId}")
    public Transaction updateTransaction(@RequestParam Long walletId, @PathVariable Long transactionId, @RequestBody Transaction transaction, @RequestHeader("loggedInWalletId") String walletid){
        return transactionService.update(walletId, transactionId, transaction, walletid);
    }
    @DeleteMapping("/{transactionId}")
    public void deleteTransaction(@RequestParam Long walletId, @PathVariable Long transactionId, @RequestHeader("loggedInWalletId") String walletid){
        transactionService.delete(walletId, transactionId, walletid);
    }
}
