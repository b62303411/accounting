package com.example.springboot.accounting;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.example.springboot.accounting.model.entities.qb.Account;
import com.example.springboot.accounting.model.entities.qb.AccountManager;
import com.example.springboot.accounting.model.entities.qb.AccountType;
import com.example.springboot.accounting.model.entities.qb.Ledger;
import com.example.springboot.accounting.model.entities.qb.LedgerRuleFactory;
import com.example.springboot.accounting.service.AccountFactory;
import com.example.springboot.accounting.service.TaxService;

public class TestFixture {
	public AccountManager accountManager = new AccountManager();
	public LedgerRuleFactory fac = new LedgerRuleFactory(accountManager);
	public TaxService tax = new TaxService();
	public Ledger ledger = new Ledger(accountManager, fac, tax);
	public Account cash = new Account("Cash", "001", AccountType.ASSET, false);
	public Account salesRevenue = new Account("Sales Revenue", "002", AccountType.REVENUE, true);
	public Account salesTaxPayable = new Account("Sales Tax Payable", "003", AccountType.LIABILITY, false);
	public Account equipment = new Account("Equipment", "001", AccountType.ASSET, false);
	public Account payable = new Account("Accounts Payable", "003", AccountType.LIABILITY, false);
	public Account retainedEarnings = new Account("Retained Earnings", "001", AccountType.EQUITY, false);
	public Account dividendsDeclared = new Account("Dividends Declared", "002", AccountType.EQUITY, false);
	public Account checking = new Account("Checking", "001", AccountType.ASSET, false);
	// Account savings = new Account("Savings", "002", AccountType.ASSET);
	public Account creditCard = new Account("Credit Card", "003", AccountType.LIABILITY, false);
	public Account investment = new Account("Investment", "004", AccountType.ASSET, false);

	public Account amazon = new Account("Amazon", "005", AccountType.LIABILITY, false); // Assuming Amazon's invoice would
																					// be a
	// liability until paid.
	public Account lawyer = new Account("Lawyer", "006", AccountType.LIABILITY, false);
	public Account accountant = new Account("Accountant", "007", AccountType.LIABILITY, false);
	public Account government = new Account("Government", "008", AccountType.LIABILITY, false);
	private String checking_str="TD_EVERY_DAY_A_BUSINESS_PLAN";
	private String credit_str="TD BUSINESS VISA";

	public TestFixture() {
		accountManager.addAccount(cash);
		accountManager.addAccount(salesRevenue);
		accountManager.addAccount(salesTaxPayable);
		accountManager.addAccount(payable);
		accountManager.addAccount(dividendsDeclared);
		accountManager.addAccount(retainedEarnings);
		accountManager.addAccount(equipment);
		accountManager.addAccount(investment);
		accountManager.addAccount(lawyer);
		accountManager.addAccount(government);
		accountManager.addAccount(creditCard);
		accountManager.addAccount(checking);
		createAccounts(accountManager);
	}
	
	private void createAccounts(AccountManager accountManager) {
		// Assets
		accountManager.addAccount(checking_str, "A001", AccountType.ASSET, false);
		accountManager.addAccount(credit_str, "L004", AccountType.LIABILITY, false);
		AccountFactory factory = new AccountFactory();
		factory.createAccounts(accountManager);
	}

	public Double getBalance(String string) {
		return accountManager.getBalance(string);
	}

	public Double getCheckingBalance() {
		return getBalance(this.checking_str);
	}
	
	public Double getCreditBalance() {
		return getBalance(this.credit_str);
	}
	
	public static String getNextDate(String inputDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        
        // Parse the inputDate using the formatter
        LocalDate date = LocalDate.parse(inputDate, formatter);
        
        // Add one day to the parsed date
        LocalDate nextDay = date.plusDays(1);

        // Return the nextDay formatted as a string in the same format
        return nextDay.format(formatter);
    }
}
