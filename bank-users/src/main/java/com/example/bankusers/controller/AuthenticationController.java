package com.example.bankusers.controller;

import com.example.bankusers.dto.*;
import com.example.bankusers.entity.Users;
import com.example.bankusers.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

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
        UsersResponseDto usersResponseDto = authenticationService.signup(signUpRequest);
        if (usersResponseDto != null){
            return ResponseEntity.ok(new Response(HttpStatus.OK, usersResponseDto));
        }
        return ResponseEntity.ok(new Response(HttpStatus.FORBIDDEN, "User Already Exists"));
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtAuthenticationResponse> signin(@RequestBody SigninRequest signinRequest){
        return ResponseEntity.ok(authenticationService.signin(signinRequest));
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
