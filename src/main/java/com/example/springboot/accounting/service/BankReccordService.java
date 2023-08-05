package com.example.springboot.accounting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springboot.accounting.model.entities.BankReccord;
import com.example.springboot.accounting.model.entities.BankStatement;
import com.example.springboot.accounting.repository.AccountRepository;
import com.example.springboot.accounting.repository.BankReccordRepository;
import com.example.springboot.accounting.repository.BankStatementRepository;

@Service
public class BankReccordService {

	private final AccountRepository accountRepository;
	private final BankReccordRepository repository;
	private final BankStatementRepository bankStatementrepository;

	@Autowired
	public BankReccordService(AccountRepository accountRepository, BankReccordRepository repository,
			BankStatementRepository bankStatementrepository) {
		this.repository = repository;
		this.bankStatementrepository = bankStatementrepository;
		this.accountRepository = accountRepository;
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
}
