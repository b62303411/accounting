package com.example.springboot.accounting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springboot.accounting.model.entities.BankReccord;
import com.example.springboot.accounting.model.entities.BankStatement;
import com.example.springboot.accounting.model.entities.Transaction;
import com.example.springboot.accounting.repository.BankReccordRepository;
import com.example.springboot.accounting.repository.BankStatementRepository;

@Service
public class BankReccordService {

	private final BankReccordRepository repository;
	private final BankStatementRepository bankStatementrepository;

	@Autowired
	public BankReccordService(BankReccordRepository repository, BankStatementRepository bankStatementrepository) {
		this.repository = repository;
		this.bankStatementrepository = bankStatementrepository;

	}

	public void save(BankReccord br) {
		repository.save(br);
	}

	public void save(BankStatement bs) {
		try {
			bankStatementrepository.save(bs);
		} catch (Exception e) {
		}

	}

	/**
	 * 
	 * @param to_be_deleted
	 * @return
	 */
	public BankReccord findByTransaction(Transaction to_be_deleted) {
		return repository.findByTransaction(to_be_deleted);
	}

	public void delete(BankReccord br) {
		repository.delete(br);
	}
}
