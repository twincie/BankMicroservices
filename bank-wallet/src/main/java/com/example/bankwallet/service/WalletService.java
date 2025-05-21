package com.example.bankwallet.service;


import com.example.bankwallet.dto.WalletDto;
import com.example.bankwallet.entity.Response;
import com.example.bankwallet.external.Role;
import com.example.bankwallet.external.Transaction;
import com.example.bankwallet.external.TransactionType;
import com.example.bankwallet.external.Users;
import com.example.bankwallet.mapper.WalletMapper;
import com.example.bankwallet.repository.WalletRepository;
import com.example.bankwallet.entity.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;
    @LoadBalanced
    private final RestTemplate restTemplate;
//    String transactionBaseUrl = "${transaction-service.url}";
//    private final String transactionServiceBaseUrl = transactionBaseUrl + "/api/v1/transaction";
    private final String transactionServiceBaseUrl = "http://BANK-TRANSACTION/api/v1/transaction";

//    String userBaseUrl = "${user-service.url}";
//    private final String usersServiceBaseUrl = userBaseUrl + "/api/v1/users";
    private final String usersServiceBaseUrl = "http://BANK-USERS/api/v1/users";

    public WalletService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }



    private WalletDto convertToDto(Wallet wallet){
        Long walletId = wallet.getId();
        String walletIdtoString = walletId.toString();
        HttpHeaders header = new HttpHeaders();
        header.set("loggedInWalletId", walletIdtoString);
        HttpEntity<String> entity = new HttpEntity<>(header);

        ResponseEntity<Response> response = restTemplate.exchange(transactionServiceBaseUrl+"/details", HttpMethod.GET, entity, new ParameterizedTypeReference<Response>(){});
        Object object = response.getBody().getBody();
        if (object instanceof List) {
            List<Transaction> transactionList = (List<Transaction>) object;
            System.out.println(transactionList);
            WalletDto walletDto = WalletMapper.walletMapperDto(wallet, transactionList);
            return walletDto;
        }
        return null;
    }

    public Wallet create(Wallet wallet){
        return walletRepository.save(wallet);
    }

    public Response readOne(String walletId){
        try {
            Long walletIdToLong = Long.parseLong(walletId);
            Wallet wallet = walletRepository.findById(walletIdToLong).orElseThrow(null);
            return new Response(HttpStatus.OK, "wallet details retrieval successful", wallet);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return new Response(HttpStatus.OK, "Server down!!, wait a while.", null);
        }
    }

    public Response readAll(String role){
        if (role.equalsIgnoreCase("admin")){
            List<Wallet> walletList = walletRepository.findAll();
            return new Response(HttpStatus.OK, "list of all wallets", walletList);
        }
        return new Response(HttpStatus.FORBIDDEN, "user cant access this resource. ", null);
    }

    public Wallet update(Long walletId, Wallet updater){
        updater.setId(walletId);
        return walletRepository.save(updater);
    }

    public void delete(Long walletId){
        walletRepository.deleteById(walletId);
    }

    public Response topup(BigDecimal amount, String walletId){
        try {
            Response response = new Response();
            Long walletIdToLong = Long.parseLong(walletId);
            Optional<Wallet> globalWalletInfo = walletRepository.findByAccountNumber("1000000000");
            Optional<Wallet> walletoptional = walletRepository.findById(walletIdToLong);
            if (walletoptional.isPresent() && globalWalletInfo.isPresent()){
                Wallet wallet = walletoptional.get();
                if (amount.compareTo(charges(amount)) >= 0){
                    BigDecimal balance = wallet.getAmount().add(amount.subtract(charges(amount)));
                    wallet.setAmount(balance);
                    //walletRepository.save(wallet);

                    Wallet globalWallet = globalWalletInfo.get();
                    BigDecimal globalBalance = globalWallet.getAmount().add(charges(amount));
                    globalWallet.setAmount(globalBalance);
                    //walletRepository.save(globalWallet);

                    Transaction transaction = new Transaction();
                    transaction.setAmount(amount);
                    transaction.setType(TransactionType.TOPUP);
                    transaction.setWalletId(walletIdToLong);


                    restTemplate.postForObject(transactionServiceBaseUrl+"?walletId=" + wallet.getId(), transaction ,Transaction.class);
                    walletRepository.save(wallet);
                    walletRepository.save(globalWallet);

                    response.setStatus(HttpStatus.OK);
                    response.setMessage("Topup Successful");
                    //WalletDto walletDto = convertToDto(wallet, )
                    response.setBody(wallet);
                    return response;
                }
                response.setStatus(HttpStatus.FORBIDDEN);
                response.setMessage("amount too small");
                return response;
            }
            response.setStatus(HttpStatus.FORBIDDEN);
            response.setMessage("Wallet doesnt exist!!");
            return response;
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return new Response(HttpStatus.OK, "Server down!!, wait a while.", null);
        }
    }

    public Response withdraw(BigDecimal amount, String walletId){
        try {
            Response response = new Response();
            Long walletIdToLong = Long.parseLong(walletId);
            Optional<Wallet> globalWalletInfo = walletRepository.findByAccountNumber("1000000000");
            Optional<Wallet> walletoptional = walletRepository.findById(walletIdToLong);
            if (walletoptional.isPresent() && globalWalletInfo.isPresent()){
                Wallet wallet = walletoptional.get();
                if(amount.compareTo(BigDecimal.ZERO) >0 && wallet.getAmount().compareTo(amount.add(charges(amount))) >= 0 ) {
                    BigDecimal balance = wallet.getAmount().subtract(amount.add(charges(amount)));
                    wallet.setAmount(balance);
                    //walletRepository.save(wallet);

                    Wallet globalWallet = globalWalletInfo.get();
                    BigDecimal globalBalance = globalWallet.getAmount().add(charges(amount));
                    globalWallet.setAmount(globalBalance);
                    //walletRepository.save(globalWallet);

                    Transaction transaction = new Transaction();
                    transaction.setAmount(amount);
                    transaction.setType(TransactionType.WITHDRAW);
                    transaction.setWalletId(walletIdToLong);
                    System.out.println(walletId);


                    restTemplate.postForObject(transactionServiceBaseUrl + "?walletId=" + wallet.getId(), transaction, Transaction.class);
                    walletRepository.save(wallet);
                    walletRepository.save(globalWallet);

    //                System.out.println(transactionServiceBaseUrl);
                    response.setStatus(HttpStatus.OK);
                    response.setMessage("Withdrawal Successful");
                    response.setBody(wallet);
                    return response;
                }
                response.setStatus(HttpStatus.FORBIDDEN);
                response.setMessage("amount too small");
                return response;
            }
            response.setStatus(HttpStatus.FORBIDDEN);
            response.setMessage("Wallet doesnt exist!!");
            return response;
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return new Response(HttpStatus.OK, "Server down!!, wait a while.", null);
        }
    }

    public Response transfer(BigDecimal amount, String accountNumber, String walletId){
        try {
            Response response = new Response();
            Long walletIdToLong = Long.parseLong(walletId);
            Optional<Wallet> globalWallet = walletRepository.findByAccountNumber("1000000000");
            Optional<Wallet> senderWallet = walletRepository.findById(walletIdToLong);
            Optional<Wallet> receiverWallet = walletRepository.findByAccountNumber(accountNumber);
            if (globalWallet.isPresent() && senderWallet.isPresent() && receiverWallet.isPresent()) {
                Wallet sendWallet = senderWallet.get();
                Wallet receiveWallet = receiverWallet.get();
                Wallet wallet1 = globalWallet.get();
                BigDecimal balance1 = wallet1.getAmount();
                String senderAccount = senderWallet.get().getAccountNumber();
                String receiverAccount = receiverWallet.get().getAccountNumber();
    //            System.out.println("sender: " + senderAccount);
    //            System.out.println("receiver: " + receiverAccount);
                if (!senderAccount.equals(receiverAccount)) {
                    if (amount.compareTo(charges(amount)) > 0 && sendWallet.getAmount().compareTo(amount.add(charges(amount))) > 0) {
                        BigDecimal senderBalance = sendWallet.getAmount().subtract(amount.add(charges((amount))));
                        sendWallet.setAmount(senderBalance);
                        balance1 = balance1.add(charges(amount));
                        //walletRepository.save(sendWallet);
                    } else{
                        response.setStatus(HttpStatus.OK);
                        response.setMessage("Amount too small or bigger than wallet balance");
                        return response;
                    }
                    if (amount.compareTo(charges(amount)) > 0) {
                        BigDecimal receiverBalance = receiveWallet.getAmount().add(amount.subtract(charges(amount)));
                        receiveWallet.setAmount(receiverBalance);
                        balance1 = balance1.add(charges(amount));
                        //walletRepository.save(receiveWallet);
                    } else{
                        response.setStatus(HttpStatus.OK);
                        response.setMessage("amount too small to be deposited");
                        return response;
                    }
                    //globalbank charges
                    wallet1.setAmount(balance1);
                    walletRepository.save(wallet1);
                    Transaction sendTransaction = new Transaction();
                    sendTransaction.setAmount(amount);
                    sendTransaction.setType(TransactionType.TRANSFER);
                    sendTransaction.setWalletId(walletIdToLong);

                    restTemplate.postForObject(transactionServiceBaseUrl + "?walletId=" + sendWallet.getId(), sendTransaction, Transaction.class);
                    walletRepository.save(sendWallet);
                    walletRepository.save(receiveWallet);

                    Transaction receiveTransaction = new Transaction();
                    receiveTransaction.setAmount(amount);
                    receiveTransaction.setType(TransactionType.TOPUP);
                    receiveTransaction.setWalletId(receiveWallet.getId());
                    System.out.println(receiveWallet.getId());

                    restTemplate.postForObject(transactionServiceBaseUrl + "?walletId=" + receiveWallet.getId(), receiveTransaction, Transaction.class);


                    //return "Transaction Successful";
                    response.setStatus(HttpStatus.OK);
                    response.setMessage("Transaction Successful");
                    response.setBody(sendWallet);
                    return response;
                }
                response.setStatus(HttpStatus.OK);
                response.setMessage("same wallet");
                return response;
            }
            response.setStatus(HttpStatus.OK);
            response.setMessage("wallet does not exist");
            return response;
    } catch (Exception e) {
        System.out.println("Error: " + e);
        return new Response(HttpStatus.OK, "Server down!!, wait a while.", null);
    }
    }

    public BigDecimal charges(BigDecimal amount){
        if (amount.compareTo(BigDecimal.valueOf(5000)) <= 0){
            return BigDecimal.valueOf(10);
        }else if (amount.compareTo(BigDecimal.valueOf(50000)) <= 0){
            return BigDecimal.valueOf(25);
        } else{
            return BigDecimal.valueOf(50);
        }
    }
}