package com.example.bankwallet.service;


import com.example.bankwallet.repository.WalletRepository;
import com.example.bankwallet.entity.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class WalletService {

//    private final RestTemplate restTemplate;
//    private final String transactionServiceBaseUrl = "http://localhost:8080/api/v1/wallet/902/transaction";

    @Autowired
    private WalletRepository walletRepository;

//    @Autowired
//    private TransactionRepository transactionRepository;

//    public WalletService(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }

    //=================================== CRUD START =================================
    public Wallet create(Wallet wallet){
        return walletRepository.save(wallet);
    }
//    public ResponseEntity<Wallet> readOne(Long waalletId){
//        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
//        Wallet wallet = walletRepository.findById(waalletId).orElseThrow(null);
//        String username = wallet.getUser().getUsername();
//        if (currentUser.equals(username)) {
//            return new ResponseEntity<>(wallet, HttpStatus.OK);
//        }
//        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
//    }
    public ResponseEntity<Wallet> readOne(Long walletId){
        return new ResponseEntity<>(walletRepository.findById(walletId).orElseThrow(null), HttpStatus.OK);
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

    //=================================== CRUD END =================================

    public void topup(Long walletId, BigDecimal amount){
        Optional<Wallet> globalWallet = walletRepository.findByAccountNumber("1000000000");
        Optional<Wallet> walletoptional = walletRepository.findById(walletId);
        if (walletoptional.isPresent() && globalWallet.isPresent()){
            Wallet wallet = walletoptional.get();
            if (amount.compareTo(charges(amount)) > 0){
                BigDecimal balance = wallet.getAmount().add(amount.subtract(charges(amount)));
                wallet.setAmount(balance);
                walletRepository.save(wallet);

                Wallet wallet1 = globalWallet.get();
                BigDecimal balance1 = wallet1.getAmount().add(charges(amount));
                wallet1.setAmount(balance1);
                walletRepository.save(wallet1);

//                Transaction transaction = new Transaction();
//                transaction.setAmount(amount);
//                transaction.setType(TransactionType.TOPUP);
//                transaction.setWallet(wallet);
//                transactionRepository.save(transaction);
            }
        }
    }

    public void withdraw(Long walletId, BigDecimal amount){
        Optional<Wallet> globalWallet = walletRepository.findByAccountNumber("1000000000");
        Optional<Wallet> walletoptional = walletRepository.findById(walletId);
        if (walletoptional.isPresent() && globalWallet.isPresent()){
            Wallet wallet = walletoptional.get();
            // BigDecimal balance = wallet.getAmount();
            if(amount.compareTo(BigDecimal.ZERO) >0 && wallet.getAmount().compareTo(amount.add(charges(amount))) >= 0 ){
                BigDecimal balance = wallet.getAmount().subtract(amount.add(charges(amount)));
                wallet.setAmount(balance);
                walletRepository.save(wallet);

                Wallet wallet1 = globalWallet.get();
                BigDecimal balance1 = wallet1.getAmount().add(charges(amount));
                wallet1.setAmount(balance1);
                walletRepository.save(wallet1);

//                Transaction transaction = new Transaction();
//                transaction.setAmount(amount);
//                transaction.setType(TransactionType.WITHDRAW);
//                transaction.setWallet(wallet);
//                transactionRepository.save(transaction);
            }

        }
    }

    public boolean transfer(Long walletId, BigDecimal amount, String accountNumber){
        Optional<Wallet> globalWallet = walletRepository.findByAccountNumber("1000000000");
        Optional<Wallet> senderWallet = walletRepository.findById(walletId);
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
                if (amount.compareTo(charges(amount)) > 0 && sendWallet.getAmount().compareTo(amount.add(charges(amount))) >= 0) {
                    BigDecimal senderBalance = sendWallet.getAmount().subtract(amount.add(charges((amount))));
                    sendWallet.setAmount(senderBalance);
                    balance1 = balance1.add(charges(amount));
                    walletRepository.save(sendWallet);
                }
                if (amount.compareTo(charges(amount)) > 0) {
                    BigDecimal receiverBalance = receiveWallet.getAmount().add(amount.subtract(charges(amount)));
                    receiveWallet.setAmount(receiverBalance);
                    balance1 = balance1.add(charges(amount));
                    walletRepository.save(receiveWallet);
                }

                //globalbank charges
                wallet1.setAmount(balance1);
                walletRepository.save(wallet1);

//                Transaction sendTransaction = new Transaction();
//                sendTransaction.setAmount(amount);
//                sendTransaction.setType(TransactionType.TRANSFER);
//                sendTransaction.setWallet(sendWallet);
//                transactionRepository.save(sendTransaction);
//
//                Transaction receiveTransaction = new Transaction();
//                receiveTransaction.setAmount(amount);
//                receiveTransaction.setType(TransactionType.TOPUP);
//                receiveTransaction.setWallet(sendWallet);
//                transactionRepository.save(receiveTransaction);
                return true;
            }
            return false;
        }

        return false;
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