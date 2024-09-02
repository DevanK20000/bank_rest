package com.aurionpro.bankRest.repository;

import com.aurionpro.bankRest.entity.BankAccount;
import com.aurionpro.bankRest.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long>{
    Optional<List<BankAccount>> findByCustomer(Customer customer);

}
