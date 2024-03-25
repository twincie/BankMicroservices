package com.example.bankusers.service;


import com.example.bankusers.dto.UsersResponseDto;
import com.example.bankusers.dto.userDto;
import com.example.bankusers.entity.Response;
import com.example.bankusers.entity.Users;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UsersService {
    Users create(Users user);
    Response readOne(String userId);
    Response readAll(String role);
    Response update(Users updater, String userId);
    void delete(Long id);

    UserDetailsService userDetailsService();
}
