package com.example.bankusers.service.impl;

import com.example.bankusers.config.ExtendedConstants;
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
        return UserMapper.userMapperDto(user);
    }

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

    public DefaultApiResponse signup(SignUpRequest signUpRequest){
        DefaultApiResponse response = new DefaultApiResponse();
        if (!usersRepository.existsByUsername(signUpRequest.getUsername())){
            if (!usersRepository.existsByEmail(signUpRequest.getEmail())) {
                String password = signUpRequest.getPassword();
                int strength = calculatePasswordStrength(password);
                if (strength < 3) {
                    response.setStatus(ExtendedConstants.ResponseCode.WEAK_PASSWORD.getCode());
                    response.setMessage(ExtendedConstants.ResponseCode.WEAK_PASSWORD.getDescription());
                    return response;
                }
                Users users = new Users();
                users.setEmail(signUpRequest.getEmail());
                users.setUsername(signUpRequest.getUsername());
                users.setRole(Role.USER);
                users.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

                DefaultApiResponse apiResponse = usersService.create(users);
                // return convertToDto(user);
                if (ExtendedConstants.ResponseCode.SUCCESS.getCode().equalsIgnoreCase(apiResponse.getStatus())) {
                    response.setStatus(ExtendedConstants.ResponseCode.SUCCESS.getCode());
                    response.setMessage(ExtendedConstants.ResponseCode.EMAIL_EXISTS.getDescription());
//                    response.setData(convertToDto(user));
                }
                return response;
            }
            response.setStatus(ExtendedConstants.ResponseCode.EMAIL_EXISTS.getCode());
            response.setMessage(ExtendedConstants.ResponseCode.EMAIL_EXISTS.getDescription());
            return response;
        }
        response.setStatus(ExtendedConstants.ResponseCode.USERNAME_EXISTS.getCode());
        response.setMessage(ExtendedConstants.ResponseCode.USERNAME_EXISTS.getDescription());
        return response;
    }

    public DefaultApiResponse signin(SigninRequest signinRequest){
        DefaultApiResponse response = new DefaultApiResponse();
        if (usersRepository.existsByUsername(signinRequest.getUsername())) {
            try {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(signinRequest.getUsername(), signinRequest.getPassword());
                System.out.println(usernamePasswordAuthenticationToken);
                Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

                var users = usersRepository.findByUsername(signinRequest.getUsername()).orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
                var jwt = jwtService.generateToken(users);
                var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), users);

                JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();

                jwtAuthenticationResponse.setToken(jwt);
                jwtAuthenticationResponse.setRefreshToken(refreshToken);
                response.setStatus(ExtendedConstants.ResponseCode.SUCCESS.getCode());
                response.setMessage(ExtendedConstants.ResponseCode.SUCCESS.getDescription());
                response.setData(jwtAuthenticationResponse);
                return response;
                // return jwtAuthenticationResponse;
            } catch (AuthenticationException e) {
                //throw new IllegalArgumentException("Authentication failed: " + e.getMessage());
                System.out.println("Authentication failed: " + e.getMessage());
                response.setStatus(ExtendedConstants.ResponseCode.WRONG_PASSWORD.getCode());
                response.setMessage("Wrong Password");
                return response;
            }
        }
        response.setStatus(ExtendedConstants.ResponseCode.USERNAME_NOT_FOUND.getCode());
        response.setMessage(ExtendedConstants.ResponseCode.USERNAME_NOT_FOUND.getDescription());
        return response;
    }

    public DefaultApiResponse requestToken(RefreshTokenRequest refreshTokenRequest){
        DefaultApiResponse response = new DefaultApiResponse();
        String userName = jwtService.extractUserName(refreshTokenRequest.getToken());
        Users user = usersRepository.findByUsername(userName).orElseThrow();
        if (jwtService.isTokenValid(refreshTokenRequest.getToken(), user)){
            var jwt = jwtService.generateToken(user);
            JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();

            jwtAuthenticationResponse.setToken(jwt);
            jwtAuthenticationResponse.setRefreshToken(refreshTokenRequest.getToken());
            // return jwtAuthenticationResponse;
            response.setStatus(ExtendedConstants.ResponseCode.SUCCESS.getCode());
            response.setMessage(ExtendedConstants.ResponseCode.SUCCESS.getDescription());
            response.setData(jwtAuthenticationResponse);
//            Response(HttpStatus.FORBIDDEN, "Token is valid!!", jwtAuthenticationResponse);
            return response;
        }
        response.setStatus(ExtendedConstants.ResponseCode.INVALID_TOKEN.getCode());
        response.setMessage(ExtendedConstants.ResponseCode.INVALID_TOKEN.getDescription());
//        return new Response(HttpStatus.FORBIDDEN, "Token not valid!!", null);
        return response;
    }

    public DefaultApiResponse validateToken(){
        DefaultApiResponse response = new DefaultApiResponse();
//        String userName = jwtService.extractUserName(token);
//        Users user = usersRepository.findByUsername(userName).orElseThrow();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null){
            response.setStatus(ExtendedConstants.ResponseCode.INVALID_TOKEN.getCode());
            response.setMessage(ExtendedConstants.ResponseCode.INVALID_TOKEN.getDescription());
            return response;
//            return new Response(HttpStatus.FORBIDDEN, "Token not valid!!", null);
        }
        response.setStatus(ExtendedConstants.ResponseCode.SUCCESS.getCode());
        response.setMessage(ExtendedConstants.ResponseCode.SUCCESS.getDescription());
        response.setData(authentication);
        return response;
//        return new Response(HttpStatus.FORBIDDEN, "Token is valid!!",  authentication);

//        if (jwtService.isTokenValid(token, user)){
//            return "Token is valid";
//        }
//        return "Token not valid!!";

    };
}
