package com.example.bankwallet.service;


import com.example.bankwallet.dto.WalletDto;
import com.example.bankwallet.external.Role;
import com.example.bankwallet.external.Transaction;
import com.example.bankwallet.external.TransactionType;
import com.example.bankwallet.external.Users;
import com.example.bankwallet.mapper.WalletMapper;
import com.example.bankwallet.repository.WalletRepository;
import com.example.bankwallet.entity.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;
    private final RestTemplate restTemplate;
    private final String transactionServiceBaseUrl = "http://BANK-TRANSACTION:8083/api/v1/transaction";

    private final String usersServiceBaseUrl = "http://BANK-USERS:8081/api/v1/users";

    public WalletService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }



    private WalletDto convertToDto(Wallet wallet){
//        ResponseEntity<List<Transaction>> transaction = restTemplate.exchange(transactionServiceBaseUrl+"/details", HttpMethod.GET, null, new ParameterizedTypeReference<List<Transaction>>(){});
//        List<Transaction> transactions= transaction.getBody();
//        WalletDto walletDto = WalletMapper.walletMapperDto(wallet, transactions);
        WalletDto walletDto = WalletMapper.walletMapperDto(wallet);
        return walletDto;
    }

    public Wallet create(Wallet wallet){
        return walletRepository.save(wallet);
    }

    public WalletDto readOne(String walletId){
        Long walletIdToLong = Long.parseLong(walletId);
        Wallet wallet = walletRepository.findById(walletIdToLong).orElseThrow(null);
        return convertToDto(wallet);
    }

    public List<Wallet> readAll(){
        return walletRepository.findAll();
    }

    public Wallet update(Long walletId, Wallet updater){
        updater.setId(walletId);
        return walletRepository.save(updater);
    }

    public void delete(Long walletId){
        walletRepository.deleteById(walletId);
    }

    public String topup(BigDecimal amount, String walletId){
        Long walletIdToLong = Long.parseLong(walletId);
        Optional<Wallet> globalWalletInfo = walletRepository.findByAccountNumber("1000000000");
        Optional<Wallet> walletoptional = walletRepository.findById(walletIdToLong);
        if (walletoptional.isPresent() && globalWalletInfo.isPresent()){
            Wallet wallet = walletoptional.get();
            if (amount.compareTo(charges(amount)) >= 0){
                BigDecimal balance = wallet.getAmount().add(amount.subtract(charges(amount)));
                wallet.setAmount(balance);
                walletRepository.save(wallet);
                Wallet globalWallet = globalWalletInfo.get();
                BigDecimal globalBalance = globalWallet.getAmount().add(charges(amount));
                globalWallet.setAmount(globalBalance);
                walletRepository.save(globalWallet);
                Transaction transaction = new Transaction();
                transaction.setAmount(amount);
                transaction.setType(TransactionType.TOPUP);
                transaction.setWalletId(walletIdToLong);
                restTemplate.postForObject(transactionServiceBaseUrl+"?walletId=" + wallet.getId(), transaction ,Transaction.class);
                return "Topup Successful";
            }
            return "amount too small";
        }
        return "wallet doesn't exist";
    }

    public String withdraw(BigDecimal amount, String walletId){
        Long walletIdToLong = Long.parseLong(walletId);
        Optional<Wallet> globalWalletInfo = walletRepository.findByAccountNumber("1000000000");
        Optional<Wallet> walletoptional = walletRepository.findById(walletIdToLong);
        if (walletoptional.isPresent() && globalWalletInfo.isPresent()){
            Wallet wallet = walletoptional.get();
            if(amount.compareTo(BigDecimal.ZERO) >0 && wallet.getAmount().compareTo(amount.add(charges(amount))) >= 0 ) {
                BigDecimal balance = wallet.getAmount().subtract(amount.add(charges(amount)));
                wallet.setAmount(balance);
                walletRepository.save(wallet);
                Wallet globalWallet = globalWalletInfo.get();
                BigDecimal globalBalance = globalWallet.getAmount().add(charges(amount));
                globalWallet.setAmount(globalBalance);
                walletRepository.save(globalWallet);
                Transaction transaction = new Transaction();
                transaction.setAmount(amount);
                transaction.setType(TransactionType.WITHDRAW);
                transaction.setWalletId(walletIdToLong);
                System.out.println(walletId);
                restTemplate.postForObject(transactionServiceBaseUrl + "?walletId=" + wallet.getId(), transaction, Transaction.class);
                System.out.println(transactionServiceBaseUrl);
                return "Withdrawal Successful";
            }
            return "amount too small";
        }
        return "wallet doesn't exist";
    }

    public String transfer(BigDecimal amount, String accountNumber, String walletId){
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
            System.out.println("sender: " + senderAccount);
            System.out.println("receiver: " + receiverAccount);
            if (!senderAccount.equals(receiverAccount)) {
                if (amount.compareTo(charges(amount)) > 0 && sendWallet.getAmount().compareTo(amount.add(charges(amount))) > 0) {
                    BigDecimal senderBalance = sendWallet.getAmount().subtract(amount.add(charges((amount))));
                    sendWallet.setAmount(senderBalance);
                    balance1 = balance1.add(charges(amount));
                    walletRepository.save(sendWallet);
                } else{
                    return "Amount too small or bigger than wallet balance";
                }
                if (amount.compareTo(charges(amount)) > 0) {
                    BigDecimal receiverBalance = receiveWallet.getAmount().add(amount.subtract(charges(amount)));
                    receiveWallet.setAmount(receiverBalance);
                    balance1 = balance1.add(charges(amount));
                    walletRepository.save(receiveWallet);
                } else{
                    return "amount too small to be deposited";
                }
                //globalbank charges
                wallet1.setAmount(balance1);
                walletRepository.save(wallet1);
                Transaction sendTransaction = new Transaction();
                sendTransaction.setAmount(amount);
                sendTransaction.setType(TransactionType.TRANSFER);
                sendTransaction.setWalletId(walletIdToLong);
                restTemplate.postForObject(transactionServiceBaseUrl + "?walletId=" + sendWallet.getId(), sendTransaction, Transaction.class);
                Transaction receiveTransaction = new Transaction();
                receiveTransaction.setAmount(amount);
                receiveTransaction.setType(TransactionType.TOPUP);
                receiveTransaction.setWalletId(receiveWallet.getId());
                System.out.println(receiveWallet.getId());
                restTemplate.postForObject(transactionServiceBaseUrl + "?walletId=" + receiveWallet.getId(), receiveTransaction, Transaction.class);
                return "Transaction Successful";
            }
            return "same wallet";
        }
        return "wallet does not exist";
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