package com.example.springboot.accounting.model.entities.qb;

public enum AccountType {
	ASSET(AccountBalance.DEBIT), 
	LIABILITY(AccountBalance.CREDIT), 
	EQUITY(AccountBalance.CREDIT),
	REVENUE(AccountBalance.CREDIT), 
	EXPENSE(AccountBalance.DEBIT),
	SOMMATION(AccountBalance.DEBIT),;

	private final AccountBalance naturalBalance;

	AccountType(AccountBalance naturalBalance) {
		this.naturalBalance = naturalBalance;
	}

	public AccountBalance getNaturalBalance() {
		return naturalBalance;
	}
}
