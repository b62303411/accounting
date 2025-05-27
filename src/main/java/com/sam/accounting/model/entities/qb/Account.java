package com.sam.accounting.model.entities.qb;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Account {
	private String name;
	private String accountNumber;
	private double balance;
	private AccountType type;
	private boolean isTaxable;
	Set<UUID> ids;
	public Account() {
		ids= new HashSet<UUID>();
	}

	/**
	 * 
	 * @param name
	 * @param accountNumber
	 * @param type
	 * @param isDeductable
	 */
	public Account(String name, String accountNumber, AccountType type, boolean isTaxable) {
		this();
		this.name = name;
		this.accountNumber = accountNumber;
		this.type = type;
		this.isTaxable = isTaxable;
		
	}

	public Account(Account account) {
		this();
		this.name=account.getName();
		this.accountNumber=account.getAccountNumber();
		this.type=account.getType();
		this.isTaxable=account.isTaxable;
		this.balance=0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void debit(double amount,UUID entryId) {
		this.ids.add(entryId);
		System.out.println(accountNumber+" NB:"+type.getNaturalBalance()+" Balence:"+balance);
		System.out.println(accountNumber+" Debit:"+amount);
		if (type.getNaturalBalance() == AccountBalance.DEBIT) {
			balance += amount;
		} else {
			balance -= amount;
		}
		System.out.println(accountNumber+" Balence:"+balance);
	}

	public void credit(double amount, UUID entryId) {
		this.ids.add(entryId);
		System.out.println(accountNumber+" NB:"+type.getNaturalBalance()+" Balence:"+balance);
		System.out.println(accountNumber+" Credit:"+amount);
		if (type.getNaturalBalance() == AccountBalance.DEBIT) {
			balance -= amount;
		} else {
			balance += amount;
		}
		System.out.println(accountNumber+" Balence:"+balance);
	}

	@Override
	public String toString() {
		return name + " (" + accountNumber + "): " + balance;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public AccountType getAccountType() {

		return type;
	}

	public AccountType getType() {
		return type;
	}

	public void setType(AccountType type) {
		this.type = type;
	}

	public boolean isTaxable() {
		return isTaxable;
	}

	public void setTaxable(boolean isTaxable) {
		this.isTaxable = isTaxable;
	}

	public boolean hasExecuted(UUID id) {
		return ids.contains(id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(accountNumber);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;
		return Objects.equals(accountNumber, other.accountNumber);
	}

	public void clear() {
		balance=0;
		ids.clear();
	}


	

}
