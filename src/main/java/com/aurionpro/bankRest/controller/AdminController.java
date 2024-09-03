package com.aurionpro.bankRest.controller;

import com.aurionpro.bankRest.dto.*;
import com.aurionpro.bankRest.service.AdminServices;
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
    public ResponseEntity<PageResponse<CustomerDto>> getAllCustomers(@RequestParam int pageNo, @RequestParam int pageSize){
        return  new ResponseEntity<>(adminServices.getAllCustomers(pageNo,pageSize), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/bankAccounts")
    public ResponseEntity<PageResponse<BankAccountDto>> getAllBankAccounts(@RequestParam int pageNo, @RequestParam int pageSize){
        return  new ResponseEntity<>(adminServices.getAllBankAccount(pageNo,pageSize), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/transactions")
    public ResponseEntity<PageResponse<TransactionDto>> getAllTransactions(@RequestParam int pageNo, @RequestParam int pageSize){
        return  new ResponseEntity<>(adminServices.getAllTransactions(pageNo,pageSize), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users/customers")
    public ResponseEntity<CustomerDto> addCustomerToUserId(@RequestBody AddCustomerDto addCustomerDto) {
        return new ResponseEntity<CustomerDto>(adminServices.addCustomerToUserId(addCustomerDto), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/customers/bankAccounts")
    public ResponseEntity<BankAccountDto> addBankAccountToCustomer(@RequestBody AddBankAccountDto addBankAccountDto) {
        return new ResponseEntity<BankAccountDto>(adminServices.addBankAccountToCustomer(addBankAccountDto), HttpStatus.CREATED);
    }
}
