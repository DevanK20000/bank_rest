package com.aurionpro.bankRest.entity;

import com.aurionpro.bankRest.entity.enums.AccountType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "BankAccounts")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BankAccount {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long accountNumber;
	
	@NotNull
	@Column
	private AccountType accountType;
	
	@NotNull
	@Column
	private Double balance;
	
	@NotNull
	@Min(0)
	private Double minOrOverdueLimit;

	@NotNull
	private boolean active=true;
	
	@ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REFRESH,CascadeType.DETACH})
	@JoinColumn(name="customerId")
	private Customer customer;
	
	@OneToMany(mappedBy = "senderAccount", cascade = CascadeType.ALL)
	private List<Transaction> sentTransactions;
	
	@OneToMany(mappedBy = "receiverAccount", cascade = CascadeType.ALL)
	private List<Transaction> receivedTransactions;
	
}
