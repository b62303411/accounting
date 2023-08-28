package com.example.springboot.accounting.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springboot.accounting.model.entities.Account;
import com.example.springboot.accounting.model.entities.qb.AccountManager;
import com.example.springboot.accounting.model.entities.qb.AccountType;

@Service
public class AccountService {

	@Autowired
	private AccountFactory factory;
	
	@Autowired
	public AccountManager accountManager;
	
	public List<Account> getAccountsByType(AccountType type) {
	
		return accountManager.getAccountByType(type);
	}

	public double getTotalByType(AccountType type) {
	    return accountManager.getAccountByType(type).stream()
                .mapToDouble(Account::getBalance)
                .sum();
	}

}
