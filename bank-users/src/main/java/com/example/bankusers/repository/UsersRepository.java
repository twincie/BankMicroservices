package com.example.bankusers.repository;

import com.example.bankusers.entity.Role;
import com.example.bankusers.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
     Optional<Users> findByUsername(String username);

     Users findByRole(Role role);

    //Optional<Users> findByEmail(String email);
}
