package com.example.springboot.accounting;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.example.springboot.accounting.model.entities.FixAccountInfo;
import com.example.springboot.accounting.model.entities.qb.AccountManager;
import com.example.springboot.accounting.model.entities.qb.AccountType;
import com.example.springboot.accounting.model.entities.qb.Ledger;
import com.example.springboot.accounting.model.entities.qb.LedgerRuleFactory;
import com.example.springboot.accounting.repository.RuleRepository;
import com.example.springboot.accounting.service.AccountFactory;
import com.example.springboot.accounting.service.TaxService;
import com.example.springboot.accounting.service.ledger.MutualFundStrategy;

/**
 * 
 */
public class TestFixture {
	public FixAccountInfo fixedAccount = new FixAccountInfo();
	public AccountManager accountManager = new AccountManager();
	public LedgerRuleFactory fac = new LedgerRuleFactory(accountManager);
	public TaxService tax = new TaxService();
	MutualFundStrategy strategy = new MutualFundStrategy();
	RuleRepository ruleRepo = new RuleRepository();
	
	public Ledger ledger = new Ledger(strategy,ruleRepo,accountManager, fac, tax);
	
	
	AccountFactory accountFactory = new AccountFactory();

	public TestFixture() {
		ruleRepo.factory=fac;
		ruleRepo.manager=accountManager;
		strategy.accountManager=accountManager;
		strategy.accounts=new FixAccountInfo();
		accountFactory.createAccounts(accountManager);
		accountManager.addAccount(fixedAccount.checkingAccount, AccountType.ASSET, false);
		accountManager.addAccount(fixedAccount.visaAccount, AccountType.LIABILITY, false);
		accountManager.addAccount(fixedAccount.investmentAccount , AccountType.ASSET, false);
		
		ruleRepo.createRules();
	   
	}
	
	double getCheckingBalence() 
	{
		return accountManager.getBalance(fixedAccount.checkingAccount.accountName);
	}
	

	public Double getBalance(String string) {
		return accountManager.getBalance(string);
	}

	public Double getCheckingBalance() {
		return getBalance(fixedAccount.checkingAccount.accountName);
	}
	
	
	public Double getCreditBalance() {
		return getBalance(fixedAccount.visaAccount.accountName);
	}
	
	public String getCheckingAccountName() 
	{
		return fixedAccount.checkingAccount.accountName;
	}
	/**
	 * 
	 * @param inputDate
	 * @return
	 */
	public static String getNextDate(String inputDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        
        // Parse the inputDate using the formatter
        LocalDate date = LocalDate.parse(inputDate, formatter);
        
        // Add one day to the parsed date
        LocalDate nextDay = date.plusDays(1);

        // Return the nextDay formatted as a string in the same format
        return nextDay.format(formatter);
    }

	public String getCreditAccountName() {
		 return fixedAccount.visaAccount.accountName;
	}

	public String getInvestmentAccountName() {
		return fixedAccount.investmentAccount.accountName;
	}
}
