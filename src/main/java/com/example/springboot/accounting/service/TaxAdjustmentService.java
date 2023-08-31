package com.example.springboot.accounting.service;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springboot.accounting.model.entities.qb.Account;
import com.example.springboot.accounting.model.entities.qb.AccountManager;
import com.example.springboot.accounting.model.entities.qb.AccountType;
import com.example.springboot.accounting.model.entities.qb.EntryType;
import com.example.springboot.accounting.model.entities.qb.Transaction;
import com.example.springboot.accounting.model.entities.qb.TransactionEntry;

@Service
public class TaxAdjustmentService {
	@Autowired
	private AccountManager accountManager; // Account manager to fetch and update accounts

	@Autowired
	private CompanyProfileService cps;
	
	@Autowired
	private GeneralLedgerService gls;

	/**
	 * Create a tax adjustment transaction for Net Operating Loss
	 * 
	 * @param nolAmount           the amount of Net Operating Loss
	 * @param taxLiabilityAccount the account representing tax liability
	 * @param taxExpenseAccount   the account representing tax expense
	 * @return 
	 */
	public Transaction createTaxAdjustmentForNOL(BigDecimal nolAmount,Date date) {

		String taxLiabilityAccountNumber = "";
		Account taxLiabilityAccount = accountManager.getAccountByAccountNo(taxLiabilityAccountNumber);
		String taxExpenseAccountNumber = "";
		Account taxExpenseAccount = accountManager.getAccountByAccountNo(taxExpenseAccountNumber);

		if (taxLiabilityAccount != null && taxExpenseAccount != null) {
			// Prepare the transaction
			Transaction taxAdjustmentTransaction = new Transaction();
			taxAdjustmentTransaction.setDate(date); // Assuming current date
			taxAdjustmentTransaction.setDescription("Adjustment for Net Operating Loss");

			// Create entries
			TransactionEntry creditEntry = new TransactionEntry();
			creditEntry.setAccount(taxLiabilityAccount);
			creditEntry.setType(EntryType.CREDIT);
			creditEntry.setAmount(nolAmount.doubleValue());

			TransactionEntry debitEntry = new TransactionEntry();
			debitEntry.setAccount(taxExpenseAccount);
			debitEntry.setType(EntryType.DEBIT);
			debitEntry.setAmount(nolAmount.doubleValue());

			// Add entries to transaction
			taxAdjustmentTransaction.addEntry(creditEntry);
			taxAdjustmentTransaction.addEntry(debitEntry);

			// Post the transaction
			taxAdjustmentTransaction.post();
			
			return taxAdjustmentTransaction;

		}
		return null;
	}

	public Transaction generateIncomeTaxTransaction(double incomeTaxExpense,Date yearEndDate) {
		// Calculate the income tax expense based on the year's net income

		// Get the date for the last day of the fiscal year

		// Create a new transaction
		Transaction taxTransaction = new Transaction();
		taxTransaction.setDate(yearEndDate);

		// Debit Income Tax Expense
		TransactionEntry taxExpenseEntry = new TransactionEntry();
		taxTransaction.setMessage("Year End Income Tax Expended");
		taxTransaction.setDescription("Year End Income Tax Expended");
		Account ite = getAccountByTypeAndName(AccountType.EXPENSE, "Income Tax Expense");
		Account tp =getAccountByTypeAndName(AccountType.LIABILITY, "Taxes Payable");
		taxExpenseEntry.setAccount(ite);
		taxExpenseEntry.setAmount(incomeTaxExpense);
		taxExpenseEntry.setType(EntryType.DEBIT);
		taxExpenseEntry.setDate(yearEndDate);
		taxExpenseEntry.setVendor_client("Canada Revenue Agency");
		taxTransaction.addEntry(taxExpenseEntry);
		

		// Credit Taxes Payable
		TransactionEntry taxesPayableEntry = new TransactionEntry();
		taxesPayableEntry.setAccount(tp);
		taxesPayableEntry.setAmount(incomeTaxExpense);
		taxesPayableEntry.setType(EntryType.CREDIT);
		taxesPayableEntry.setDate(yearEndDate);
		taxesPayableEntry.setVendor_client("Canada Revenue Agency");
	
		taxTransaction.addEntry(taxesPayableEntry);

		// Post the transaction to the ledger
		
		
		return taxTransaction;
	}

	private Date getYearEndDate(int fiscalYear) {
		return cps.getProfile().getFiscalYearEnd().getLastDayDate(fiscalYear);
	}

	private Account getAccountByTypeAndName(AccountType expense, String name) {
		return accountManager.getAccountByName(name);
	}

	public void postTransactionToLedger(Transaction taxTransaction) {
		gls.getLedger().postTransaction(taxTransaction);
		gls.clearCashedLedger();

	}

}
