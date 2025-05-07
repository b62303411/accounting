package com.sam.accounting.model.entities.qb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.sam.accounting.model.entities.AccountInfo;

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
	public void addAccount(
			String name, 
			String accountNumber, 
			AccountType type, 
			boolean isTaxable) {
		accounts.put(name, new Account(name, accountNumber, type, isTaxable));
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

	public List<Account> getAccounts() {
		return new ArrayList<Account>(accounts.values());
	}

	public double getBalance(String accountName) {
		if(!accounts.containsKey(accountName)) 
		{
			System.err.println();
		}
		
		Account account = accounts.get(accountName);
		
		return account.getBalance();
	}

	/**
	 * 
	 * @param accountNo
	 * @return
	 */
	public Account getAccountByAccountNo(String accountNo) {
		Collection<Account> values = accounts.values();
		for (Account account : values) {
			if (account.getAccountNumber().equals(accountNo))
				return account;
		}
		return null;
	}

	public List<com.sam.accounting.model.entities.Account> getAccountByType(AccountType type) {
		Collection<Account> values = accounts.values();
		List<com.sam.accounting.model.entities.Account> list = new ArrayList<com.sam.accounting.model.entities.Account>();
		for (Account account : values) {
			if (account.getAccountType() == type) {
				com.sam.accounting.model.entities.Account a = new com.sam.accounting.model.entities.Account();
				a.setAccountingType(account.getAccountType());
				a.setAccountNo(account.getAccountNumber());
				a.setBalance(account.getBalance());
				a.setTaxable(account.isTaxable());
				a.setAccountName(account.getName());
				list.add(a);
			}
		}
		return list;
	}

	public List<com.sam.accounting.model.entities.Account> getAccountByType(AccountType type, boolean b) {
		Collection<Account> values = accounts.values();
		List<com.sam.accounting.model.entities.Account> list = new ArrayList<com.sam.accounting.model.entities.Account>();
		for (Account account : values) {
			if (account.getAccountType() == type && account.isTaxable() ==b) {
				com.sam.accounting.model.entities.Account a = new com.sam.accounting.model.entities.Account();
				a.setAccountingType(account.getAccountType());
				a.setAccountNo(account.getAccountNumber());
				a.setBalance(account.getBalance());
				a.setTaxable(account.isTaxable());
				a.setAccountName(account.getName());
				list.add(a);
			}
		}
		return list;
	}

	public void addAccount(AccountInfo info, AccountType type, boolean b) {
		addAccount(info.accountName,info.accountNo,type,b);
		
	}
}
