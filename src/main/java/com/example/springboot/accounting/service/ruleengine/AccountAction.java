package com.example.springboot.accounting.service.ruleengine;

public class AccountAction {
    private String account;
    private String accountName;
	protected String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
    
}
