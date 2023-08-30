package com.example.springboot.accounting.model.entities;

import com.example.springboot.accounting.model.entities.qb.AccountType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Account {
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String type;  // CHECKING, SAVINGS, etc. 
    private String accountNo;
    private String accountName;// "TD EVERY DAY A BUSINESS PLAN", "VISA CARD"
    private String alias;
	private Double balance;
	@Enumerated(EnumType.STRING)
	private AccountType accountingType;
	public Double getBalance() {
		return balance;
	}
	public void setBalance(Double balance) {
		this.balance = balance;
	}
	public AccountType getAccountingType() {
		return accountingType;
	}
	public void setAccountingType(AccountType accountingType) {
		this.accountingType = accountingType;
	}
	public Boolean isTaxable() {
		return isTaxable;
	}
	public void setTaxable(Boolean isTaxable) {
		this.isTaxable = isTaxable;
	}
	private Boolean isTaxable;
	
    public Account() {}
	public Account(String type) {
		// TODO Auto-generated constructor stub
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
    
    

}
