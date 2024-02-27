package com.example.bankwallet.controller;

import com.example.bankwallet.service.WalletService;
import com.example.bankwallet.entity.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {
    @Autowired
    private WalletService walletService;

    //======================================================================
    //    CRUD START

    @PostMapping
    public Wallet createWallet(@RequestBody Wallet wallet){
        return walletService.create(wallet);
    }
    @GetMapping("/{walletId}")
    public ResponseEntity<Wallet> readOneWallet(@PathVariable Long walletId){
        return walletService.readOne(walletId);
        //return walletService.readOne(walletId);
    }
    @GetMapping
    public List<Wallet> readAllWallets(){
        return walletService.readAll();
    }
    @PutMapping("/{walletId}")
    public Wallet updateWallet(@PathVariable Long walletId, @RequestBody Wallet wallet){
        return walletService.update(walletId, wallet);
    }
    @DeleteMapping("/{walletId}")
    public void deleteWallet(@PathVariable Long walletId){
        walletService.delete(walletId);
    }
    //    CRUD END
    //======================================================================






    @PutMapping("/{walletId}/topup")
    public ResponseEntity<String> topUpWallet(@PathVariable Long walletId, @RequestBody Map<String, BigDecimal> request){
        BigDecimal amount = request.get("amount");
        walletService.topup(walletId, amount);
        return new ResponseEntity<>("Topup Successful", HttpStatus.OK);
    }

    @PutMapping("/{walletId}/withdraw")
    public ResponseEntity<String> withdrawWallet(@PathVariable Long walletId, @RequestBody Map<String, BigDecimal> request){
        BigDecimal amount = request.get("amount");
        walletService.withdraw(walletId, amount);
        return new ResponseEntity<>("Withdraw Successful",HttpStatus.OK);
    }

    @PutMapping("/{walletId}/transfer")
    public ResponseEntity<String> transferWallet(@PathVariable Long walletId, @RequestBody Map<String, Object> request){
        Object amountObj = request.get("amount");
        Object receiverAccountNumberObj = request.get("accountNumber");
        BigDecimal amount = new BigDecimal(amountObj.toString());
        String receiverAccountNumber = receiverAccountNumberObj.toString();

//        System.out.println(amountObj);
//        System.out.println(receiverAccountNumberObj);
        boolean isTransfer = walletService.transfer(walletId, amount, receiverAccountNumber);
        if (isTransfer){
            return new ResponseEntity<>("Transfer Successful", HttpStatus.OK);
        }
        return new ResponseEntity<>("Transfer not successful", HttpStatus.FORBIDDEN);
    }

}
