package com.example.bankusers.service;

import com.example.bankusers.entity.Users;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;


public interface JWTService {
    String extractUserName(String token);
    String generateToken(Users users);

    boolean isTokenValid(String token, UserDetails userDetails);

    String generateRefreshToken(Map<String, Object> extraClaims, Users users);
}
