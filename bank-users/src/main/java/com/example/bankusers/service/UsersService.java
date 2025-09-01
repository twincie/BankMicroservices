package com.example.bankusers.service;


import com.example.bankusers.dto.DefaultApiResponse;
import com.example.bankusers.entity.Users;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UsersService {
    UserDetailsService userDetailsService();
    DefaultApiResponse create(Users user);
    DefaultApiResponse readOne(String userId);
    DefaultApiResponse readAll(String role);
    DefaultApiResponse update(Users updater, String userId);
    DefaultApiResponse delete(Long id);

}
