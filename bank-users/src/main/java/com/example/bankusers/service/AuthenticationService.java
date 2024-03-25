package com.example.bankusers.service;


import com.example.bankusers.dto.*;
import com.example.bankusers.entity.Response;

public interface AuthenticationService {

    Response signup(SignUpRequest signUpRequest);

    Response signin(SigninRequest signinRequest);

    Response requestToken(RefreshTokenRequest refreshTokenRequest);

    Response validateToken();
}
