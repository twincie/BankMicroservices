package com.example.bankusers.controller;

import com.example.bankusers.dto.UsersResponseDto;
import com.example.bankusers.dto.userDto;
import com.example.bankusers.entity.Response;
import com.example.bankusers.entity.Users;
import com.example.bankusers.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UsersControllers {
    @Autowired
    private UsersService usersService;

    @GetMapping
    public ResponseEntity<Response> readAllUsers(@RequestHeader("role") String role){
        Response response = usersService.readAll(role);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public Users createUser(@RequestBody Users users){
        return usersService.create(users);
    }

    @GetMapping("/details")
    public ResponseEntity<Response> readOneUser(@RequestHeader("loggedInUserId") String userId){
        Response response = usersService.readOne(userId);
        return ResponseEntity.ok(response);
//        return usersService.readOne(userId);
    }

    @PutMapping("/update")
    public ResponseEntity<Response> updateUser(@RequestBody Users users, @RequestHeader("loggedInUserId") String userId){
        Response response = usersService.update(users, userId);
        return ResponseEntity.ok(response);
    }

    //for admin
    @DeleteMapping("/delete")
    public void deleteUser(@PathVariable Long id){
        usersService.delete(id);
    }
}
