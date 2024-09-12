package com.aurionpro.bankRest.utils;

import com.aurionpro.bankRest.entity.BankAccount;
import com.aurionpro.bankRest.entity.Customer;
import com.aurionpro.bankRest.entity.User;
import com.aurionpro.bankRest.exception.UserApiException;
import com.aurionpro.bankRest.repository.BankAccountRepository;
import com.aurionpro.bankRest.repository.CustomerRepository;
import com.aurionpro.bankRest.service.AdminServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

public class EntityIsActiveChecker {



    private static final Logger LOGGER = LoggerFactory.getLogger(EntityIsActiveChecker.class);

    // Method to check if Bank Account is active
    public static void checkIfAccountIsActive(BankAccount bankAccount) {
        if (!bankAccount.isActive()) {
            LOGGER.error("Bank account with ID {} is inactive", bankAccount.getAccountNumber());
            throw new UserApiException(HttpStatus.FORBIDDEN, "Bank account is inactive");
        }
    }

    // Method to check if Customer is active
    public static void checkIfCustomerIsActive(Customer customer) {
        if (!customer.isActive()) {
            LOGGER.error("Customer with ID {} is inactive", customer.getCustomerId());
            throw new UserApiException(HttpStatus.FORBIDDEN, "Customer is inactive");
        }
    }

}
