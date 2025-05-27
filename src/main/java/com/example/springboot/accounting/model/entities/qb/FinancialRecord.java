package com.example.springboot.accounting.model.entities.qb;

import com.example.springboot.accounting.model.Sequence;

public class FinancialRecord extends Transaction{
	public FinancialRecord(Sequence seq) {
		super(seq);
		// TODO Auto-generated constructor stub
	}
	private String transactionType; // Expense, Revenue, etc.
	private String account; // Associated account for the transaction
	private String debitOrCredit; // Debit or Credit
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getDebitOrCredit() {
		return debitOrCredit;
	}
	public void setDebitOrCredit(String debitOrCredit) {
		this.debitOrCredit = debitOrCredit;
	}
	
	
}
