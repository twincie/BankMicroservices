package com.example.bankusers.dto;

import com.example.bankusers.entity.Gender;
import com.example.bankusers.entity.Role;
import com.example.bankusers.external.Wallet;
import lombok.Data;

import java.time.LocalDate;


@Data
public class userDto {
    private Long id;
    private String firstName;
    private String lastName;
    private Gender gender;
    private LocalDate dateOfBirth;
    private String username;
    private String email;
    private Role role;
    private Long walletId;
}
