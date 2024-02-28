package com.example.bankusers.controller;

import com.example.bankusers.dto.UsersResponseDto;
import com.example.bankusers.entity.Users;
import com.example.bankusers.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UsersControllers {
    @Autowired
    private UsersService usersService;

    // only for admin
    @GetMapping
    public List<Users> readAllUsers(){
        return usersService.readAll();
    }

    @PostMapping
    public Users createUser(@RequestBody Users users){
        return usersService.create(users);
    }
    @GetMapping("/{id}")
    public UsersResponseDto readOneUser(@PathVariable Long id){
        return usersService.readOne(id);
    }

    @PutMapping("/{id}")
    public Users updateUser(@PathVariable Long id, @RequestBody Users users){
        return usersService.update(id, users);
    }

    //for admin
    @DeleteMapping("{id}/delete")
    public void deleteUser(@PathVariable Long id){
        usersService.delete(id);
    }

}
