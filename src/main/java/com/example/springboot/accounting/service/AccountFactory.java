package com.example.springboot.accounting.service;

import org.springframework.stereotype.Service;

import com.example.springboot.accounting.model.entities.qb.AccountManager;
import com.example.springboot.accounting.model.entities.qb.AccountType;

@Service
public class AccountFactory {
	public void createAccounts(AccountManager accountManager) {
	
	    // Assets
		accountManager.addAccount("Accounts Receivable", "A002", AccountType.ASSET,false);
		accountManager.addAccount("Prepaid Expenses", "A003", AccountType.ASSET,false);
		accountManager.addAccount("Office Equipment", "A004", AccountType.ASSET,false);
		accountManager.addAccount("Loan to Owner", "A005", AccountType.ASSET,false);
		accountManager.addAccount("Unknown","A006",AccountType.ASSET,false);
		// Liabilities
		accountManager.addAccount("Accounts Payable", "L001", AccountType.LIABILITY,false);
		accountManager.addAccount("Taxes Payable", "L002", AccountType.LIABILITY,false);
		accountManager.addAccount("Unearned Revenue", "L003", AccountType.LIABILITY,false);
	

		// Equity
		accountManager.addAccount("Owner's Draw", "E001", AccountType.EQUITY,false);
		accountManager.addAccount("Owner's Equity", "E002", AccountType.EQUITY,false);
		accountManager.addAccount("Retained Earnings", "E003", AccountType.EQUITY,false);
	

		// Revenue
		accountManager.addAccount("Consulting Revenue", "R001", AccountType.REVENUE,true);
		accountManager.addAccount("Miscellaneous Revenue", "R002", AccountType.REVENUE,true);

		// Expenses
		accountManager.addAccount("Salaries and Wages", "EX001", AccountType.EXPENSE,true);
		accountManager.addAccount("Professional Fees", "EX002", AccountType.EXPENSE,true);
		accountManager.addAccount("Office Supplies", "EX003", AccountType.EXPENSE,true);
		accountManager.addAccount("Software SAS", "A005", AccountType.EXPENSE,true);
		accountManager.addAccount("Bank Fees", "EX006", AccountType.EXPENSE,true);
		accountManager.addAccount("Income Tax Expense", "EX007", AccountType.EXPENSE,true);
		accountManager.addAccount("Travel & Meals", "EX008", AccountType.EXPENSE,true);
		accountManager.addAccount("Loss on Asset Write-off", "EX009", AccountType.EXPENSE,true);
		// Using simplified method. 
		accountManager.addAccount("Sales Tax Expense","EX009", AccountType.EXPENSE,true);
		accountManager.addAccount("Depreciation Expense","EX010", AccountType.EXPENSE,true);
		accountManager.addAccount("To Classify","EX011", AccountType.EXPENSE,false);
	}
}
