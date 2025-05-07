package com.sam.accounting.model.entities;

import org.springframework.stereotype.Service;

@Service
public class FixAccountInfo {
	
	public AccountInfo checkingAccount = new AccountInfo("5235425","TD_EVERY_DAY_A_BUSINESS_PLAN");
	public AccountInfo visaAccount = new AccountInfo("7053","VISA_TD_REMISES_AFFAIRES");
	public AccountInfo investmentAccount = new AccountInfo("9115997","FONDS MUTUELS TD");
	public AccountInfo personalAccount = new AccountInfo("1","Personal Account");

}
