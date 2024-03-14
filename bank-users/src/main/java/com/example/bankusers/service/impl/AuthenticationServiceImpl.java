package com.example.bankusers.service.impl;

import com.example.bankusers.dto.*;
import com.example.bankusers.entity.Role;
import com.example.bankusers.entity.Users;
import com.example.bankusers.mapper.UserMapper;
import com.example.bankusers.repository.UsersRepository;
import com.example.bankusers.service.AuthenticationService;
import com.example.bankusers.service.JWTService;
import com.example.bankusers.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UsersService usersService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    private UsersResponseDto convertToDto(Users user){
        //Wallet wallet = restTemplate.getForObject(walletServiceBaseUrl+"/"+user.getWalletId(), Wallet.class);
        UsersResponseDto usersResponseDto = UserMapper.userMapperDto(user);
        return usersResponseDto;
    }

    public UsersResponseDto signup(SignUpRequest signUpRequest){
        if (!usersRepository.existsByUsername(signUpRequest.getUsername())){
            if (!usersRepository.existsByEmail(signUpRequest.getEmail())) {
                Users users = new Users();
                users.setEmail(signUpRequest.getEmail());
                users.setUsername(signUpRequest.getUsername());
                users.setRole(Role.USER);
                users.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

                Users user = usersService.create(users);
                return convertToDto(user);
            }
            return null;
        }
        return null;
    }

    public JwtAuthenticationResponse signin(SigninRequest signinRequest){
        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(signinRequest.getUsername(), signinRequest.getPassword());
            System.out.println(usernamePasswordAuthenticationToken);
            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

            var users = usersRepository.findByUsername(signinRequest.getUsername()).orElseThrow(()-> new IllegalArgumentException("Invalid email or password."));
            var jwt = jwtService.generateToken(users);
            var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), users);

            JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();

            jwtAuthenticationResponse.setToken(jwt);
            jwtAuthenticationResponse.setRefreshToken(refreshToken);
            return jwtAuthenticationResponse;
        } catch (AuthenticationException e){
            throw new IllegalArgumentException("Authentication failed: "+ e.getMessage());
        }
    }

    public JwtAuthenticationResponse requestToken(RefreshTokenRequest refreshTokenRequest){
        String userName = jwtService.extractUserName(refreshTokenRequest.getToken());
        Users user = usersRepository.findByUsername(userName).orElseThrow();
        if (jwtService.isTokenValid(refreshTokenRequest.getToken(), user)){
            var jwt = jwtService.generateToken(user);
            JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();

            jwtAuthenticationResponse.setToken(jwt);
            jwtAuthenticationResponse.setRefreshToken(refreshTokenRequest.getToken());
            return jwtAuthenticationResponse;
        }
        return null;
    }

    public String validateToken(){
//        String userName = jwtService.extractUserName(token);
//        Users user = usersRepository.findByUsername(userName).orElseThrow();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return "Token not valid!!";
        return "Token is valid";
//        if (jwtService.isTokenValid(token, user)){
//            return "Token is valid";
//        }
//        return "Token not valid!!";

    };
}
