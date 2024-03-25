package com.example.bankusers.controller;

import com.example.bankusers.dto.*;
import com.example.bankusers.entity.Response;
import com.example.bankusers.entity.Users;
import com.example.bankusers.repository.UsersRepository;
import com.example.bankusers.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    @Autowired
    UsersRepository usersRepository;

    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<Response> signup(@RequestBody SignUpRequest signUpRequest){
            Response response = authenticationService.signup(signUpRequest);
            return ResponseEntity.ok(response);
    }

    @PostMapping("/signin")
    public ResponseEntity<Response> signin(@RequestBody SigninRequest signinRequest){
        Response response = authenticationService.signin(signinRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<Response> validateToken(){
        return ResponseEntity.ok(authenticationService.validateToken());
    }

    @PostMapping("/refresh")
    public ResponseEntity<Response> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest){
        return ResponseEntity.ok(authenticationService.requestToken(refreshTokenRequest));
    }
}
