package com.example.springboot.accounting.service.ledger;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springboot.accounting.model.TransactionNature;
import com.example.springboot.accounting.model.entities.FixAccountInfo;
import com.example.springboot.accounting.model.entities.qb.Account;
import com.example.springboot.accounting.model.entities.qb.AccountManager;
import com.example.springboot.accounting.model.entities.qb.TransactionAccount;

@Service
public class CashStrategy implements TransactionStrategy {
	@Autowired
	AccountManager accountManager;
	
	@Autowired
	FixAccountInfo info;
	
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
	private void handleCash(String message, String account, Double balence, double amount, Account checkingAccount,
			TransactionAccount cardinality, List<String> words) {
		System.err.println();
		if(message.contains("Owner Initial deposit")) 
		{
//			Date       Account Title         Debit     Credit
//			---------- --------------------  -------   -------
//			XX/XX/XXXX Checking Account      600
//			           Owner's Contributions           600
			String vendor = "Self";
			Account check = getCheckingAccount();
			cardinality.debited = check;
			cardinality.setCredited(getAccountByName("Owner's Contributions"));
			cardinality.credited_balence = balence;
			cardinality.amount = Math.abs(amount);
			cardinality.vendor_client = vendor;

		}
	}

	private Account getAccountByName(String string) {
		return this.accountManager.getAccountByName(string);
	}

	private Account getCheckingAccount() {
		return this.accountManager.getAccountByAccountNo(info.checkingAccount.accountNo);
	}

	@Override
	public void populate(double amount, Account transactionAccount, TransactionAccount cardinality, TransactionNature type,
			Double balence) {
		//handleCash(message,transactionAccount,balence,amount,cardinality)
		
	}
}
