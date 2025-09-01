package com.example.bankwallet.controller;

import com.example.bankwallet.dto.DefaultApiResponse;
import com.example.bankwallet.service.WalletService;
import com.example.bankwallet.entity.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @PostMapping
    public DefaultApiResponse createWallet(@RequestBody Wallet wallet){
        return walletService.create(wallet);
    }

    @GetMapping("/details")
    public DefaultApiResponse readOneWallet(@RequestHeader("role") String role, @RequestHeader("loggedInWalletId") String walletId){
        return walletService.readOne(walletId);
    }

    @GetMapping
    public DefaultApiResponse readAllWallets(@RequestHeader("role") String role, @RequestHeader("loggedInWalletId") String walletId){
        return walletService.readAll(role);
    }

    @PutMapping("/{walletId}")
    public DefaultApiResponse updateWallet(@PathVariable Long walletId, @RequestBody Wallet wallet){
        return walletService.update(walletId, wallet);
    }

    @DeleteMapping("/{walletId}")
    public DefaultApiResponse deleteWallet(@PathVariable Long walletId){
        return walletService.delete(walletId);
    }

    @PutMapping("/topup")
    public DefaultApiResponse topUpWallet(@RequestBody Map<String, BigDecimal> request, @RequestHeader("loggedInWalletId") String walletId){
        BigDecimal amount = request.get("amount");
        return walletService.topup(amount, walletId);

    }

    @PutMapping("/withdraw")
    public DefaultApiResponse withdrawWallet(@RequestBody Map<String, BigDecimal> request, @RequestHeader("loggedInWalletId") String walletId){
        BigDecimal amount = request.get("amount");
        return walletService.withdraw(amount, walletId);
    }

    @PutMapping("/transfer")
    public DefaultApiResponse transferWallet(@RequestBody Map<String, Object> request, @RequestHeader("loggedInWalletId") String walletId){
        Object amountObj = request.get("amount");
        Object receiverAccountNumberObj = request.get("accountNumber");
        BigDecimal amount = new BigDecimal(amountObj.toString());
        String receiverAccountNumber = receiverAccountNumberObj.toString();
        return walletService.transfer(amount, receiverAccountNumber, walletId);

    }
}
