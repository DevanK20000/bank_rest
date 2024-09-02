package com.aurionpro.bankRest.service;

import com.aurionpro.bankRest.dto.BankAccountDto;
import com.aurionpro.bankRest.dto.CustomerDto;
import com.aurionpro.bankRest.dto.PageResponse;
import com.aurionpro.bankRest.dto.TransactionDto;
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
        User user = userRepository.findByUsername(username).orElseThrow(()-> new UserApiException(HttpStatus.BAD_REQUEST,"Invalid login"));
        Optional<Customer> customer = customerRepository.findByUser(user);
        return customer.map(EntityToDtoConverter::toCustomerDto)
                .orElseThrow(()-> new UserApiException(HttpStatus.NOT_FOUND,"Customer Not Found"));
    }

    public List<BankAccountDto> getCustomerBankAccounts() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(()-> new UserApiException(HttpStatus.BAD_REQUEST,"Invalid login"));
        Optional<Customer> customer = customerRepository.findByUser(user);
        return customer.map(value -> value.getBankAccount().stream()
                .map(EntityToDtoConverter::toBankAccountDto)
                .collect(Collectors.toList()))
                .orElseThrow(()-> new UserApiException(HttpStatus.NOT_FOUND,"Customer Not Found"));
    }

    @Transactional
    public TransactionDto credit(Long accountNumber, Double amount) {
        if(amount<=0)
            throw new UserApiException(HttpStatus.BAD_REQUEST,"Amount should be greater than zero");

        BankAccount bankAccount = getBankAccount(accountNumber);

        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccountRepository.save(bankAccount);

        Transaction transaction = new Transaction();
        transaction.setReceiverAccount(bankAccount);
        transaction.setTransactionType(TransactionType.credit);
        transaction.setAmount(amount);
        transaction.setSenderAccount(null); // Sender account is null for credit transactions

        Transaction savedTransaction = transactionRepository.save(transaction);
        return EntityToDtoConverter.toTransactionDto(savedTransaction);
    }

    @Transactional
    public TransactionDto debit(Long accountNumber, Double amount) {
        if(amount<=0)
            throw new UserApiException(HttpStatus.BAD_REQUEST,"Amount should be greater than zero");

        BankAccount bankAccount = getBankAccount(accountNumber);

        AccountType accountType = bankAccount.getAccountType();
            double minOrOverdueLimit = bankAccount.getMinOrOverdueLimit();

            if (accountType.equals(AccountType.saving)) {
                if (bankAccount.getBalance() - amount < minOrOverdueLimit) {
                    throw new UserApiException(HttpStatus.BAD_REQUEST, "Cannot debit below minimum account balance for salary account");
                }
            } else if (accountType.equals(AccountType.current)) {
                if (bankAccount.getBalance() - amount < -minOrOverdueLimit) {
                    throw new UserApiException(HttpStatus.BAD_REQUEST,"Cannot exceed overdraft limit for current account");
                }
            }

            bankAccount.setBalance(bankAccount.getBalance() - amount);
            bankAccountRepository.save(bankAccount);

            Transaction transaction = new Transaction();
            transaction.setSenderAccount(bankAccount);
            transaction.setTransactionType(TransactionType.debit);
            transaction.setAmount(amount);
            transaction.setReceiverAccount(null); // Receiver account is null for debit transactions

            Transaction savedTransaction = transactionRepository.save(transaction);
            return EntityToDtoConverter.toTransactionDto(savedTransaction);
    }

    public PageResponse<TransactionDto> getCustomerTransactions(Long accountNumber, int pageNo, int pageSize){
        BankAccount bankAccount = getBankAccount(accountNumber);
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        List<TransactionDto> transactionDtos = Stream.concat(bankAccount.getSentTransactions().stream(),bankAccount.getReceivedTransactions().stream())
                .map(EntityToDtoConverter::toTransactionDto)
                .sorted((o1, o2) -> o2.getDate().compareTo(o1.getDate()))
                .toList();

        // Calculate start and end indices for sublist
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), transactionDtos.size());

        // Create a sublist based on pagination
        List<TransactionDto> subList = transactionDtos.subList(start, end);

        // Wrap the sublist in a PageImpl object
         Page<TransactionDto> transactionDtoPage= new PageImpl<>(subList, pageable, transactionDtos.size());
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
        User user = userRepository.findByUsername(username).orElseThrow(()-> new UserApiException(HttpStatus.BAD_REQUEST,"Invalid login"));
        Customer customer = customerRepository.findByUser(user).orElseThrow(()-> new UserApiException(HttpStatus.NOT_FOUND,"Customer Not Found"));
        List<BankAccount> bankAccounts = bankAccountRepository.findByCustomer(customer).orElseThrow(()-> new UserApiException(HttpStatus.BAD_REQUEST,"No bank accounts"));
        return  bankAccounts.stream().filter(account -> account.getAccountNumber().equals(accountNumber)).toList().get(0);
    }

    @Transactional
    public TransactionDto transfer(Long senderAccountNumber, Long receiverAccountNumber, Double amount) {
        // Perform debit operation on sender's account
        debit(senderAccountNumber, amount);

        // Perform credit operation on receiver's account
        credit(receiverAccountNumber, amount);

        // Create a new transaction to log the transfer
        Transaction transaction = new Transaction();
        transaction.setSenderAccount(bankAccountRepository.findById(senderAccountNumber).orElse(null));
        transaction.setReceiverAccount(bankAccountRepository.findById(receiverAccountNumber).orElse(null));
        transaction.setTransactionType(TransactionType.transfer);
        transaction.setAmount(amount);

        Transaction savedTransaction = transactionRepository.save(transaction);
        return EntityToDtoConverter.toTransactionDto(savedTransaction);
    }


}
