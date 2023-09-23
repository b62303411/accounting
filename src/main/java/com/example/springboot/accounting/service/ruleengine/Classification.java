package com.example.springboot.accounting.service.ruleengine;

public class Classification {
    private AccountAction debit;
    private AccountAction credit;
    public AccountAction getDebit() {
		return debit;
	}
	public void setDebit(AccountAction debit) {
		this.debit = debit;
	}
	public AccountAction getCredit() {
		return credit;
	}
	public void setCredit(AccountAction credit) {
		this.credit = credit;
	}
    
}
