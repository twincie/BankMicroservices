package com.example.bankusers.service.impl;

import com.example.bankusers.controller.UsersControllers;
import com.example.bankusers.dto.UsersResponseDto;
import com.example.bankusers.dto.userDto;
import com.example.bankusers.entity.Response;
import com.example.bankusers.entity.Role;
import com.example.bankusers.entity.Users;
import com.example.bankusers.external.Wallet;
import com.example.bankusers.mapper.UserMapper;
import com.example.bankusers.repository.UsersRepository;
import com.example.bankusers.service.UsersService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    private UsersRepository usersRepository;
    String walletServiceBaseUrl = "http://BANK-WALLET:8082/api/v1/wallet";

    private UsersResponseDto convertToDto(Users user){
        //Wallet wallet = restTemplate.getForObject(walletServiceBaseUrl+"/"+user.getWalletId(), Wallet.class);
        UsersResponseDto usersResponseDto = UserMapper.userMapperDto(user);
        return usersResponseDto;
    }

    public Users create(Users user){
        String emptyJson = "{}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(emptyJson, headers);
        Wallet wallet = restTemplate.postForObject(walletServiceBaseUrl,requestEntity,Wallet.class);
        assert wallet != null;
        user.setWalletId(wallet.getId());
        user.setRole(Role.USER);
        return usersRepository.save(user);
    }

    @Override
    public Response readOne(String userId) {
        try {
            Response response = new Response();
            Long userIdToLong = Long.parseLong(userId);
            Users user = usersRepository.findById(userIdToLong).orElseThrow(null);
            response.setStatus(HttpStatus.OK);
            response.setMessage("Details of user");
            response.setBody(convertToDto(user));
            return response;
        } catch (Exception e) {
            Response response = new Response();
            System.out.println("Error: "+ e);
            response.setStatus(HttpStatus.FORBIDDEN);
            response.setMessage("an exception occured, wait a while.");
            return response;
        }

    }

    @Override
//    @CircuitBreaker(name = "companyBreaker", fallbackMethod = "companyBreakerFallback")
//    @Retry(name = "companyBreaker", fallbackMethod = "companyBreakerFallback")
//    @RateLimiter(name = "companyBreaker", fallbackMethod = "companyBreakerFallback")
    public Response readAll(String role ) {
        if(role == null || !role.equalsIgnoreCase("admin")){
            return new Response(HttpStatus.FORBIDDEN,
                    "User does not have permission to access this resource.", null);
        }
        List<Users> usersList = usersRepository.findAll();
        List<UsersResponseDto> usersDtoList = new ArrayList<>();
        for (Users user : usersList) {
            usersDtoList.add(convertToDto(user));
        }
        return new Response(HttpStatus.OK,
                "List of all Users", usersDtoList);
    }

    public List<String> companyBreakerFallback(Exception e) {
        List<String> fallback = new ArrayList<>();
        fallback.add("User does not have permission to access this resource.");
        return fallback;
    }

    @Override
    public Response update(Users updater, String userId) {
//        Long userIdToLong = Long.parseLong(userId);
//        updater.setId(userIdToLong);
//        Users users = usersRepository.save(updater);
//        return new Response(HttpStatus.OK,
//                "User Updated Successfully ", users);
        Long userIdToLong = Long.parseLong(userId);
        Optional<Users> optionalUser = usersRepository.findById(userIdToLong);
        if (optionalUser.isPresent()) {
            Users existingUser = optionalUser.get();
            // Update non-null fields from updater
            if (updater.getFirstName() != null) {
                existingUser.setFirstName(updater.getFirstName());
            }
            if (updater.getLastName() != null) {
                existingUser.setLastName(updater.getLastName());
            }
            if (updater.getGender() != null) {
                existingUser.setGender(updater.getGender());
            }
            if (updater.getDateOfBirth() != null) {
                existingUser.setDateOfBirth(updater.getDateOfBirth());
            }
            usersRepository.save(existingUser);
            Users updatedUser = usersRepository.findById(userIdToLong).orElseThrow(null);
            UsersResponseDto user = convertToDto(updatedUser);
            return new Response(HttpStatus.OK, "User updated successfully", user);
        } else {
            return new Response(HttpStatus.NOT_FOUND, "User not found", null);
        }
    }

    @Override
    public void delete(Long id) {
        usersRepository.deleteById(id);
    }

    @Override
    public UserDetailsService userDetailsService(){
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return usersRepository.findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            }
        };
    }

}
