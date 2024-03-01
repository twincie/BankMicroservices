package com.example.banktransaction.service;

import com.example.banktransaction.entity.Transaction;
import com.example.banktransaction.external.Wallet;
import com.example.banktransaction.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final RestTemplate restTemplate;
    private final String walletServiceBaseUrl = "http://BANK-WALLET:8082/api/v1/wallet";

    @Autowired
    private TransactionRepository transactionRepository;

    public TransactionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    // CRUD methods
    public Transaction create(Transaction transaction){
        return transactionRepository.save(transaction);
    }

    public boolean doesWalletExistForUser(Long walletId) {
        boolean walletExists = false;
        if (walletExists){
            restTemplate.getForObject(walletServiceBaseUrl + "/{walletId}", Wallet.class, walletId);
            return true;
        } else {
            return false;
        }
    }
    public ResponseEntity<Optional<Transaction>> readOne(Long walletId, Long transactionId){
//        String walletInfo = restTemplate.getForObject(walletServiceBaseUrl + "/wallets/{walletId}", String.class, walletId)
        if(doesWalletExistForUser(walletId) && transactionRepository.existsById(transactionId)){
            return new ResponseEntity<>(transactionRepository.findById(transactionId), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    public  List<Transaction> readOneUserTransactions(Long walletId){
        return transactionRepository.findByWalletId(walletId);
    }
    public List<Transaction> readAll(){
        return transactionRepository.findAll();
    }
    public Transaction update(Long walletId, Long transactionId, Transaction updater){
        if(doesWalletExistForUser(walletId) && transactionRepository.existsById(transactionId)){
            updater.setId(transactionId);
            return transactionRepository.save(updater);
        }
        return null;
    }
    public void delete(Long walletId, Long transactionId){
        if(doesWalletExistForUser(walletId) && transactionRepository.existsById(transactionId)) {
            transactionRepository.deleteById(transactionId);
        }
    }
}
