package com.sam.accounting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sam.accounting.model.IncomeStatementWhiteBoard;
import com.sam.accounting.model.entities.qb.Account;
import com.sam.accounting.model.entities.qb.AccountManager;
@Service
public class IncomeStatementWhiteBoardFactory {
	
	@Autowired
	public
	AccountManager accountManager;
	
	public IncomeStatementWhiteBoard makeWhiteBoard() {
		IncomeStatementWhiteBoard wb = new IncomeStatementWhiteBoard();

		// Get all accounts from the AccountManager
		wb.allAccounts = accountManager.getAccounts();

		if (wb.allAccounts.isEmpty()) {
			System.err.println();
		}
		// Create a map of accounts by their names
		for (Account account : wb.allAccounts) {
			Account fyAccount = new Account(account);
			wb.accountMap.put(account.getAccountNumber(), fyAccount);
			switch (account.getAccountType()) {
			case REVENUE:
				wb.revenueAccounts.add(fyAccount);
				break;
			case ASSET:
				wb.assetAccounts.add(fyAccount);
				break;
			case EQUITY:
				wb.equityAccounts.add(fyAccount);
				break;
			case EXPENSE:
				wb.expensesAccounts.add(fyAccount);
				if (fyAccount.isTaxable()) {
					wb.operatingExpensesAccounts.add(fyAccount);
				} else {
					wb.otherExpensesAccounts.add(fyAccount);
				}
				break;
			case LIABILITY:
				wb.liabilityAccounts.add(fyAccount);
				break;
			}
		}

		return wb;
	}
}
