package com.example.bankwallet.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Response {
    private HttpStatus status;
    private  String message;
    private Object body;
}
