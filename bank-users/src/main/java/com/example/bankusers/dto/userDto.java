package com.example.bankusers.dto;

import com.example.bankusers.entity.Role;
import com.example.bankusers.external.Wallet;
import lombok.Data;


@Data
public class userDto {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private Long walletId;
}
