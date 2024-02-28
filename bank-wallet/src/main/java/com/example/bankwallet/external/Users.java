package com.example.bankwallet.external;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class Users {
    private Long id;
    private String username;
    private String email;
    private String password;
    private Role role;
}
