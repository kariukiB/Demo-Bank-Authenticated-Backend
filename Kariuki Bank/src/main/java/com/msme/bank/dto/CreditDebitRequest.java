package com.msme.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditDebitRequest {
    private String AccountNumber;
    private BigDecimal amount;
}
