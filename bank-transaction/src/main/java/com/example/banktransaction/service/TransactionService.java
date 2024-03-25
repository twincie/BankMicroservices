package com.example.banktransaction.service;

import com.example.banktransaction.entity.Response;
import com.example.banktransaction.entity.Transaction;
import com.example.banktransaction.external.Wallet;
import com.example.banktransaction.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @LoadBalanced
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
    public Response readOne(Long transactionId, String walletId){
        Response response = new Response();
        Long walletIdToLong = Long.parseLong(walletId);
        Transaction transaction = transactionRepository.findByIdAndWalletId(transactionId, walletIdToLong);
        if (transaction != null) {
            response.setStatus(HttpStatus.OK);
            response.setMessage("Successfully retrieved Transactions");
            response.setBody(transaction);
            return response;
        }
        response.setStatus(HttpStatus.FORBIDDEN);
        response.setMessage("ID cannot be found : Check if ID is correct");
        return response;
    }

    public  Response readOneUserTransactions(String walletId){
        try {
            Response response = new Response();
            Long walletIdToLong = Long.parseLong(walletId);
            List<Transaction> transactions = transactionRepository.findByWalletId(walletIdToLong);
            response.setStatus(HttpStatus.OK);
            response.setMessage("Successfully retrieved Transactions");
            response.setBody(transactions);
            return response;
//            return transactions;
        } catch(Exception e) {
            Response response = new Response();
            System.out.println("Error: "+ e);
            response.setStatus(HttpStatus.OK);
            response.setMessage("Successfully retrieved Transactions");
            return response;
        }
    }

    public Response readAll(String role){
        Response response = new Response();
        if(role.equalsIgnoreCase("admin")){
            List<Transaction> transactionRes = transactionRepository.findAll();
            response.setStatus(HttpStatus.OK);
            response.setMessage("Successfully retrieved all Transactions");
            response.setBody(transactionRes);
            return response;
        }
        response.setStatus(HttpStatus.FORBIDDEN);
        response.setMessage("cannot access Resource");
        return response;

    }
    public Response update(String walletId, Long transactionId, Transaction updater){
        Long walletIdToLong = Long.parseLong(walletId);
        Response response = new Response();
        if(transactionRepository.existsByIdAndWalletId(transactionId, walletIdToLong)){
            updater.setId(transactionId);
            Transaction transaction = transactionRepository.save(updater);
            response.setStatus(HttpStatus.OK);
            response.setMessage("Successfully Updated Transactions");
            response.setBody(transaction);
            return response;

        }
        response.setStatus(HttpStatus.OK);
        response.setMessage("Didnt update transaction successfully");
        return response;
    }
    public void delete(String walletId, Long transactionId){
        Long walletIdToLong = Long.parseLong(walletId);
        if(transactionRepository.existsByIdAndWalletId(transactionId, walletIdToLong)) {
            transactionRepository.deleteById(transactionId);
        }
    }

    public List<Transaction> walletReadOneUserTransactions(Long walletId) {
        return  transactionRepository.findByWalletId(walletId);
    }
}
