package com.example.bankwallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DefaultApiResponse {
    private String status;
    private  String message;
    private Object data;
}
