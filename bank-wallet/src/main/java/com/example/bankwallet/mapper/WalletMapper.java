package com.example.bankwallet.mapper;

import com.example.bankwallet.dto.WalletDto;
import com.example.bankwallet.entity.Wallet;
import com.example.bankwallet.external.Transaction;

import java.util.List;

public class WalletMapper {

    public static WalletDto walletMapperDto(Wallet wallet, List<Transaction> transaction){

        WalletDto walletDto = new WalletDto();
        walletDto.setId(wallet.getId());
        walletDto.setAccountNumber(wallet.getAccountNumber());
        walletDto.setAmount(wallet.getAmount());
        walletDto.setTransaction(transaction);

        return walletDto;

    }
}
