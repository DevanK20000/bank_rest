package com.aurionpro.bankRest.service;

import com.aurionpro.bankRest.dto.BankAccountDto;
import com.aurionpro.bankRest.dto.CustomerDto;
import com.aurionpro.bankRest.dto.PageResponse;
import com.aurionpro.bankRest.dto.TransactionDto;
import com.aurionpro.bankRest.entity.BankAccount;
import com.aurionpro.bankRest.entity.Customer;
import com.aurionpro.bankRest.entity.Transaction;
import com.aurionpro.bankRest.entity.User;
import com.aurionpro.bankRest.exception.UserApiException;
import com.aurionpro.bankRest.repository.BankAccountRepository;
import com.aurionpro.bankRest.repository.CustomerRepository;
import com.aurionpro.bankRest.repository.TransactionRepository;
import com.aurionpro.bankRest.repository.UserRepository;
import com.aurionpro.bankRest.utils.DtoToEntityConverter;
import com.aurionpro.bankRest.utils.EntityToDtoConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServices {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public CustomerDto addCustomerToUserId(Integer userId,CustomerDto customerDto) {
        //Get Login
        User user = userRepository.findById(userId).orElseThrow(()-> new UserApiException(HttpStatus.NOT_FOUND,"Invalid login Id"));

        // Convert CustomerDto to Customer entity and set the saved login
        Customer customer = DtoToEntityConverter.toCustomerEntity(customerDto);
        customer.setUser(user);

        // Convert saved customer entity to DTO and return
        return EntityToDtoConverter.toCustomerDto(customerRepository.save(customer));
    }

    @Transactional
    public BankAccountDto addBankAccountToCustomer(int customerId, BankAccountDto bankAccountDto) {
        // Find the customer by ID
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Convert BankAccountDto to BankAccount entity
        BankAccount bankAccount = DtoToEntityConverter.toBankAccountEntity(bankAccountDto);
        bankAccount.setCustomer(customer);

        // Save bank account entity
        BankAccount savedBankAccount = bankAccountRepository.save(bankAccount);

        // Convert saved bank account entity to DTO and return
        return EntityToDtoConverter.toBankAccountDto(savedBankAccount);
    }

    public PageResponse<CustomerDto> getAllCustomers(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Customer> customerPage = customerRepository.findAll(pageRequest);

        List<CustomerDto> content = customerPage.getContent().stream()
                .map(EntityToDtoConverter::toCustomerDto)
                .collect(Collectors.toList());

        return new PageResponse<>(
                customerPage.getTotalPages(),
                customerPage.getSize(),
                customerPage.getTotalElements(),
                content,
                customerPage.isLast()
        );
    }

    public PageResponse<TransactionDto> getAllTransactions(int pageNo, int pageSize){
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<Transaction> transactionPage = transactionRepository.findAll(pageRequest);

        List<TransactionDto> content = transactionPage.getContent().stream()
                .map(EntityToDtoConverter::toTransactionDto)
                .toList();

        return new PageResponse<>(
                    transactionPage.getTotalPages(),
                    transactionPage.getSize(),
                    transactionPage.getTotalElements(),
                    content,
                    transactionPage.isLast()
                );
    }

    public PageResponse<BankAccountDto> getAllBankAccount(int pageNo, int pageSize){
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<BankAccount> bankAccountPage = bankAccountRepository.findAll(pageRequest);

        List<BankAccountDto> content = bankAccountPage.getContent().stream()
                .map(EntityToDtoConverter::toBankAccountDto)
                .toList();

        return new PageResponse<>(
                bankAccountPage.getTotalPages(),
                bankAccountPage.getSize(),
                bankAccountPage.getTotalElements(),
                content,
                bankAccountPage.isLast()
        );
    }

}
