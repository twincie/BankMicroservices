package com.example.bankusers.controller;

import com.example.bankusers.dto.*;
import com.example.bankusers.dto.DefaultApiResponse;
import com.example.bankusers.repository.UsersRepository;
import com.example.bankusers.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    @Autowired
    UsersRepository usersRepository;

    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<DefaultApiResponse> signup(@RequestBody SignUpRequest signUpRequest){
            DefaultApiResponse response = authenticationService.signup(signUpRequest);
            return ResponseEntity.ok(response);
    }

    @PostMapping("/signin")
    public ResponseEntity<DefaultApiResponse> signin(@RequestBody SigninRequest signinRequest){
        DefaultApiResponse response = authenticationService.signin(signinRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<DefaultApiResponse> validateToken(){
        return ResponseEntity.ok(authenticationService.validateToken());
    }

    @PostMapping("/refresh")
    public ResponseEntity<DefaultApiResponse> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest){
        return ResponseEntity.ok(authenticationService.requestToken(refreshTokenRequest));
    }
}
