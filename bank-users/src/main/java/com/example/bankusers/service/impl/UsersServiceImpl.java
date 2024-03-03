package com.example.bankusers.service.impl;

import com.example.bankusers.dto.UsersResponseDto;
import com.example.bankusers.entity.Role;
import com.example.bankusers.entity.Users;
import com.example.bankusers.external.Wallet;
import com.example.bankusers.mapper.UserMapper;
import com.example.bankusers.repository.UsersRepository;
import com.example.bankusers.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
        Wallet wallet = restTemplate.getForObject(walletServiceBaseUrl+"/"+user.getWalletId(), Wallet.class);
        UsersResponseDto usersResponseDto = UserMapper.userMapperDto(user, wallet);
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
    public UsersResponseDto readOne(Long id) {
//        Users user = usersRepository.findById(id).orElseThrow(null);
        Optional<Users> optionalUsers = usersRepository.findById(id);
        Users user = new Users();
        if (optionalUsers.isPresent()){
            user.setId(optionalUsers.get().getId());
            user.setUsername(optionalUsers.get().getUsername());
            user.setEmail(optionalUsers.get().getEmail());
            user.setPassword(optionalUsers.get().getPassword());
            user.setRole(optionalUsers.get().getRole());
            user.setWalletId(optionalUsers.get().getWalletId());
            return convertToDto(user);
        } else {
            return null;
        }
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
