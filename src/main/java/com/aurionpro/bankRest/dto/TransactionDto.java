package com.aurionpro.bankRest.dto;

import com.aurionpro.bankRest.entity.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransactionDto {
	private Long transactionId;
	private Long senderAccount;
	private Long receiverAccount;
	private TransactionType transactionType;
	private Double amount;
	private Instant dateTime;
}
