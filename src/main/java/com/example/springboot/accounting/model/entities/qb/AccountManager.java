package com.example.springboot.accounting.model.entities.qb;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;


@Service
public class AccountManager {
	private Map<String, Account> accounts;

	public AccountManager() {
		this.accounts = new HashMap<>();
	}

	/**
	 * 
	 * @param name
	 * @param accountNumber
	 * @param type
	 * @param isDeductable
	 */
	public void addAccount(String name, String accountNumber, AccountType type,boolean isTaxable ) {
	   accounts.put(name, new Account(name,accountNumber,type,isTaxable));
	}

	public Account getAccount(String type) {
		return accounts.get(type);
	}

	public void displayAllBalances() {
		for (Account account : accounts.values()) {
			System.out.println(account);
		}
	}

	public void addAccount(Account acc) {
		accounts.put(acc.getName(), acc);
		
	}

	public Account getAccountByName(String string) {
		return accounts.get(string);
	}

	public Collection<Account> getAccounts() {
		return accounts.values();
	}

	public double getBalance(String accountName) {
		return accounts.get(accountName).getBalance();
	}

	/**
	 * 
	 * @param accountNo
	 * @return
	 */
	public Account getAccountByAccountNo(String accountNo) {
		Collection<Account> values = accounts.values();
		for (Account account : values) {
			if(account.getAccountNumber().equals(accountNo))
				return account;
		}
		return null;
	}
}
