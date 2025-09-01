package com.example.bankusers.controller;

import com.example.bankusers.dto.DefaultApiResponse;
import com.example.bankusers.entity.Users;
import com.example.bankusers.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UsersControllers {
    @Autowired
    private UsersService usersService;

    @GetMapping
    public DefaultApiResponse readAllUsers(@RequestHeader("role") String role) {
        return usersService.readAll(role);
    }

    @PostMapping
    public DefaultApiResponse createUser(@RequestBody Users users){
        return usersService.create(users);
    }

    @GetMapping("/details")
    public DefaultApiResponse readOneUser(@RequestHeader("loggedInUserId") String userId){
        return usersService.readOne(userId);
    }

    @PutMapping("/update")
    public DefaultApiResponse updateUser(@RequestBody Users users, @RequestHeader("loggedInUserId") String userId){
        return usersService.update(users, userId);
    }

    //for admin
    @DeleteMapping("/delete/{id}")
    public DefaultApiResponse deleteUser(@PathVariable Long id){
        return usersService.delete(id);
    }
}
