package com.example.bankwallet.controller;

import com.example.bankwallet.dto.WalletDto;
import com.example.bankwallet.entity.Response;
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

    @PostMapping
    public Wallet createWallet(@RequestBody Wallet wallet){
        return walletService.create(wallet);
    }

    @GetMapping("/details")
    public ResponseEntity<Response> readOneWallet(@RequestHeader("role") String role, @RequestHeader("loggedInWalletId") String walletId){
        Response response = walletService.readOne(walletId);
            return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Response> readAllWallets(@RequestHeader("role") String role, @RequestHeader("loggedInWalletId") String walletId){
        Response response = walletService.readAll(role);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{walletId}")
    public Wallet updateWallet(@PathVariable Long walletId, @RequestBody Wallet wallet){
        return walletService.update(walletId, wallet);
    }

    @DeleteMapping("/{walletId}")
    public void deleteWallet(@PathVariable Long walletId){
        walletService.delete(walletId);
    }

    @PutMapping("/topup")
    public ResponseEntity<Response> topUpWallet(@RequestBody Map<String, BigDecimal> request, @RequestHeader("loggedInWalletId") String walletId){
        BigDecimal amount = request.get("amount");
        Response walletResponse = walletService.topup(amount, walletId);
        return ResponseEntity.ok(walletResponse);
    }

    @PutMapping("/withdraw")
    public ResponseEntity<Response> withdrawWallet(@RequestBody Map<String, BigDecimal> request, @RequestHeader("loggedInWalletId") String walletId){
        BigDecimal amount = request.get("amount");
        Response walletResponse = walletService.withdraw(amount, walletId);
        //WalletDto wallet = walletService.readOne(walletId);
//        return new ResponseEntity<>("Withdraw Successful",HttpStatus.OK);
        //if (walletResponse.equals("Withdrawal Successful")){
            return ResponseEntity.ok(walletResponse);
        //}
        //return ResponseEntity.ok(new Response(HttpStatus.OK, walletResponse, wallet));
    }

    @PutMapping("/transfer")
    public ResponseEntity<Response> transferWallet(@RequestBody Map<String, Object> request, @RequestHeader("loggedInWalletId") String walletId){
        Object amountObj = request.get("amount");
        Object receiverAccountNumberObj = request.get("accountNumber");
        BigDecimal amount = new BigDecimal(amountObj.toString());
        String receiverAccountNumber = receiverAccountNumberObj.toString();
        Response isTransfer = walletService.transfer(amount, receiverAccountNumber, walletId);
        //WalletDto wallet = walletService.readOne(walletId);
        //if (isTransfer.equals("Transaction Successful")){
//            return new ResponseEntity<>("Transfer Successful", HttpStatus.OK);
            return ResponseEntity.ok(isTransfer);
        //}
        //return ResponseEntity.status(HttpStatus.FORBIDDEN)
          //      .body(new Response(HttpStatus.FORBIDDEN, isTransfer, wallet));
    }
}
