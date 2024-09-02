package com.aurionpro.bankRest.repository;

import com.aurionpro.bankRest.entity.Customer;
import com.aurionpro.bankRest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer>{
    Optional<Customer> findByUser(User user);

}
