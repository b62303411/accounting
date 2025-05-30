package com.sam.accounting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sam.accounting.model.entities.BankStatement;

public interface BankStatementRepository  extends JpaRepository<BankStatement, Long>{

	List<BankStatement> findAllByYear(Integer year);

	List<BankStatement> findAllByYearAndAcc(Integer year, String accountNumber);

}
