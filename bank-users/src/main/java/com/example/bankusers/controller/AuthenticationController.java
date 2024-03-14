package com.example.bankusers.controller;

import com.example.bankusers.dto.*;
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

    private int calculatePasswordStrength(String password) {
        int strength = 0;
        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasSymbol = false;
        boolean hasNumber = false;
        // Iterate through each character of the password
        for (char ch : password.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(ch)) {
                hasLowercase = true;
            } else if (Character.isDigit(ch)) {
                hasNumber = true;
            } else {
                hasSymbol = true;
            }
        }
        // Increment strength for each requirement met
        if (hasUppercase) strength++;
        if (hasLowercase) strength++;
        if (hasNumber) strength++;
        if (hasSymbol) strength++;

        return strength;
    }

    static class Response{
        private final HttpStatus status;
        private final Object body;

        Response(HttpStatus status, Object body) {
            this.status = status;
            this.body = body;
        }
        public HttpStatus getStatus() {
            return status;
        }

        public Object getBody() {
            return body;
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<Response> signup(@RequestBody SignUpRequest signUpRequest){
        String password = signUpRequest.getPassword();
        int strength = calculatePasswordStrength(password);
        if (strength < 3) {
            return ResponseEntity.ok(new Response(HttpStatus.FORBIDDEN, "Weak password"));
        } else {
            UsersResponseDto usersResponseDto = authenticationService.signup(signUpRequest);
            if (usersResponseDto != null) {
                return ResponseEntity.ok(new Response(HttpStatus.OK, usersResponseDto));
            }
            return ResponseEntity.ok(new Response(HttpStatus.FORBIDDEN, "User Already Exists"));
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<Response> signin(@RequestBody SigninRequest signinRequest){
        if (usersRepository.existsByUsername(signinRequest.getUsername())){
            try{
            JwtAuthenticationResponse jwtAuthenticationResponse = authenticationService.signin(signinRequest);
                return ResponseEntity.ok(new Response(HttpStatus.OK, jwtAuthenticationResponse));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new Response(HttpStatus.FORBIDDEN, "Wrong Password"));
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new Response(HttpStatus.FORBIDDEN, "Username doesnt exist"));
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(){
        return ResponseEntity.ok(authenticationService.validateToken());
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationResponse> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest){
        return ResponseEntity.ok(authenticationService.requestToken(refreshTokenRequest));
    }
}
