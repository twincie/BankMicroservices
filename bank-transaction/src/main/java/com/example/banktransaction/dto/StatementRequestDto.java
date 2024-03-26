package com.example.banktransaction.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StatementRequestDto {
    private LocalDate startDate;
    private LocalDate stopDate;
}
