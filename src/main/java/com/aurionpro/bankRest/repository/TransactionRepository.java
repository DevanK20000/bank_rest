package com.aurionpro.bankRest.repository;

import com.aurionpro.bankRest.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long>{

}
