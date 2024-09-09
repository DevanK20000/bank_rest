package com.aurionpro.bankRest.utils;

import com.aurionpro.bankRest.dto.*;
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
        customer.setActive(customer.isActive());
        return customer;
    }

    public static Customer toCustomerEntity(AddCustomerDto addCustomerDto){
        Customer customer = new Customer();
        customer.setFirstName(addCustomerDto.getFirstName());
        customer.setLastName(addCustomerDto.getLastName());
        customer.setEmail(addCustomerDto.getEmail());
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

    public static BankAccount toBankAccountEntity(AddBankAccountDto addBankAccountDto) {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountType(addBankAccountDto.getAccountType());
        bankAccount.setBalance(addBankAccountDto.getBalance());
        bankAccount.setMinOrOverdueLimit(addBankAccountDto.getMinOrOverdueLimit());
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
        transaction.setDateTime(transactionDto.getDateTime());
        // TODO: You will need to set senderAccount and receiverAccount separately
        return transaction;
    }
}
