package com.example.bankusers.service.impl;

import com.example.bankusers.dto.UsersResponseDto;
import com.example.bankusers.entity.Role;
import com.example.bankusers.entity.Users;
import com.example.bankusers.external.Wallet;
import com.example.bankusers.mapper.UserMapper;
import com.example.bankusers.repository.UsersRepository;
import com.example.bankusers.service.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    @Autowired
    RestTemplate restTemplate;

    String walletServiceBaseUrl = "http://localhost:8082/api/v1/wallet";

    @Autowired
    private UsersRepository usersRepository;



    private UsersResponseDto convertToDto(Users user){
        Wallet wallet = restTemplate.getForObject(walletServiceBaseUrl+"/"+user.getWalletId(), Wallet.class);
        UsersResponseDto usersResponseDto = UserMapper.userMapperDto(user, wallet);
        return usersResponseDto;
    }

    //    =================================== CRUD START =================================
    public Users create(Users user){
        ObjectMapper objectMapper = new ObjectMapper();
        // Create an empty JSON object
        String emptyJson = "{}";

        // Set up HttpHeaders with content type as application/json
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create an HttpEntity with the empty JSON object and headers
        HttpEntity<String> requestEntity = new HttpEntity<>(emptyJson, headers);
        Wallet wallet = restTemplate.postForObject(walletServiceBaseUrl,requestEntity,Wallet.class);
        assert wallet != null;
        user.setWalletId(wallet.getId());
        user.setRole(Role.USER);
        return usersRepository.save(user);
    }

    @Override
    public UsersResponseDto readOne(Long id) {
        Users user = usersRepository.findById(id).orElseThrow(null);
        return convertToDto(user);
    }

    @Override
    public List<Users> readAll() {
        return usersRepository.findAll();
    }

    @Override
    public Users update(Long id, Users updater) {
        updater.setId(id);
        return usersRepository.save(updater);
    }

    @Override
    public void delete(Long id) {
        usersRepository.deleteById(id);
    }
}
