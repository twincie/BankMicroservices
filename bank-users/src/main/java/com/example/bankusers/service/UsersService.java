package com.example.bankusers.service;


import com.example.bankusers.dto.UsersResponseDto;
import com.example.bankusers.dto.userDto;
import com.example.bankusers.entity.Users;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UsersService {
    Users create(Users user);
    UsersResponseDto readOne(Long id, String username);
    List<Users> readAll(String username);
    public Users update(Long id, Users updater, String username);
    void delete(Long id);

    UserDetailsService userDetailsService();
}
