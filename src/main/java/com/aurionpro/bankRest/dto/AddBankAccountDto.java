package com.aurionpro.bankRest.dto;

import com.aurionpro.bankRest.entity.enums.AccountType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddBankAccountDto {
    @NotNull
    @NotBlank
    private int customerId;

    @NotNull
    @NotBlank
    private Long accountNumber;

    @NotNull
    private AccountType accountType;

    @NotNull
    @NotBlank
    @Min(0)
    private Double balance;

    @NotNull
    @NotBlank
    @Min(0)
    private Double minOrOverdueLimit;
}
