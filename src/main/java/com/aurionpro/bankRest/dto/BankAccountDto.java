package com.aurionpro.bankRest.dto;

import com.aurionpro.bankRest.entity.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BankAccountDto {
	private Long accountNumber;
	private AccountType accountType;
	private Double balance;
	private Double minOrOverdueLimit;
}
