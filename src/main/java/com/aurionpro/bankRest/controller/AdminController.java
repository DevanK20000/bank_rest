package com.aurionpro.bankRest.controller;

import com.aurionpro.bankRest.dto.*;
import com.aurionpro.bankRest.service.AdminServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminServices adminServices;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/customers")
    public ResponseEntity<PageResponse<CustomerDto>> getAllCustomers(@RequestParam int pageNo, @RequestParam int pageSize,@RequestParam(required = false,defaultValue = "false") boolean inactive){
        return new ResponseEntity<>(adminServices.getAllCustomers(pageNo,pageSize,inactive), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/bankAccounts")
    public ResponseEntity<PageResponse<BankAccountDto>> getAllBankAccounts(@RequestParam int pageNo, @RequestParam int pageSize,@RequestParam(required = false,defaultValue = "false") boolean inactive){
        return new ResponseEntity<>(adminServices.getAllBankAccount(pageNo,pageSize,inactive), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/transactions")
    public ResponseEntity<PageResponse<TransactionDto>> getAllTransactions(@RequestParam int pageNo, @RequestParam int pageSize){
        return new ResponseEntity<>(adminServices.getAllTransactions(pageNo,pageSize), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users/customers")
    public ResponseEntity<CustomerDto> addCustomerToUserId(@Valid @RequestBody AddCustomerDto addCustomerDto) {
        return new ResponseEntity<CustomerDto>(adminServices.addCustomerToUserId(addCustomerDto), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/customers/bankAccounts")
    public ResponseEntity<BankAccountDto> addBankAccountToCustomer(@Valid @RequestBody AddBankAccountDto addBankAccountDto) {
        return new ResponseEntity<BankAccountDto>(adminServices.addBankAccountToCustomer(addBankAccountDto), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/customers/{customerId}/delete")
    public ResponseEntity<CustomerDto> softDeleteCustomer(@PathVariable int customerId) {
        return new ResponseEntity<CustomerDto>(adminServices.softDeleteCustomer(customerId), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/bankAccounts/{accountNumber}/delete")
    public ResponseEntity<BankAccountDto> softDeleteBankAccount(@PathVariable Long accountNumber) {
        return new ResponseEntity<BankAccountDto>(adminServices.softDeleteBankAccount(accountNumber), HttpStatus.OK);
    }
}
