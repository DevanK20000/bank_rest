package com.aurionpro.bankRest.repository;

import com.aurionpro.bankRest.entity.BankAccount;
import com.aurionpro.bankRest.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long>{
    Optional<List<BankAccount>> findByCustomer(Customer customer);

    @Modifying
    @Query("UPDATE BankAccount b SET b.active = false WHERE b IN :bankAccounts")
    void updateBankAccountsAsInactive(@Param("bankAccounts") List<BankAccount> bankAccounts);
}
