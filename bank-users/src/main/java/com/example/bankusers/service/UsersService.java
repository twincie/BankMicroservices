package com.example.bankusers.service;


import com.example.bankusers.dto.UsersResponseDto;
import com.example.bankusers.entity.Users;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UsersService {
    Users create(Users user);
    UsersResponseDto readOne(Long id);
    List<Users> readAll();
    public Users update(Long id, Users updater);
    void delete(Long id);
//    Optional<Users> userTopup(Long id, BigDecimal amount);
//    void userWithdraw(Long userId, BigDecimal amount);
//    void userTransfer(Long id, TransferRequest transferRequest);

//    List<Transaction> getUsersTransactions(Long id);

}
