package com.sam.accounting.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sam.accounting.model.entities.qb.AccountManager;
import com.sam.accounting.model.entities.qb.AccountType;
import com.sam.accounting.service.util.SuffixCount;

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
		createRevenueAccounts(accountManager);
		

		// Expenses
		createExpensesAccounts(accountManager);
		
		createSommationAccounts(accountManager);
	}

	private void createRevenueAccounts(AccountManager accountManager) {
		SuffixCount count_rev = new SuffixCount("R");
		List<String> revenues = List.of(
				"Consulting Revenue", 
				"Quick Method Benefit", 
				"Miscellaneous Revenue",
				"Realized Gain on Sale of Investments"
				);
		for (String string : revenues) {
			accountManager.addAccount(string, count_rev.getNext(), AccountType.REVENUE, true);
		}
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
				"Depreciation Expense",
				"Training"
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
		accountManager.addAccount("Owner's Contributions", count_eq.getNext(), AccountType.EQUITY, false);
	}

	private void createLiabilitiesAccounts(AccountManager accountManager) {
		SuffixCount count_liab = new SuffixCount("L");
		List<String> liabilities = List.of(
				"Accounts Payable", 
				"Taxes Payable", 
				"Sales Tax Payable",
				"Sales Tax Collected",
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
	
	private void createSommationAccounts(AccountManager accountManager) 
	{
		SuffixCount count_asset = new SuffixCount("S");
		List<String> assets = List.of(
				"Tax Savings from NOL", 
				"CA");
		for (String string : assets) {
			accountManager.addAccount(string, count_asset.getNext(), AccountType.SOMMATION, false);
		}
	}
}
