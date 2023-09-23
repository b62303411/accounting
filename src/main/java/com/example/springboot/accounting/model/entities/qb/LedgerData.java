package com.example.springboot.accounting.model.entities.qb;

public class LedgerData {
	public String creditCardAccountNo;
	public String checkAccountNo;
	public String investmentAccountNo;

	public LedgerData(String creditCardAccountNo, String checkAccountNo, String investmentAccountNo) {
		this.creditCardAccountNo = creditCardAccountNo;
		this.checkAccountNo = checkAccountNo;
		this.investmentAccountNo = investmentAccountNo;
	}
}