package com.example.bankusers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DefaultApiResponse {
    private String status;
    private  String message;
    private Object data;
}
