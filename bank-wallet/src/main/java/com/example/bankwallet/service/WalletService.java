package com.example.bankwallet.service;


import com.example.bankwallet.config.ExtendedConstants;
import com.example.bankwallet.dto.DefaultApiResponse;
import com.example.bankwallet.dto.WalletDto;
import com.example.bankwallet.external.Transaction;
import com.example.bankwallet.external.TransactionType;
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

        ResponseEntity<DefaultApiResponse> response = restTemplate.exchange(transactionServiceBaseUrl+"/details", HttpMethod.GET, entity, DefaultApiResponse.class);
        Object object = response.getBody().getData();
        if (object instanceof List) {
            List<Transaction> transactionList = (List<Transaction>) object;
            System.out.println(transactionList);
            WalletDto walletDto = WalletMapper.walletMapperDto(wallet, transactionList);
            return walletDto;
        }
        return null;
    }

    public DefaultApiResponse create(Wallet wallet){
        DefaultApiResponse apiResponse = new DefaultApiResponse();
        try {
            walletRepository.save(wallet);
            apiResponse.setStatus(ExtendedConstants.ResponseCode.SUCCESS.getCode());
            apiResponse.setMessage(ExtendedConstants.ResponseCode.SUCCESS.getDescription());
        } catch (Exception e) {
            System.out.println("Error: " + e);
            apiResponse.setStatus(ExtendedConstants.ResponseCode.FAILED.getCode());
            apiResponse.setMessage(ExtendedConstants.ResponseCode.FAILED.getDescription());
        }
        return  apiResponse;
    }

    public DefaultApiResponse readOne(String walletId){
        DefaultApiResponse apiResponse = new DefaultApiResponse();
        try {
            Long walletIdToLong = Long.parseLong(walletId);
            Wallet wallet = walletRepository.findById(walletIdToLong).orElseThrow(null);
            apiResponse.setStatus(ExtendedConstants.ResponseCode.SUCCESS.getCode());
            apiResponse.setMessage(ExtendedConstants.ResponseCode.SUCCESS.getDescription());
            apiResponse.setData(wallet);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            apiResponse.setStatus(ExtendedConstants.ResponseCode.FAILED.getCode());
            apiResponse.setMessage(ExtendedConstants.ResponseCode.FAILED.getDescription());
        }
        return  apiResponse;
    }

    public DefaultApiResponse readAll(String role){
        DefaultApiResponse apiResponse = new DefaultApiResponse();
        if (role.equalsIgnoreCase("admin")){
            List<Wallet> walletList = walletRepository.findAll();
            apiResponse.setStatus(ExtendedConstants.ResponseCode.SUCCESS.getCode());
            apiResponse.setMessage(ExtendedConstants.ResponseCode.SUCCESS.getDescription());
            apiResponse.setData(walletList);
            return apiResponse;
        }
        apiResponse.setStatus(ExtendedConstants.ResponseCode.FAILED.getCode());
        apiResponse.setMessage(ExtendedConstants.ResponseCode.FAILED.getDescription());
        return apiResponse;
    }

    public DefaultApiResponse update(Long walletId, Wallet updater){
        DefaultApiResponse apiResponse = new DefaultApiResponse();
        try {
            updater.setId(walletId);
            Wallet wallet = walletRepository.save(updater);
            WalletDto walletDto = convertToDto(wallet);
            apiResponse.setStatus(ExtendedConstants.ResponseCode.SUCCESS.getCode());
            apiResponse.setMessage(ExtendedConstants.ResponseCode.SUCCESS.getDescription());
            apiResponse.setData(walletDto);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            apiResponse.setStatus(ExtendedConstants.ResponseCode.FAILED.getCode());
            apiResponse.setMessage(ExtendedConstants.ResponseCode.FAILED.getDescription());
        }
        return  apiResponse;
    }

    public DefaultApiResponse delete(Long walletId){
        DefaultApiResponse apiResponse = new DefaultApiResponse();
        try {
            walletRepository.deleteById(walletId);
            apiResponse.setStatus(ExtendedConstants.ResponseCode.SUCCESS.getCode());
            apiResponse.setMessage(ExtendedConstants.ResponseCode.SUCCESS.getDescription());
        } catch (Exception e) {
            System.out.println("Error: " + e);
            apiResponse.setStatus(ExtendedConstants.ResponseCode.FAILED.getCode());
            apiResponse.setMessage(ExtendedConstants.ResponseCode.FAILED.getDescription());
        }
        return  apiResponse;
    }

    public DefaultApiResponse topup(BigDecimal amount, String walletId){
        DefaultApiResponse apiResponse = new DefaultApiResponse();
        try {
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
                    Wallet wallet1 = walletRepository.save(wallet);
                    walletRepository.save(globalWallet);

                    apiResponse.setStatus(ExtendedConstants.ResponseCode.SUCCESS.getCode());
                    apiResponse.setMessage(ExtendedConstants.ResponseCode.SUCCESS.getDescription());
                    WalletDto walletDto = convertToDto(wallet1);
                    apiResponse.setData(walletDto);
                    return apiResponse;
                }
                apiResponse.setStatus(ExtendedConstants.ResponseCode.NO_PERMISSION.getCode());
                apiResponse.setMessage(ExtendedConstants.ResponseCode.NO_PERMISSION.getDescription());
                return apiResponse;
            }
            apiResponse.setStatus(ExtendedConstants.ResponseCode.NOT_FOUND.getCode());
            apiResponse.setMessage("Wallet doesnt exist!!");
            return apiResponse;
        } catch (Exception e) {
            System.out.println("Error: " + e);
            apiResponse.setStatus(ExtendedConstants.ResponseCode.FAILED.getCode());
            apiResponse.setMessage(ExtendedConstants.ResponseCode.FAILED.getCode());
            return apiResponse;
        }
    }

    public DefaultApiResponse withdraw(BigDecimal amount, String walletId){
        DefaultApiResponse apiResponse = new DefaultApiResponse();
        try {
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
                    Wallet wallet1 = walletRepository.save(wallet);
                    WalletDto wallerDto = convertToDto(wallet1);
                    walletRepository.save(globalWallet);

    //                System.out.println(transactionServiceBaseUrl);
                    apiResponse.setStatus(ExtendedConstants.ResponseCode.NOT_FOUND.getCode());
                    apiResponse.setMessage(ExtendedConstants.ResponseCode.NOT_FOUND.getCode());
                    apiResponse.setData(wallerDto);
                    return apiResponse;
                }
                apiResponse.setStatus(ExtendedConstants.ResponseCode.FAILED.getCode());
                apiResponse.setMessage("amount too small");
                return apiResponse;
            }
            apiResponse.setStatus(ExtendedConstants.ResponseCode.FAILED.getCode());
            apiResponse.setMessage("Wallet doesnt exist!!");
            return apiResponse;
        } catch (Exception e) {
            System.out.println("Error: " + e);
            apiResponse.setStatus(ExtendedConstants.ResponseCode.FAILED.getCode());
            apiResponse.setMessage(ExtendedConstants.ResponseCode.FAILED.getCode());
            return apiResponse;
        }
    }

    public DefaultApiResponse transfer(BigDecimal amount, String accountNumber, String walletId){
        DefaultApiResponse apiResponse = new DefaultApiResponse();
        try {
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
                        apiResponse.setStatus(ExtendedConstants.ResponseCode.FAILED.getCode());
                        apiResponse.setMessage("Amount too small or bigger than wallet balance");
                        return apiResponse;
                    }
                    if (amount.compareTo(charges(amount)) > 0) {
                        BigDecimal receiverBalance = receiveWallet.getAmount().add(amount.subtract(charges(amount)));
                        receiveWallet.setAmount(receiverBalance);
                        balance1 = balance1.add(charges(amount));
                        //walletRepository.save(receiveWallet);
                    } else{
                        apiResponse.setStatus(ExtendedConstants.ResponseCode.FAILED.getCode());
                        apiResponse.setMessage("amount too small to be deposited");
                        return apiResponse;
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
                    apiResponse.setStatus(ExtendedConstants.ResponseCode.SUCCESS.getCode());
                    apiResponse.setMessage(ExtendedConstants.ResponseCode.SUCCESS.getDescription());
                    apiResponse.setData(sendWallet);
                    return apiResponse;
                }
                apiResponse.setStatus(ExtendedConstants.ResponseCode.FAILED.getCode());
                apiResponse.setMessage("same wallet");
                return apiResponse;
            }
            apiResponse.setStatus(ExtendedConstants.ResponseCode.FAILED.getCode());
            apiResponse.setMessage("wallet does not exist");
            return apiResponse;
    } catch (Exception e) {
        System.out.println("Error: " + e);
        apiResponse.setStatus(ExtendedConstants.ResponseCode.FAILED.getCode());
        apiResponse.setMessage(ExtendedConstants.ResponseCode.FAILED.getDescription());
        return apiResponse;
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