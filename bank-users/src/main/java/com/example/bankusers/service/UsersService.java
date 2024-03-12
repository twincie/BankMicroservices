package com.example.bankusers.service;


import com.example.bankusers.dto.UsersResponseDto;
import com.example.bankusers.dto.userDto;
import com.example.bankusers.entity.Users;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UsersService {
    Users create(Users user);
    UsersResponseDto readOne(String userId);
    List<Users> readAll();
    public Users update(Users updater, String userId);
    void delete(Long id);

    UserDetailsService userDetailsService();
}
