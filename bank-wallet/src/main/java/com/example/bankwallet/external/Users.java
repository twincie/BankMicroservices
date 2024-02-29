package com.example.bankwallet.external;

import lombok.Data;

@Data
public class Users {
    private Long id;
    private String username;
    private String email;
    private String password;
    private Role role;
}
