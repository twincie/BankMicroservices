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

//    public boolean doesWalletExist(Long walletId) {
//        Optional<Wallet> wallet = Optional.ofNullable(restTemplate.getForObject(walletServiceBaseUrl + "/" + walletId, Wallet.class));
//        if (wallet.isPresent()){
//            return true;
//        }
//        return false;
//    }
    public Transaction readOne(Long transactionId, String walletId){
        Long walletIdToLong = Long.parseLong(walletId);
        return transactionRepository.findByIdAndWalletId(transactionId, walletIdToLong);
//        if(transactionRepository.existsByIdAndWalletId(transactionId)){
//            return transactionRepository.findById(transactionId).orElseThrow(null);
//        }
//        return null;
    }

    public  List<Transaction> readOneUserTransactions(String walletId){
        Long walletIdToLong = Long.parseLong(walletId);
        List<Transaction> transactions = transactionRepository.findByWalletId(walletIdToLong);
        return transactions;

    }

    public List<Transaction> readAll(){
//        Long walletIdToLong = Long.parseLong(walletId);
//        Transaction transaction = transactionRepository.findByAccountNumber("1000000000");
//        if(transaction.getWalletId().equals(walletIdToLong)){
            return transactionRepository.findAll();
//        }

    }
    public Transaction update(Long walletId, Long transactionId, Transaction updater, String walletid){
        Long walletIdToLong = Long.parseLong(walletid);
        if(walletId.equals(walletIdToLong) && transactionRepository.existsById(transactionId)){
            updater.setId(transactionId);
            return transactionRepository.save(updater);
        }
        return null;
    }
    public void delete(Long walletId, Long transactionId, String walletid){
        Long walletIdToLong = Long.parseLong(walletid);
        if(walletId.equals(walletIdToLong) && transactionRepository.existsById(transactionId)) {
            transactionRepository.deleteById(transactionId);
        }
    }

    public List<Transaction> walletReadOneUserTransactions(Long walletId) {
        return  transactionRepository.findByWalletId(walletId);
    }
}
