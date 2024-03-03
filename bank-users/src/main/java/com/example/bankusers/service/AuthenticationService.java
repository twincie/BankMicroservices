package com.example.bankusers.service;


import com.example.bankusers.dto.JwtAuthenticationResponse;
import com.example.bankusers.dto.RefreshTokenRequest;
import com.example.bankusers.dto.SignUpRequest;
import com.example.bankusers.dto.SigninRequest;
import com.example.bankusers.entity.Users;

public interface AuthenticationService {

    Users signup(SignUpRequest signUpRequest);

    JwtAuthenticationResponse signin(SigninRequest signinRequest);

    JwtAuthenticationResponse requestToken(RefreshTokenRequest refreshTokenRequest);

    String validateToken();
}
