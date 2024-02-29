package com.example.bankusers.service;


import com.example.bankusers.dto.UsersResponseDto;
import com.example.bankusers.entity.Users;

import java.util.List;

public interface UsersService {
    Users create(Users user);
    UsersResponseDto readOne(Long id);
    List<Users> readAll();
    public Users update(Long id, Users updater);
    void delete(Long id);
}
