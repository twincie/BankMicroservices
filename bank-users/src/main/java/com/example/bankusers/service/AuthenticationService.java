package com.example.bankusers.service;


import com.example.bankusers.dto.*;

public interface AuthenticationService {

    DefaultApiResponse signup(SignUpRequest signUpRequest);

    DefaultApiResponse signin(SigninRequest signinRequest);

    DefaultApiResponse requestToken(RefreshTokenRequest refreshTokenRequest);

    DefaultApiResponse validateToken();
}
