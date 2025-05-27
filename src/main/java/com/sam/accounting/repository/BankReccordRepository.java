package com.sam.accounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sam.accounting.model.entities.BankReccord;
import com.sam.accounting.model.entities.Transaction;

public interface BankReccordRepository extends JpaRepository<BankReccord, Long>{

	BankReccord findByTransaction(Transaction to_be_deleted);

}
