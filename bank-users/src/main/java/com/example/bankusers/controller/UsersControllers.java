package com.example.bankusers.controller;

import com.example.bankusers.dto.UsersResponseDto;
import com.example.bankusers.dto.userDto;
import com.example.bankusers.entity.Users;
import com.example.bankusers.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UsersControllers {
    @Autowired
    private UsersService usersService;

    // only for admin
    @GetMapping
    public List<Users> readAllUsers(@RequestHeader("loggedInUserId") String userId){
        return usersService.readAll(userId);
    }

    @PostMapping
    public Users createUser(@RequestBody Users users){
        return usersService.create(users);
    }
    @GetMapping("/{id}")
    public UsersResponseDto readOneUser(@PathVariable Long id, @RequestHeader("loggedInUserId") String userId){
        System.out.println(userId);
        return usersService.readOne(id,userId);
    }

    @PutMapping("/{id}")
    public Users updateUser(@PathVariable Long id, @RequestBody Users users, @RequestHeader("loggedInUserId") String userId){
        return usersService.update(id, users, userId);
    }

    //for admin
    @DeleteMapping("{id}/delete")
    public void deleteUser(@PathVariable Long id){
        usersService.delete(id);
    }

}
