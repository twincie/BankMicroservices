package com.example.bankusers.service.impl;

import com.example.bankusers.dto.*;
import com.example.bankusers.entity.Response;
import com.example.bankusers.entity.Role;
import com.example.bankusers.entity.Users;
import com.example.bankusers.mapper.UserMapper;
import com.example.bankusers.repository.UsersRepository;
import com.example.bankusers.service.AuthenticationService;
import com.example.bankusers.service.JWTService;
import com.example.bankusers.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public Response signup(SignUpRequest signUpRequest){
        Response response = new Response();
        if (!usersRepository.existsByUsername(signUpRequest.getUsername())){
            if (!usersRepository.existsByEmail(signUpRequest.getEmail())) {
                String password = signUpRequest.getPassword();
                int strength = calculatePasswordStrength(password);
                if (strength < 3) {
                    response.setStatus(HttpStatus.FORBIDDEN);
                    response.setMessage("Weak Password");
                    return response;
                }
                Users users = new Users();
                users.setEmail(signUpRequest.getEmail());
                users.setUsername(signUpRequest.getUsername());
                users.setRole(Role.USER);
                users.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

                Users user = usersService.create(users);
                // return convertToDto(user);
                response.setStatus(HttpStatus.OK);
                response.setMessage("User Registerd Successfully.");
                response.setBody(convertToDto(user));
                return response;
            }
            response.setStatus(HttpStatus.FORBIDDEN);
            response.setMessage("Email Already exists.");
            return response;
        }
        response.setStatus(HttpStatus.FORBIDDEN);
        response.setMessage("Username Already exists.");
        return response;
    }

    public Response signin(SigninRequest signinRequest){
        Response response = new Response();
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
                response.setStatus(HttpStatus.OK);
                response.setMessage("User logged in Successfully.");
                response.setBody(jwtAuthenticationResponse);
                return response;
                // return jwtAuthenticationResponse;
            } catch (AuthenticationException e) {
                //throw new IllegalArgumentException("Authentication failed: " + e.getMessage());
                System.out.println("Authentication failed: " + e.getMessage());
                response.setStatus(HttpStatus.FORBIDDEN);
                response.setMessage("Wrong Password");
                return response;
            }
        }
        response.setStatus(HttpStatus.FORBIDDEN);
        response.setMessage("Username doesnt exist");
        return response;
    }

    public Response requestToken(RefreshTokenRequest refreshTokenRequest){
        String userName = jwtService.extractUserName(refreshTokenRequest.getToken());
        Users user = usersRepository.findByUsername(userName).orElseThrow();
        if (jwtService.isTokenValid(refreshTokenRequest.getToken(), user)){
            var jwt = jwtService.generateToken(user);
            JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();

            jwtAuthenticationResponse.setToken(jwt);
            jwtAuthenticationResponse.setRefreshToken(refreshTokenRequest.getToken());
            // return jwtAuthenticationResponse;
            return new Response(HttpStatus.FORBIDDEN, "Token is valid!!", jwtAuthenticationResponse);
        }
        return new Response(HttpStatus.FORBIDDEN, "Token not valid!!", null);
    }

    public Response validateToken(){
//        String userName = jwtService.extractUserName(token);
//        Users user = usersRepository.findByUsername(userName).orElseThrow();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null){
            return new Response(HttpStatus.FORBIDDEN, "Token not valid!!", null);
        }
        return new Response(HttpStatus.FORBIDDEN, "Token is valid!!",  authentication);

//        if (jwtService.isTokenValid(token, user)){
//            return "Token is valid";
//        }
//        return "Token not valid!!";

    };
}
