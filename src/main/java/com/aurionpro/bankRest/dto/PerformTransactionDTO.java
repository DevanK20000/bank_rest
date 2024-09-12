package com.aurionpro.bankRest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PerformTransactionDTO {
    @NotNull
    private Long accountNumber;

    @NotNull
    @Min(0)
    private Double amount;

    private Long receiverAccountNumber;
}
