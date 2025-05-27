package com.sam.accounting.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sam.accounting.model.AccountLedger;
import com.sam.accounting.model.dto.LedgerEntryDTO;
import com.sam.accounting.model.entities.qb.Account;
import com.sam.accounting.model.entities.qb.AccountManager;

@Service
public class LedgerService {
	@Autowired
	private AccountManager accountManager;
	@Autowired
	private GeneralLedgerService gls;
	
	public List<AccountLedger> createPagesPerAccounts(int fiscal_year) {
		List<LedgerEntryDTO> entries = gls.getLedgerDtos(fiscal_year);
	
	
		List<Account> accounts = accountManager.getAccounts();
		List<AccountLedger> ledgers = new ArrayList<>();
		for (Account account : accounts) {
			AccountLedger ledger_acc= new  AccountLedger();
			ledger_acc =  new AccountLedger();
			ledgers.add(ledger_acc);
			ledger_acc.account=account;
			if(null == account) 
			{
				System.err.println();
			}
		
	
			ledger_acc.entries= new ArrayList();
	
			for (LedgerEntryDTO item : entries) {
				if(account.getAccountNumber().equals(item.getGlAccountNumber())) 
				{
					ledger_acc.entries.add(item);
				}
				
			}
			
		}
		return ledgers;
	}
	
}
