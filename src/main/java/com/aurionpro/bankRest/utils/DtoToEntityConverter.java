package com.aurionpro.bankRest.utils;

import com.aurionpro.bankRest.dto.BankAccountDto;
import com.aurionpro.bankRest.dto.CustomerDto;
import com.aurionpro.bankRest.dto.LoginDto;
import com.aurionpro.bankRest.dto.TransactionDto;
import com.aurionpro.bankRest.entity.BankAccount;
import com.aurionpro.bankRest.entity.Customer;
import com.aurionpro.bankRest.entity.Transaction;
import com.aurionpro.bankRest.entity.User;

public class DtoToEntityConverter {

    public static Customer toCustomerEntity(CustomerDto customerDto) {
        Customer customer = new Customer();
        customer.setCustomerId(customerDto.getCustomerId());
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setEmail(customerDto.getEmail());
        return customer;
    }

    public static BankAccount toBankAccountEntity(BankAccountDto bankAccountDto) {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber(bankAccountDto.getAccountNumber());
        bankAccount.setAccountType(bankAccountDto.getAccountType());
        bankAccount.setBalance(bankAccountDto.getBalance());
        bankAccount.setMinOrOverdueLimit(bankAccountDto.getMinOrOverdueLimit());
        return bankAccount;
    }

    public static User toLoginEntity(LoginDto loginDto) {
        User user = new User();
        user.setUsername(loginDto.getUsername());
        user.setPassword(loginDto.getPassword());
        return user;
    }

    public static Transaction toTransactionEntity(TransactionDto transactionDto) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(transactionDto.getTransactionId());
        transaction.setAmount(transactionDto.getAmount());
        transaction.setTransactionType(transactionDto.getTransactionType());
        transaction.setDate(transactionDto.getDate());
        // TODO: You will need to set senderAccount and receiverAccount separately
        return transaction;
    }
}
