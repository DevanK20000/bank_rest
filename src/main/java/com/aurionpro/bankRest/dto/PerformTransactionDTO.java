package com.aurionpro.bankRest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PerformTransactionDTO {
    private Long accountNumber;
    private Double amount;
    private Long receiverAccountNumber;
}
