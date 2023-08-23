package com.example.springboot.accounting.model.entities.qb;

public class Account {
	private String name;
	private String accountNumber;
	private double balance;
	private AccountType type;
	private boolean isTaxable;

	public Account() {
	}

	/**
	 * 
	 * @param name
	 * @param accountNumber
	 * @param type
	 * @param isDeductable
	 */
	public Account(String name, String accountNumber, AccountType type, boolean isTaxable) {
		this.name = name;
		this.accountNumber = accountNumber;
		this.type = type;
		this.isTaxable = isTaxable;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void debit(double amount) {
		if (type.getNaturalBalance() == AccountBalance.DEBIT) {
			balance += amount;
		} else {
			balance -= amount;
		}
	}

	public void credit(double amount) {
		if (type.getNaturalBalance() == AccountBalance.DEBIT) {
			balance -= amount;
		} else {
			balance += amount;
		}
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


	

}
