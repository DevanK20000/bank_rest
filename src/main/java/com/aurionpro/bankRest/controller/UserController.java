package com.aurionpro.bankRest.controller;

import com.aurionpro.bankRest.dto.*;
import com.aurionpro.bankRest.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/")
    public CustomerDto getCustomerById() {
        return userService.getCustomerById();
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/bankAccounts")
    public ResponseEntity<List<BankAccountDto>> getCustomerBankAccounts() {
        return new ResponseEntity<>(userService.getCustomerBankAccounts(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/transactions/{accountNumber}")
    public ResponseEntity<PageResponse<TransactionDto>> getCustomerTransactions(@PathVariable Long accountNumber, @RequestParam int pageNo, @RequestParam int pageSize) {
        return new ResponseEntity<>(userService.getCustomerTransactions(accountNumber,pageNo,pageSize),HttpStatus.OK);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/credit")
    public ResponseEntity<TransactionDto> credit(@Valid @RequestBody PerformTransactionDTO performTransactionDTO) {
        return new ResponseEntity<>(userService.credit(performTransactionDTO),HttpStatus.OK);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/debit")
    public ResponseEntity<TransactionDto> debit(@Valid @RequestBody PerformTransactionDTO performTransactionDTO) {
        return new ResponseEntity<>(userService.debit(performTransactionDTO),HttpStatus.OK);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/transfer")
    public ResponseEntity<TransactionDto> transfer(@Valid @RequestBody PerformTransactionDTO performTransactionDTO) {
        return new ResponseEntity<>(userService.transfer(performTransactionDTO),HttpStatus.OK);
    }
}
