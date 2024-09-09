package com.aurionpro.bankRest.service;

import com.aurionpro.bankRest.dto.*;
import com.aurionpro.bankRest.entity.BankAccount;
import com.aurionpro.bankRest.entity.Customer;
import com.aurionpro.bankRest.entity.Transaction;
import com.aurionpro.bankRest.entity.User;
import com.aurionpro.bankRest.entity.enums.AccountType;
import com.aurionpro.bankRest.entity.enums.TransactionType;
import com.aurionpro.bankRest.exception.UserApiException;
import com.aurionpro.bankRest.repository.BankAccountRepository;
import com.aurionpro.bankRest.repository.CustomerRepository;
import com.aurionpro.bankRest.repository.TransactionRepository;
import com.aurionpro.bankRest.repository.UserRepository;
import com.aurionpro.bankRest.utils.EntityToDtoConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;


    public CustomerDto getCustomerById() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        LOGGER.info("Fetching customer details for username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    LOGGER.error("Invalid login: username '{}' not found", username);
                    return new UserApiException(HttpStatus.BAD_REQUEST, "Invalid login");
                });

        Optional<Customer> customer = customerRepository.findByUser(user);
        return customer.map(EntityToDtoConverter::toCustomerDto)
                .orElseThrow(() -> {
                    LOGGER.error("Customer not found for user '{}'", username);
                    return new UserApiException(HttpStatus.NOT_FOUND, "Customer Not Found");
                });
    }

    public List<BankAccountDto> getCustomerBankAccounts() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        LOGGER.info("Fetching bank accounts for username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    LOGGER.error("Invalid login: username '{}' not found", username);
                    return new UserApiException(HttpStatus.BAD_REQUEST, "Invalid login");
                });

        Optional<Customer> customer = customerRepository.findByUser(user);
        return customer.map(value -> value.getBankAccount().stream()
                        .map(EntityToDtoConverter::toBankAccountDto)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> {
                    LOGGER.error("Customer not found for user '{}'", username);
                    return new UserApiException(HttpStatus.NOT_FOUND, "Customer Not Found");
                });
    }

    @Transactional
    public TransactionDto credit(PerformTransactionDTO performTransactionDTO) {
        Long accountNumber = performTransactionDTO.getAccountNumber();
        Double amount = performTransactionDTO.getAmount();

        LOGGER.info("Initiating credit of amount {} to account number {}", amount, accountNumber);

        if (amount <= 0) {
            LOGGER.error("Invalid credit amount: {}", amount);
            throw new UserApiException(HttpStatus.BAD_REQUEST, "Amount should be greater than zero");
        }

        BankAccount bankAccount = getBankAccount(accountNumber);

        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccountRepository.save(bankAccount);
        LOGGER.info("Credited amount {} to account number {}", amount, accountNumber);

        Transaction transaction = new Transaction();
        transaction.setReceiverAccount(bankAccount);
        transaction.setTransactionType(TransactionType.credit);
        transaction.setAmount(amount);
        transaction.setSenderAccount(null); // Sender account is null for credit transactions

        Transaction savedTransaction = transactionRepository.save(transaction);
        LOGGER.info("Transaction completed and saved for credit of amount {} to account number {}", amount, accountNumber);

        return EntityToDtoConverter.toTransactionDto(savedTransaction);
    }

    @Transactional
    public TransactionDto debit(PerformTransactionDTO performTransactionDTO) {
        Long accountNumber = performTransactionDTO.getAccountNumber();
        Double amount = performTransactionDTO.getAmount();

        LOGGER.info("Initiating debit of amount {} from account number {}", amount, accountNumber);

        if (amount <= 0) {
            LOGGER.error("Invalid debit amount: {}", amount);
            throw new UserApiException(HttpStatus.BAD_REQUEST, "Amount should be greater than zero");
        }

        BankAccount bankAccount = getBankAccount(accountNumber);

        AccountType accountType = bankAccount.getAccountType();
        double minOrOverdueLimit = bankAccount.getMinOrOverdueLimit();

        if (accountType.equals(AccountType.saving)) {
            if (bankAccount.getBalance() - amount < minOrOverdueLimit) {
                LOGGER.error("Debit operation failed: Insufficient balance in saving account number {}", accountNumber);
                throw new UserApiException(HttpStatus.BAD_REQUEST, "Cannot debit below minimum account balance for salary account");
            }
        } else if (accountType.equals(AccountType.current)) {
            if (bankAccount.getBalance() - amount < -minOrOverdueLimit) {
                LOGGER.error("Debit operation failed: Overdraft limit exceeded for current account number {}", accountNumber);
                throw new UserApiException(HttpStatus.BAD_REQUEST, "Cannot exceed overdraft limit for current account");
            }
        }

        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount);
        LOGGER.info("Debited amount {} from account number {}", amount, accountNumber);

        Transaction transaction = new Transaction();
        transaction.setSenderAccount(bankAccount);
        transaction.setTransactionType(TransactionType.debit);
        transaction.setAmount(amount);
        transaction.setReceiverAccount(null); // Receiver account is null for debit transactions

        Transaction savedTransaction = transactionRepository.save(transaction);
        LOGGER.info("Transaction completed and saved for debit of amount {} from account number {}", amount, accountNumber);

        return EntityToDtoConverter.toTransactionDto(savedTransaction);
    }

    public PageResponse<TransactionDto> getCustomerTransactions(Long accountNumber, int pageNo, int pageSize) {
        LOGGER.info("Fetching transactions for account number {}", accountNumber);

        BankAccount bankAccount = getBankAccount(accountNumber);
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        List<TransactionDto> transactionDtos = Stream.concat(bankAccount.getSentTransactions().stream(), bankAccount.getReceivedTransactions().stream())
                .map(EntityToDtoConverter::toTransactionDto)
                .sorted((o1, o2) -> o2.getDateTime().compareTo(o1.getDateTime()))
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), transactionDtos.size());
        List<TransactionDto> subList = transactionDtos.subList(start, end);

        Page<TransactionDto> transactionDtoPage = new PageImpl<>(subList, pageable, transactionDtos.size());

        LOGGER.info("Fetched {} transactions for account number {}", transactionDtoPage.getContent().size(), accountNumber);

        return new PageResponse<>(
                transactionDtoPage.getTotalPages(),
                transactionDtoPage.getSize(),
                transactionDtoPage.getTotalElements(),
                transactionDtoPage.getContent(),
                transactionDtoPage.isLast()
        );
    }

    private BankAccount getBankAccount(Long accountNumber) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        LOGGER.info("Fetching bank account details for account number {} and username {}", accountNumber, username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    LOGGER.error("Invalid login: username '{}' not found", username);
                    return new UserApiException(HttpStatus.BAD_REQUEST, "Invalid login");
                });

        Customer customer = customerRepository.findByUser(user)
                .orElseThrow(() -> {
                    LOGGER.error("Customer not found for username '{}'", username);
                    return new UserApiException(HttpStatus.NOT_FOUND, "Customer Not Found");
                });

        List<BankAccount> bankAccounts = bankAccountRepository.findByCustomer(customer)
                .orElseThrow(() -> {
                    LOGGER.error("No bank accounts found for customer '{}'", customer.getCustomerId());
                    return new UserApiException(HttpStatus.BAD_REQUEST, "No bank accounts");
                });

        return bankAccounts.stream()
                .filter(account -> account.getAccountNumber().equals(accountNumber))
                .findFirst()
                .orElseThrow(() -> {
                    LOGGER.error("Bank account '{}' not found for customer '{}'", accountNumber, customer.getCustomerId());
                    return new UserApiException(HttpStatus.NOT_FOUND, "Account number not found");
                });
    }

    @Transactional
    public TransactionDto transfer(PerformTransactionDTO performTransactionDTO) {
        Long senderAccountNumber = performTransactionDTO.getAccountNumber();
        Double amount = performTransactionDTO.getAmount();
        Long receiverAccountNumber = performTransactionDTO.getReceiverAccountNumber();

        LOGGER.info("Initiating transfer of amount {} from account number {} to account number {}", amount, senderAccountNumber, receiverAccountNumber);

        if (senderAccountNumber.equals(receiverAccountNumber)) {
            LOGGER.error("Transfer operation failed: Cannot transfer to the same account number {}", senderAccountNumber);
            throw new UserApiException(HttpStatus.BAD_REQUEST, "Can't self transfer");
        }

        debit(performTransactionDTO);

        PerformTransactionDTO creditPerformTransactionDTO = new PerformTransactionDTO(receiverAccountNumber, amount, null);
        credit(creditPerformTransactionDTO);

        Transaction transaction = new Transaction();
        transaction.setSenderAccount(bankAccountRepository.findById(senderAccountNumber).orElse(null));
        transaction.setReceiverAccount(bankAccountRepository.findById(receiverAccountNumber).orElse(null));
        transaction.setTransactionType(TransactionType.transfer);
        transaction.setAmount(amount);

        Transaction savedTransaction = transactionRepository.save(transaction);
        LOGGER.info("Transfer completed successfully: amount {} from account number {} to account number {}", amount, senderAccountNumber, receiverAccountNumber);

        return EntityToDtoConverter.toTransactionDto(savedTransaction);
    }
}
