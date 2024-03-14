package com.example.bankusers.controller;

import com.example.bankusers.dto.UsersResponseDto;
import com.example.bankusers.dto.userDto;
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

    static class Response{
        private final HttpStatus status;
        private final Object body;

        Response(HttpStatus status, Object body) {
            this.status = status;
            this.body = body;
        }
        public HttpStatus getStatus() {
            return status;
        }

        public Object getBody() {
            return body;
        }
    }
    @GetMapping
    public ResponseEntity<Response> readAllUsers(@RequestHeader("role") String role){
        System.out.println(role);
        if(role == null || !role.equalsIgnoreCase("admin")){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).
                    body(new Response(HttpStatus.FORBIDDEN, "User does not have permission to access this resource."));
        }
        List<Users> userList = usersService.readAll();
        return ResponseEntity.ok(new Response(HttpStatus.OK, userList));
    }

    @PostMapping
    public Users createUser(@RequestBody Users users){
        return usersService.create(users);
    }

    @GetMapping("/details")
    public ResponseEntity<Response> readOneUser(@RequestHeader("role") String role , @RequestHeader("loggedInUserId") String userId){
        if(role == null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new Response(HttpStatus.FORBIDDEN, "cannot see users details."));
        }
        UsersResponseDto user = usersService.readOne(userId);
        return ResponseEntity.ok(new Response(HttpStatus.OK, user));
//        return usersService.readOne(userId);
    }

    @PutMapping("/update")
    public Users updateUser(@RequestBody Users users, @RequestHeader("loggedInUserId") String userId){
        return usersService.update(users, userId);
    }

    //for admin
    @DeleteMapping("/delete")
    public void deleteUser(@PathVariable Long id){
        usersService.delete(id);
    }
}
