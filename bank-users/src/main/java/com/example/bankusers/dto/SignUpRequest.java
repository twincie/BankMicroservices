package com.example.bankusers.dto;

import com.example.bankusers.entity.Gender;
import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SignUpRequest {
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private Gender gender;
    @Column(nullable = false)
    private LocalDate dateOfBirth;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
}
