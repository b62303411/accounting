package com.sam.accounting.service;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sam.accounting.model.Sequence;
import com.sam.accounting.model.entities.qb.Account;
import com.sam.accounting.model.entities.qb.AccountManager;
import com.sam.accounting.model.entities.qb.AccountType;
import com.sam.accounting.model.entities.qb.EntryType;
import com.sam.accounting.model.entities.qb.Transaction;
import com.sam.accounting.model.entities.qb.TransactionEntry;

@Service
public class TaxAdjustmentService {
	@Autowired
	private AccountManager accountManager; // Account manager to fetch and update accounts

	@Autowired
	private CompanyProfileService cps;
	
	@Autowired
	private GeneralLedger gl;
	//private GeneralLedgerService gls;

	/**
	 * Create a tax adjustment transaction for Net Operating Loss
	 * 
	 * @param nolAmount           the amount of Net Operating Loss
	 * @param seq 
	 * @param taxLiabilityAccount the account representing tax liability
	 * @param taxExpenseAccount   the account representing tax expense
	 * @return 
	 */
	public Transaction createTaxAdjustmentForNOL(
			BigDecimal nolAmount,
			Date date_of_transaction,
			Date date_of_affected_year, 
			Sequence seq) {
		Account taxSavingAccount = getAccountByTypeAndName(AccountType.ASSET, "Tax Savings from NOL");
		Account taxExpenseAccount = getAccountByTypeAndName(AccountType.EXPENSE, "Income Tax Expense");
		Account taxLiabilityAccount =getAccountByTypeAndName(AccountType.LIABILITY, "Taxes Payable");

		if (taxLiabilityAccount != null && taxExpenseAccount != null) {
			// Prepare the transaction
			Transaction taxAdjustmentTransaction = new Transaction(seq);
			taxAdjustmentTransaction.setDate(date_of_transaction); // Assuming current date
			
			String description = "Adjustment for Net Operating Loss:"+date_of_affected_year;
			
			taxAdjustmentTransaction.setDescription(description);
			taxAdjustmentTransaction.setMessage(description);
		
			// Create entries
			TransactionEntry creditEntry = new TransactionEntry();
			creditEntry.setAccount(taxLiabilityAccount);
			creditEntry.setType(EntryType.CREDIT);
			creditEntry.setAmount(nolAmount.doubleValue());
			creditEntry.setVendor_client("Canada Revenue Agency");
			creditEntry.setDate(date_of_transaction);
			TransactionEntry debitEntry = new TransactionEntry();
			debitEntry.setAccount(taxExpenseAccount);
			debitEntry.setType(EntryType.DEBIT);
			debitEntry.setAmount(nolAmount.doubleValue());
			debitEntry.setDate(date_of_transaction);
			debitEntry.setVendor_client("Canada Revenue Agency");
			
			TransactionEntry taxSavingEntry = new TransactionEntry();
			taxSavingEntry.setAccount(taxSavingAccount);
			taxSavingEntry.setDate(date_of_transaction);
			taxSavingEntry.setAmount(nolAmount.doubleValue());
			taxSavingEntry.setType(EntryType.CREDIT);
			taxSavingEntry.setVendor_client("Canada Revenue Agency");
			taxSavingEntry.setVendor_client("Canada Revenue Agency");
			
			// Add entries to transaction
			taxAdjustmentTransaction.addEntry(creditEntry);
			taxAdjustmentTransaction.addEntry(debitEntry);
			taxAdjustmentTransaction.addEntry(taxSavingEntry);
			return taxAdjustmentTransaction;
		}
		return null;
	}

	public Transaction generateIncomeTaxTransaction(double incomeTaxExpense,Date yearEndDate, Sequence seq) {
		// Calculate the income tax expense based on the year's net income

		// Get the date for the last day of the fiscal year

		// Create a new transaction
		Transaction taxTransaction = new Transaction(seq);
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
		gl.getLedger().postTransaction(taxTransaction);
		//gls.clearCashedLedger();

	}

}
