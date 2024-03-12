package com.example.bankusers.service;


import com.example.bankusers.dto.*;

public interface AuthenticationService {

    UsersResponseDto signup(SignUpRequest signUpRequest);

    JwtAuthenticationResponse signin(SigninRequest signinRequest);

    JwtAuthenticationResponse requestToken(RefreshTokenRequest refreshTokenRequest);

    String validateToken();
}
