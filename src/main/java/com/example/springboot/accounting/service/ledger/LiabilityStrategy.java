package com.example.springboot.accounting.service.ledger;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springboot.accounting.model.TransactionNature;
import com.example.springboot.accounting.model.entities.qb.Account;
import com.example.springboot.accounting.model.entities.qb.AccountManager;
import com.example.springboot.accounting.model.entities.qb.ClassificationRule;
import com.example.springboot.accounting.model.entities.qb.TransactionAccount;

@Service
public class LiabilityStrategy implements TransactionStrategy {
	
	private static final String Owner = "Samuel Audet-Arsenault";
	private ClassificationRule taxes_rule;
	@Autowired
	AccountManager accountManager;
	/**
	 * 
	 * @param message
	 * @param account
	 * @param balence
	 * @param amount
	 * @param checkingAccount
	 * @param cardinality
	 * @param words
	 */
	private void handleLiability(String message, String account, Double balence, double amount, Account checkingAccount,
			TransactionAccount cardinality, List<String> words) {
		if (this.taxes_rule.keyWordFount(account, words)) {
			switch (account) {
			case "1":
//					+----------+------------------+---------------------+----------------+---------+---------+
//					|   Date   | Account Type     | Account Name        | Vendor/Client  |  Debit  | Credit  |
//					+----------+------------------+---------------------+----------------+---------+---------+
//					| 1-May-23 | ASSET            | Loan to Owner       | Owner's Name   | 5,000   |   -     |
//					| 1-May-23 | LIABILITY        | Income Tax Payable  | ARC            |   -     | 5,000   |
//					+----------+------------------+---------------------+----------------+---------+---------+

				cardinality.vendor_client_from = Owner;
				cardinality.vendor_client_to = "ARC";
				populateLoanPaymentViaTaxRefund(amount, cardinality);

				break;
			default:
				populateTax(amount, checkingAccount, cardinality, balence);
				break;
			}

		} else {
//				+----------+------------------+---------------------+----------------+---------+---------+
//				|   Date   | Account Type     | Account Name        | Vendor/Client  |  Debit  | Credit |
//				+----------+------------------+---------------------+----------------+---------+---------+
//				| 1-May-23 | ASSET            | Loan to Owner       | Owner's Name   | 5,000   |   -    |
//				| 1-May-23 | LIABILITY        | Income Tax Payable  | ARC            |   -     | 5,000  |
//				+----------+------------------+---------------------+----------------+---------+---------+
			if (message.contains("Cloutier & Longtin")) {
				cardinality.vendor_client_from = Owner;
				cardinality.vendor_client_to = "Cloutier & Longtin";
				populateLoanRefundViaPayable(amount, cardinality);
			} else if (message.contains("CPA")) {
				cardinality.vendor_client_from = Owner;
				cardinality.vendor_client_to = "MTA";
				populateLoanRefundViaPayable(amount, cardinality);
			} else if (message.contains("VISA")) {
				cardinality.vendor_client_from = Owner;
				cardinality.vendor_client_to = "VISA";
				populateLoanRefundViaCredit(amount, cardinality);
			} else {
				populateToClassify(amount, checkingAccount, cardinality, balence);
			}

		}
	}
	
	private void populateLoanPaymentViaTaxRefund(double amount, TransactionAccount cardinality) {
		populateLoanRefundViaLiability(amount, cardinality, "Taxes Payable");
	}

	private void populateLoanRefundViaCredit(double amount, TransactionAccount cardinality) {
		populateLoanRefundViaLiability(amount, cardinality, "VISA_TD_REMISES_AFFAIRES");
	}

	private void populateLoanRefundViaPayable(double amount, TransactionAccount cardinality) {
		populateLoanRefundViaLiability(amount, cardinality, "Accounts Payable");
	}
	
//	+------------+----------+-----------------------+-----+------------------+----------+----------+
//	| Date       | Type     | Name                  | No  | Vendor/Client    |  Debit   | Credit   |
//	+------------+----------+-----------------------+-----+------------------+----------+----------+
//	| 30/12/2022 | EXPENSE  | Income Tax Expense    | 025 | Revenue Agency   | $5,000.00|   -      |
//	| 30/12/2022 | LIABILITY| Taxes Payable         | 026 | Revenue Agency   |   -      | $5,000.00|
//	+------------+----------+-----------------------+-----+------------------+----------+----------+

//	When the tax is actually paid:
//	+------------+----------+-----------------------+-----+------------------+----------+----------+
//	| 30/01/2023 | LIABILITY| Taxes Payable         | 027 | Revenue Agency   | $5,000.00|   -      |
//	| 30/01/2023 | ASSET    | Checking Account      | 028 | Revenue Agency   |   -      | $5,000.00|
//	+------------+----------+-----------------------+-----+------------------+----------+----------+
	/**
	 * 
	 * @param amount
	 * @param checkingAccount
	 * @param cardinality
	 * @param balence
	 */
	private void populateTax(double amount, Account checkingAccount, TransactionAccount cardinality, Double balence) {

		TransactionAccount incomeTaxPayment = new TransactionAccount();

		String vendor = "Revenue Agency";
		Account payable = getAccountByName("Taxes Payable");
		incomeTaxPayment.debited = payable;
		incomeTaxPayment.setCredited(checkingAccount);
		incomeTaxPayment.credited_balence = balence;
		incomeTaxPayment.amount = Math.abs(amount);
		incomeTaxPayment.vendor_client = vendor;

		cardinality.split = new ArrayList<TransactionAccount>();
		// cardinality.split.add(incomeTaxInvoice);
		cardinality.split.add(incomeTaxPayment);
	}

	private Account getAccountByName(String string) {
		return accountManager.getAccountByName(string);
	}
	
	/**
	 * 
	 * @param amount
	 * @param cardinality
	 * @param liability
	 */
	private void populateLoanRefundViaLiability(double amount, TransactionAccount cardinality, String liability) {
		cardinality.setCredited(getAccountByName("Loan to Owner"));
		cardinality.debited = accountManager.getAccountByName(liability);
		cardinality.amount = Math.abs(amount);
	}
	
	/**
	 * 
	 * @param amount
	 * @param checkingAccount
	 * @param cardinality
	 * @param balence
	 */
	private void populateToClassify(double amount, Account checkingAccount, TransactionAccount cardinality,
			Double balence) {
		// When the tax is actually paid:
		// +------------+-------------+-----------------------+-----+------------------+----------+----------+
		// | Date | Account Type| Account Name | | Vendor/Client | Debit | Credit |
		// +------------+-------------+-----------------------+-----+------------------+----------+----------+
		// | 30/01/2023 | EXPENSE | To Classify | 027 | Revenue Agency | $5,000.00| - |
		// | 30/01/2023 | ASSET | Checking Account | 028 | Revenue Agency | - |
		// $5,000.00|
		// +------------+-------------+-----------------------+-----+------------------+----------+----------+
		cardinality.debited = accountManager.getAccountByName("To Classify");
		cardinality.vendor_client = "self";
		cardinality.setCredited(checkingAccount);
		cardinality.credited_balence = balence;
		cardinality.amount = Math.abs(amount);
	}

	@Override
	public void populate(double amount, Account transactionAccount, TransactionAccount cardinality, TransactionNature type,
			Double balence) {
		// TODO Auto-generated method stub
		
	}
}
