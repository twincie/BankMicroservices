package com.example.bankwallet.controller;

import com.example.bankwallet.dto.WalletDto;
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

    static class Response{
        private final HttpStatus status;
        private  final String message;
        private final Object body;

        Response(HttpStatus status, String message, Object body) {
            this.status = status;
            this.message = message;
            this.body = body;
        }
        public HttpStatus getStatus() {
            return status;
        }

        public Object getBody() {
            return body;
        }

        public String getMessage() {
            return message;
        }
    }

    @PostMapping
    public Wallet createWallet(@RequestBody Wallet wallet){
        return walletService.create(wallet);
    }

    @GetMapping("/details")
    public ResponseEntity<Response> readOneWallet(@RequestHeader("role") String role, @RequestHeader("loggedInWalletId") String walletId){
        WalletDto wallet = walletService.readOne(walletId);
        return ResponseEntity.ok(new Response(HttpStatus.OK, "wallet details retrieval successful", wallet));
    }

    @GetMapping
    public ResponseEntity<Response> readAllWallets(@RequestHeader("role") String role, @RequestHeader("loggedInWalletId") String walletId){
        if (role.equalsIgnoreCase("admin")){
            List<Wallet> walletList = walletService.readAll();
            return ResponseEntity.ok(new Response(HttpStatus.OK, "list of all wallets", walletList));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new Response(HttpStatus.FORBIDDEN, "user cant access this resource. ", null));
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
        String walletResponse = walletService.topup(amount, walletId);
        WalletDto wallet = walletService.readOne(walletId);
//        return new ResponseEntity<>("Topup Successful", HttpStatus.OK);
        if (walletResponse.equals("Withdrawal Successful")){
            return ResponseEntity.ok(new Response(HttpStatus.OK, walletResponse, wallet));
        }
        return ResponseEntity.ok(new Response(HttpStatus.OK, walletResponse, wallet));
    }

    @PutMapping("/withdraw")
    public ResponseEntity<Response> withdrawWallet(@RequestBody Map<String, BigDecimal> request, @RequestHeader("loggedInWalletId") String walletId){
        BigDecimal amount = request.get("amount");
        String walletResponse = walletService.withdraw(amount, walletId);
        WalletDto wallet = walletService.readOne(walletId);
//        return new ResponseEntity<>("Withdraw Successful",HttpStatus.OK);
        if (walletResponse.equals("Withdrawal Successful")){
            return ResponseEntity.ok(new Response(HttpStatus.OK, walletResponse, wallet));
        }
        return ResponseEntity.ok(new Response(HttpStatus.OK, walletResponse, wallet));
    }

    @PutMapping("/transfer")
    public ResponseEntity<Response> transferWallet(@RequestBody Map<String, Object> request, @RequestHeader("loggedInWalletId") String walletId){
        Object amountObj = request.get("amount");
        Object receiverAccountNumberObj = request.get("accountNumber");
        BigDecimal amount = new BigDecimal(amountObj.toString());
        String receiverAccountNumber = receiverAccountNumberObj.toString();
        String isTransfer = walletService.transfer(amount, receiverAccountNumber, walletId);
        WalletDto wallet = walletService.readOne(walletId);
        if (isTransfer.equals("Transaction Successful")){
//            return new ResponseEntity<>("Transfer Successful", HttpStatus.OK);
            return ResponseEntity.ok(new Response(HttpStatus.OK, isTransfer, wallet));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new Response(HttpStatus.FORBIDDEN, isTransfer, wallet));
    }
}
