package com.example.bankusers.service.impl;

import com.example.bankusers.entity.Role;
import com.example.bankusers.entity.Users;
import com.example.bankusers.external.Wallet;
import com.example.bankusers.repository.UsersRepository;
import com.example.bankusers.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private UsersRepository usersRepository;

    //    =================================== CRUD START =================================
    public Users create(Users user){
        return usersRepository.save(user);
    }

    @Override
    public ResponseEntity<Users> readOne(Long id) {
        return usersRepository.findById();
    }

    @Override
    public List<Users> readAll() {
        return null;
    }

    @Override
    public Users update(Long id, Users updater) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }


//
//    public ResponseEntity<Users> readOne(Long id){
//        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
//        Optional<Users> usersOptional = usersRepository.findById(id);
//        if (usersOptional.isPresent() || isAdmin()){
//            Users users = usersOptional.get();
//            if (!users.getUsername().equals(currentUser)) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//            }
//            return ResponseEntity.ok(users);
//        }
//        return ResponseEntity.notFound().build();
//    }
//
//    public List<Users> readAll(){
//        return usersRepository.findAll();
//    }
//    public Users update(Long id, Users updater){
//        Users existingUser = usersRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("User Not Found"));
//
//        if(usersRepository.existsById(id)){
//            existingUser.setEmail(updater.getEmail());
//            existingUser.setUsername(updater.getUsername());
//            return usersRepository.save(existingUser);
//        } else
//            return null;
//    }
//    public void delete(Long id){
//        usersRepository.deleteById(id);
//    }
//
//    //=================================== CRUD END =================================
//
//    public boolean isAdmin(){
//        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
//        Optional<Users> users = usersRepository.findByUsername(currentUser);
//        if (users.isPresent()){
//            Users user = users.get();
//            return user.getRole() == Role.ADMIN;
//        }
//        return false;
//    }
}
