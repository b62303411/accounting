package com.example.springboot.accounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.springboot.accounting.model.entities.BankReccord;
import com.example.springboot.accounting.model.entities.Transaction;

public interface BankReccordRepository extends JpaRepository<BankReccord, Long>{

	BankReccord findByTransaction(Transaction to_be_deleted);

}
