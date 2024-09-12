package com.aurionpro.bankRest.service;

import com.aurionpro.bankRest.dto.*;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminServices.class);

    @Transactional
    public CustomerDto addCustomerToUserId(AddCustomerDto addCustomerDto) {
        LOGGER.info("Adding customer to user with ID: {}", addCustomerDto.getUserId());

        // Get Login
        User user = userRepository.findById(addCustomerDto.getUserId())
                .orElseThrow(() -> {
                    LOGGER.error("Invalid login ID: {}", addCustomerDto.getUserId());
                    return new UserApiException(HttpStatus.NOT_FOUND, "Invalid login Id");
                });

        // Convert AddCustomerDto to Customer entity and set the saved login
        Customer customer = DtoToEntityConverter.toCustomerEntity(addCustomerDto);
        customer.setUser(user);

        Customer savedCustomer = customerRepository.save(customer);
        CustomerDto customerDto = EntityToDtoConverter.toCustomerDto(savedCustomer);

        LOGGER.info("Customer added successfully with ID: {}", savedCustomer.getCustomerId());

        return customerDto;
    }

    @Transactional
    public BankAccountDto addBankAccountToCustomer(AddBankAccountDto addBankAccountDto) {
        LOGGER.info("Adding bank account to customer with ID: {}", addBankAccountDto.getCustomerId());

        // Find the customer by ID
        Customer customer = customerRepository.findById(addBankAccountDto.getCustomerId())
                .orElseThrow(() -> {
                    LOGGER.error("Customer not found with ID: {}", addBankAccountDto.getCustomerId());
                    return new UserApiException(HttpStatus.NOT_FOUND,"Customer not found");
                });
        if(!customer.isActive())
            throw new UserApiException(HttpStatus.FORBIDDEN,"Customer is inactive");

        // Convert AddBankAccountDto to BankAccount entity
        BankAccount bankAccount = DtoToEntityConverter.toBankAccountEntity(addBankAccountDto);
        bankAccount.setCustomer(customer);

        // Save bank account entity
        BankAccount savedBankAccount = bankAccountRepository.save(bankAccount);
        BankAccountDto bankAccountDto = EntityToDtoConverter.toBankAccountDto(savedBankAccount);

        LOGGER.info("Bank account added successfully with account number: {}", savedBankAccount.getAccountNumber());

        return bankAccountDto;
    }

    public PageResponse<CustomerDto> getAllCustomers(int page, int size,boolean inactive) {
        LOGGER.info("Fetching all customers with pagination - Page: {}, Size: {}", page, size);

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Customer> customerPage = customerRepository.findAll(pageRequest);

        List<CustomerDto> content = customerPage.getContent().stream()
                .map(EntityToDtoConverter::toCustomerDto)
                .collect(Collectors.toList());

        if(!inactive){
            LOGGER.info("Filtering inactive customers");
            content = content.stream().filter(CustomerDto::isActive).toList();
        }

        PageResponse<CustomerDto> response = new PageResponse<>(
                customerPage.getTotalPages(),
                customerPage.getSize(),
                customerPage.getTotalElements(),
                content,
                customerPage.isLast()
        );

        LOGGER.info("Fetched {} customers successfully", content.size());

        return response;
    }

    public PageResponse<TransactionDto> getAllTransactions(int pageNo, int pageSize) {
        LOGGER.info("Fetching all transactions with pagination - Page: {}, Size: {}", pageNo, pageSize);

        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<Transaction> transactionPage = transactionRepository.findAll(pageRequest);

        List<TransactionDto> content = transactionPage.getContent().stream()
                .map(EntityToDtoConverter::toTransactionDto)
                .toList();

        PageResponse<TransactionDto> response = new PageResponse<>(
                transactionPage.getTotalPages(),
                transactionPage.getSize(),
                transactionPage.getTotalElements(),
                content,
                transactionPage.isLast()
        );

        LOGGER.info("Fetched {} transactions successfully", content.size());

        return response;
    }

    public PageResponse<BankAccountDto> getAllBankAccount(int pageNo, int pageSize, boolean inactive) {
        LOGGER.info("Fetching all bank accounts with pagination - Page: {}, Size: {}", pageNo, pageSize);

        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<BankAccount> bankAccountPage = bankAccountRepository.findAll(pageRequest);

        List<BankAccountDto> content = bankAccountPage.getContent().stream()
                .map(EntityToDtoConverter::toBankAccountDto)
                .toList();

        if(!inactive){
            LOGGER.info("Filtering inactive bank accounts");
            content = content.stream().filter(BankAccountDto::isActive).toList();
        }


        PageResponse<BankAccountDto> response = new PageResponse<>(
                bankAccountPage.getTotalPages(),
                bankAccountPage.getSize(),
                bankAccountPage.getTotalElements(),
                content,
                bankAccountPage.isLast()
        );

        LOGGER.info("Fetched {} bank accounts successfully", content.size());

        return response;
    }


    @Transactional
    public CustomerDto softDeleteCustomer(int customerId){
        LOGGER.info("Fetching customer with customer Id : {}", customerId);
        Customer customer = customerRepository.findById(customerId).orElseThrow(()->{
            LOGGER.error("Invalid customer Id: {}, customer not found",customerId);
            return new UserApiException(HttpStatus.NOT_FOUND, "Customer Not Found");
        });


        List<BankAccount> bankAccountList = customer.getBankAccount();
        LOGGER.info("Fetched {} bank accounts of customer id: {}", bankAccountList.size(), customerId);

        // Using a batch update instead of deleting each account individually
        bankAccountRepository.updateBankAccountsAsInactive(bankAccountList);
        LOGGER.info("Soft deleted {} bank accounts for customer id: {}", bankAccountList.size(), customerId);



        customer.setActive(false);
        CustomerDto customerDto = EntityToDtoConverter.toCustomerDto(customerRepository.save(customer));
        LOGGER.info("Soft deleted customer with customer id:{}",customerId);
        return customerDto;
    }

    @Transactional
    public BankAccountDto softDeleteBankAccount(Long accountNumber) {
        LOGGER.info("Fetching bank account with account number : {}", accountNumber);
        BankAccount bankAccount = bankAccountRepository.findById(accountNumber).orElseThrow(()->{
            LOGGER.error("Invalid account number: {}, bank account not found",accountNumber);
            return new UserApiException(HttpStatus.NOT_FOUND, "Bank account Not Found");
        });
        bankAccount.setActive(false);
        BankAccountDto bankAccountDto = EntityToDtoConverter.toBankAccountDto(bankAccountRepository.save(bankAccount));
        LOGGER.info("Soft deleted bank account with account number:{}",accountNumber);
        return bankAccountDto;
    }
}
