package com.example.bankusers.mapper;

import com.example.bankusers.dto.UsersResponseDto;
import com.example.bankusers.entity.Users;
import com.example.bankusers.external.Wallet;

public class UserMapper {

    public static UsersResponseDto userMapperDto(Users user, Wallet wallet){
        UsersResponseDto usersResponseDto = new UsersResponseDto();
        usersResponseDto.setId(user.getId());
        usersResponseDto.setUsername(user.getUsername());
        usersResponseDto.setEmail(user.getEmail());
        usersResponseDto.setRole(user.getRole());
        usersResponseDto.setWallet(wallet);
        return usersResponseDto;
    }
}
