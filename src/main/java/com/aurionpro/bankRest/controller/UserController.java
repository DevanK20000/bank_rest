package com.aurionpro.bankRest.controller;

import com.aurionpro.bankRest.dto.BankAccountDto;
import com.aurionpro.bankRest.dto.CustomerDto;
import com.aurionpro.bankRest.dto.PageResponse;
import com.aurionpro.bankRest.dto.TransactionDto;
import com.aurionpro.bankRest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    @PostMapping("/{accountNumber}/credit")
    public ResponseEntity<TransactionDto> credit(@PathVariable Long accountNumber, @RequestParam Double amount) {
        return new ResponseEntity<>(userService.credit(accountNumber, amount),HttpStatus.OK);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/{accountNumber}/debit")
    public ResponseEntity<TransactionDto> debit(@PathVariable Long accountNumber, @RequestParam Double amount) {
        return new ResponseEntity<>(userService.debit(accountNumber, amount),HttpStatus.OK);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("{senderAccountNumber}/transfer")
    public ResponseEntity<TransactionDto> transfer(@PathVariable Long senderAccountNumber, @RequestParam Long receiverAccountNumber, @RequestParam Double amount) {
        return new ResponseEntity<>(userService.transfer(senderAccountNumber, receiverAccountNumber, amount),HttpStatus.OK);
    }
}
