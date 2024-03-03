package com.example.bankusers.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class SignUpRequest {

    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
}
