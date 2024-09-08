package com.aurionpro.bankRest.utils;

import com.aurionpro.bankRest.dto.BankAccountDto;
import com.aurionpro.bankRest.dto.CustomerDto;
import com.aurionpro.bankRest.dto.LoginDto;
import com.aurionpro.bankRest.dto.TransactionDto;
import com.aurionpro.bankRest.entity.BankAccount;
import com.aurionpro.bankRest.entity.Customer;
import com.aurionpro.bankRest.entity.Transaction;
import com.aurionpro.bankRest.entity.User;

public class EntityToDtoConverter {

	public static BankAccountDto toBankAccountDto(BankAccount bankAccount) {
		BankAccountDto bankAccountDto = new BankAccountDto();
		bankAccountDto.setAccountNumber(bankAccount.getAccountNumber());
		bankAccountDto.setAccountType(bankAccount.getAccountType());
		bankAccountDto.setBalance(bankAccount.getBalance());
		bankAccountDto.setMinOrOverdueLimit(bankAccount.getMinOrOverdueLimit());
		return bankAccountDto;
	}
	
	public static CustomerDto toCustomerDto(Customer customer) {
		CustomerDto customerDto = new CustomerDto();
		customerDto.setCustomerId(customer.getCustomerId());
		customerDto.setFirstName(customer.getFirstName());
		customerDto.setLastName(customer.getLastName());
		customerDto.setEmail(customer.getEmail());
		return customerDto;
	}
	
	public static LoginDto toLoginDto(User user) {
		LoginDto loginDto = new LoginDto();
		loginDto.setUsername(user.getUsername());
		loginDto.setPassword(user.getPassword());
		return loginDto;
	}
	
	public static TransactionDto toTransactionDto(Transaction transaction) {
		TransactionDto transactionDto = new TransactionDto();
		transactionDto.setTransactionId(transaction.getTransactionId());
		transactionDto.setSenderAccount(transaction.getSenderAccount()==null?null:transaction.getSenderAccount().getAccountNumber());
		transactionDto.setReceiverAccount(transaction.getReceiverAccount()==null?null:transaction.getReceiverAccount().getAccountNumber());
		transactionDto.setAmount(transaction.getAmount());
		transactionDto.setTransactionType(transaction.getTransactionType());
		transactionDto.setDateTime(transaction.getDateTime());
		return transactionDto;
	}
}
