package com.example.bankusers.service.impl;

import com.example.bankusers.config.ExtendedConstants;
import com.example.bankusers.dto.DefaultApiResponse;
import com.example.bankusers.dto.UsersResponseDto;
import com.example.bankusers.entity.Role;
import com.example.bankusers.entity.Users;
import com.example.bankusers.external.Wallet;
import com.example.bankusers.mapper.UserMapper;
import com.example.bankusers.repository.UsersRepository;
import com.example.bankusers.service.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
//    String walletBaseUrl = "${wallet-service.url}";
//    String walletServiceBaseUrl = walletBaseUrl + "/api/v1/wallet";
    String walletServiceBaseUrl = "http://BANK-WALLET:8082/api/v1/wallet";

    private UsersResponseDto convertToDto(Users user){
        //Wallet wallet = restTemplate.getForObject(walletServiceBaseUrl+"/"+user.getWalletId(), Wallet.class);
        UsersResponseDto usersResponseDto = UserMapper.userMapperDto(user);
        return usersResponseDto;
    }

    public DefaultApiResponse create(Users user){
        DefaultApiResponse apiResponse = new DefaultApiResponse();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            DefaultApiResponse response = restTemplate.postForObject(walletServiceBaseUrl, requestEntity, DefaultApiResponse.class);
            if (!response.getStatus().equalsIgnoreCase("00") || response == null){
                apiResponse.setStatus(ExtendedConstants.ResponseCode.FAILED.getCode());
                apiResponse.setMessage(ExtendedConstants.ResponseCode.FAILED.getDescription());
                return apiResponse;
            }
            ObjectMapper mapper = new ObjectMapper();
            Wallet wallet = mapper.convertValue(response.getData(), Wallet.class);
            user.setWalletId(wallet.getId());
            user.setRole(Role.USER);
            usersRepository.save(user);
            apiResponse.setStatus(ExtendedConstants.ResponseCode.SUCCESS.getCode());
            apiResponse.setMessage(ExtendedConstants.ResponseCode.SUCCESS.getDescription());
        } catch (Exception e) {
            System.out.println("Error: "+ e);
            apiResponse.setStatus(ExtendedConstants.ResponseCode.FAILED.getCode());
            apiResponse.setMessage(ExtendedConstants.ResponseCode.FAILED.getDescription());
        }
        return apiResponse;
    }

    @Override
    public DefaultApiResponse readOne(String userId) {
        DefaultApiResponse response = new DefaultApiResponse();
        try {
            Long userIdToLong = Long.parseLong(userId);
            Users user = usersRepository.findById(userIdToLong).orElseThrow(null);
            response.setStatus(ExtendedConstants.ResponseCode.SUCCESS.getCode());
            response.setMessage(ExtendedConstants.ResponseCode.SUCCESS.getDescription());
            response.setData(convertToDto(user));
        } catch (Exception e) {
            System.out.println("Error: "+ e);
            response.setStatus(ExtendedConstants.ResponseCode.FAILED.getCode());
            response.setMessage(ExtendedConstants.ResponseCode.FAILED.getDescription());
        }
        return response;
    }

    @Override
    public DefaultApiResponse readAll(String role ) {
        DefaultApiResponse response = new DefaultApiResponse();
        if(role == null || !role.equalsIgnoreCase("admin")){
            response.setStatus(ExtendedConstants.ResponseCode.NO_PERMISSION.getCode());
            response.setMessage(ExtendedConstants.ResponseCode.NO_PERMISSION.getDescription());
            return response;
        }
        try {
            List<Users> usersList = usersRepository.findAll();
            List<UsersResponseDto> usersDtoList = new ArrayList<>();
            for (Users user : usersList) {
                usersDtoList.add(convertToDto(user));
            }
            response.setStatus(ExtendedConstants.ResponseCode.SUCCESS.getCode());
            response.setMessage(ExtendedConstants.ResponseCode.SUCCESS.getDescription());
            response.setData(usersDtoList);
        } catch (Exception e) {
        System.out.println("Error: "+ e);
        response.setStatus(ExtendedConstants.ResponseCode.FAILED.getCode());
        response.setMessage(ExtendedConstants.ResponseCode.FAILED.getDescription());
    }
        return response;
    }

    @Override
    public DefaultApiResponse update(Users updater, String userId) {
        DefaultApiResponse response = new DefaultApiResponse();
        Long userIdToLong = Long.parseLong(userId);
        try {
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
                response.setStatus(ExtendedConstants.ResponseCode.SUCCESS.getCode());
                response.setMessage(ExtendedConstants.ResponseCode.SUCCESS.getDescription());
                response.setData(user);
            } else {
                response.setStatus(ExtendedConstants.ResponseCode.NOT_FOUND.getCode());
                response.setMessage(ExtendedConstants.ResponseCode.NOT_FOUND.getDescription());
            }
        } catch (Exception e) {
            System.out.println("Error: "+ e);
            response.setStatus(ExtendedConstants.ResponseCode.FAILED.getCode());
            response.setMessage(ExtendedConstants.ResponseCode.FAILED.getDescription());
        }
        return response;
    }

    @Override
    public DefaultApiResponse delete(Long id) {
        DefaultApiResponse response = new DefaultApiResponse();
        try {
            usersRepository.deleteById(id);
            response.setStatus(ExtendedConstants.ResponseCode.SUCCESS.getCode());
            response.setMessage(ExtendedConstants.ResponseCode.SUCCESS.getDescription());
        } catch (Exception e) {
            System.out.println("Error: "+ e);
            response.setStatus(ExtendedConstants.ResponseCode.FAILED.getCode());
            response.setMessage(ExtendedConstants.ResponseCode.FAILED.getDescription());
        }
        return response;
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
