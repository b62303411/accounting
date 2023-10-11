package com.example.springboot.accounting.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springboot.accounting.model.entities.Account;
import com.example.springboot.accounting.model.entities.FixAccountInfo;
import com.example.springboot.accounting.model.entities.qb.AccountManager;
import com.example.springboot.accounting.model.entities.qb.AccountType;
import com.example.springboot.accounting.repository.AccountRepository;

@Service
public class AccountService {

	//@Autowired
	private AccountFactory factory;

	//@Autowired
	public AccountManager accountManager;

	//@Autowired
	public AccountRepository a_repo;

	private FixAccountInfo fixAccountInfo;
   
	
	@Autowired
	public AccountService(
			AccountFactory factory,
			AccountManager accountManager,
			AccountRepository a_repo,
			FixAccountInfo accountInfo
			) 
	{
		this.factory=factory;
		this.accountManager=accountManager;
		this.a_repo=a_repo;
		this.fixAccountInfo=accountInfo;
		initiazeAccounts();
	}
	
	
	public void initiazeAccounts() 
	{
		factory.createAccounts(accountManager);
		List<Account> acounts = a_repo.findAll();
		for (Account account : acounts) {
			if (account.getAccountName().contains("TD_EVERY_DAY"))
				accountManager.addAccount(account.getAccountName(), account.getAccountNo(), AccountType.ASSET, false);
			if (account.getAccountName().contains("VISA"))
				accountManager.addAccount(account.getAccountName(), account.getAccountNo(), AccountType.LIABILITY,
						false);
			if (account.getAccountNo().equals("1"))
				accountManager.addAccount(account.getAccountName(), account.getAccountNo(), AccountType.EQUITY,
						false);
		}
		accountManager.addAccount(
				fixAccountInfo.personalAccount.accountName, 
				fixAccountInfo.personalAccount.accountNo, AccountType.EQUITY, false);
		
		accountManager.addAccount(
				fixAccountInfo.investmentAccount.accountName, 
				fixAccountInfo.investmentAccount.accountNo, AccountType.ASSET, false);
	}
	
	public List<com.example.springboot.accounting.model.entities.qb.Account> findAll(){
		return accountManager.getAccounts();
   }
	
	
	public List<Account> getAccountsByType(AccountType type) {

		return accountManager.getAccountByType(type);
	}

	public double getTotalByType(AccountType type) {
		return accountManager.getAccountByType(type).stream().mapToDouble(Account::getBalance).sum();
	}

	public List<Account> getTaxableAccountsByType(AccountType expense) {
		return accountManager.getAccountByType(expense, true);
	}

	public double getTotalNonTaxableByType(AccountType type) {
		return getTotalByTypes(type, false);
	}

	private double getTotalByTypes(AccountType type, boolean isTaxable) {
		return accountManager.getAccountByType(type, isTaxable).stream().mapToDouble(Account::getBalance).sum();
	}

	public double getTotalTaxableByType(AccountType type) {
		return getTotalByTypes(type, true);
	}

	public List<Account> getNonTaxableAccountsByType(AccountType expense) {
		return accountManager.getAccountByType(expense, false);
	}

}
