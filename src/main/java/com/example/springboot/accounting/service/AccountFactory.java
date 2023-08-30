package com.example.springboot.accounting.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.springboot.accounting.model.entities.qb.AccountManager;
import com.example.springboot.accounting.model.entities.qb.AccountType;

@Service
public class AccountFactory {

	public void createAccounts(AccountManager accountManager) {

		
		// Assets
		createAssetAccounts(accountManager);
		
		// Liabilities
		createLiabilitiesAccounts(accountManager);

		// Equity
		createEquityAccounts(accountManager);

		// Revenue
		accountManager.addAccount("Consulting Revenue", "R001", AccountType.REVENUE, true);
		accountManager.addAccount("Miscellaneous Revenue", "R002", AccountType.REVENUE, true);

		// Expenses
		createExpensesAccounts(accountManager);
	}

	private void createExpensesAccounts(AccountManager accountManager) {
		SuffixCount count_exp = new SuffixCount("E");
		
		List<String> taxableExpenses = List.of(
				"Salaries and Wages", 
				"Professional Fees", 
				"Office Supplies", 
				"Software SAS",
				"Bank Fees",
				"Travel & Meals",
				"Loss on Asset Write-off",
				"Sales Tax Expense",// Using simplified method.
				"Depreciation Expense"
				);
	
		for (String string : taxableExpenses) {
			accountManager.addAccount(string, count_exp.getNext(), AccountType.EXPENSE, true);
		}
	
		accountManager.addAccount("To Classify", count_exp.getNext(), AccountType.EXPENSE, false);
		accountManager.addAccount("Income Tax Expense", count_exp.getNext(), AccountType.EXPENSE, false);
	}

	private void createEquityAccounts(AccountManager accountManager) {
		SuffixCount count_eq = new SuffixCount("EX");
		accountManager.addAccount("Owner's Draw", count_eq.getNext(), AccountType.EQUITY, false);
		accountManager.addAccount("Owner's Equity",count_eq.getNext(), AccountType.EQUITY, false);
		accountManager.addAccount("Retained Earnings", count_eq.getNext(), AccountType.EQUITY, false);
	}

	private void createLiabilitiesAccounts(AccountManager accountManager) {
		SuffixCount count_liab = new SuffixCount("L");
		List<String> liabilities = List.of(
				"Accounts Payable", 
				"Taxes Payable", 
				"Unearned Revenue");
		for (String string : liabilities) {
			accountManager.addAccount(string, count_liab.getNext(), AccountType.LIABILITY, false);
		}
	}

	private void createAssetAccounts(AccountManager accountManager) {
		SuffixCount count_asset = new SuffixCount("A");
		List<String> assets = List.of(
				"Accounts Receivable", 
				"Prepaid Expenses", 
				"Office Equipment", 
				"Loan to Owner",
				"Unknown");
		for (String string : assets) {
			accountManager.addAccount(string, count_asset.getNext(), AccountType.ASSET, false);
		}
	}
}
