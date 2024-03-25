package com.example.bankusers.mapper;

import com.example.bankusers.dto.UsersResponseDto;
import com.example.bankusers.dto.userDto;
import com.example.bankusers.entity.Users;
import com.example.bankusers.external.Wallet;

public class UserMapper {

    public static UsersResponseDto userMapperDto(Users user){
        UsersResponseDto usersResponseDto = new UsersResponseDto();
        usersResponseDto.setId(user.getId());
        usersResponseDto.setFirstName(user.getFirstName());
        usersResponseDto.setLastName(user.getLastName());
        usersResponseDto.setGender(user.getGender());
        usersResponseDto.setDateOfBirth(user.getDateOfBirth());
        usersResponseDto.setUsername(user.getUsername());
        usersResponseDto.setEmail(user.getEmail());
        usersResponseDto.setRole(user.getRole());
        usersResponseDto.setWalletId(user.getWalletId());
        return usersResponseDto;
    }
}
