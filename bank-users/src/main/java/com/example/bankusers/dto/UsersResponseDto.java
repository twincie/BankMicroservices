package com.example.bankusers.dto;

import com.example.bankusers.entity.Role;
import com.example.bankusers.external.Wallet;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class UsersResponseDto {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private Wallet wallet;
}
