package com.aurionpro.bankRest.entity;

import com.aurionpro.bankRest.entity.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Date;
import java.time.Instant;

@Entity
@Table(name="transactions")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long transactionId;
	
	@ManyToOne
	@JoinColumn(name = "senderAccountNumber")
	private BankAccount senderAccount;
	
	@ManyToOne
	@JoinColumn(name = "receiverAccountNumber")
	private BankAccount receiverAccount;
	
	@NotNull
	@Column
	private TransactionType transactionType;
	
	@NotNull
	@Min(1)
	@Column
	private Double amount;
	
	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private Instant dateTime;
	
}
