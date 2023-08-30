package com.example.springboot.accounting.service;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springboot.accounting.model.IncomeStatementWhiteBoard;
import com.example.springboot.accounting.model.dto.IncomeStatementDto;
import com.example.springboot.accounting.model.entities.qb.Account;
import com.example.springboot.accounting.model.entities.qb.AccountManager;
import com.example.springboot.accounting.model.entities.qb.Transaction;
import com.example.springboot.accounting.model.entities.qb.TransactionEntry;
import com.example.springboot.accounting.service.FiscalYearService.DateBoundaries;

@Service
public class IncomeStatementFromLedgerService {

	@Autowired
	private FinancialStatementLineFactory fsf;

	@Autowired
	private FiscalYearService fys;

	@Autowired
	private AccountManager accountManager;

	@Autowired
	GeneralLedgerService gls;

	

	public IncomeStatementWhiteBoard getWhiteBoard() {
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
				if(fyAccount.isTaxable()) 
				{
					wb.operatingExpensesAccounts.add(fyAccount);
				}
				else 
				{
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

	Map<Integer, IncomeStatementWhiteBoard> wbBoards = new HashMap<Integer, IncomeStatementWhiteBoard>();
	Set<Transaction> temp_transactions = new HashSet<Transaction>();

	public void populateMap() {

		for (int fiscal_year = 2015; fiscal_year < 2026; fiscal_year++) {
			IncomeStatementWhiteBoard wb = wbBoards.get(fiscal_year);
			if(null == wb) 
			{
				wb = getWhiteBoard();
				wb.fiscal_year = fiscal_year;
				wb.boundaries = fys.getBoundaries(fiscal_year);
				wbBoards.put(fiscal_year, wb);
			}
					
		}
		Set<Transaction> transactions = this.gls.getLedger().getTransactions();
		for (Transaction t : transactions) {
			if (t.getDate() != null) {
				int fy = this.fys.getFiscalYear(t.getDate());
				IncomeStatementWhiteBoard wb = wbBoards.get(fy);
				if (null == wb) {
					System.err.println(fy);
				}
				Transaction fy_t = new Transaction(t);
				for (TransactionEntry entry : t.getEntries()) {
					Account account = wb.accountMap.get(entry.getAccount().getAccountNumber());
					if(null == account) 
					{
						System.err.println();
					}
					TransactionEntry fy_entry = new TransactionEntry(entry, account);
					wb.addEntry(fy_entry);
					fy_t.addEntry(fy_entry);
				}
				wb.getTransactions().add(fy_t);
				temp_transactions.add(fy_t);
			} else {
				System.err.println();
			}
		}

		for (Transaction transaction : temp_transactions) {
			transaction.post();
//			TransactionEntry e = wbBoards.get(2023).entries.get(UUID.fromString("185d782e-a90c-47c1-8b20-be2144e742f1"));
//			System.err.println(e.getVendor_client());
		}

	}

	/**
	 * 
	 * @param allTransactions
	 * @param startDate
	 * @param endDate
	 * @param wb
	 * @return
	 */
	public IncomeStatementDto generateIncomeStatement(Set<Transaction> allTransactions, Date startDate, Date endDate,
			IncomeStatementWhiteBoard wb) {
		IncomeStatementDto incomeStatement = new IncomeStatementDto();
		incomeStatement.wb=wb;

//		// Filter transactions based on the fiscal year
//		List<Transaction> filteredTransactions = allTransactions.stream()
//				.filter(t -> (t.getDate().after(startDate) || t.getDate().equals(startDate))
//						&& (t.getDate().before(endDate) || t.getDate().equals(endDate)))
//				.collect(Collectors.toList());
//
//		// Iterate through filtered transactions to populate the maps
//		for (Transaction transaction : filteredTransactions) {
//			Transaction fy_t = new Transaction(transaction);
//			for (TransactionEntry entry : transaction.getEntries()) {
//				Account account = wb.accountMap.get(entry.getAccount().getAccountNumber());
//				TransactionEntry fy_entry = new TransactionEntry(entry, account);
//				fy_t.addEntry(fy_entry);
//			}
//			wb.fy_transactions.add(fy_t);
//		}
//
//		for (Transaction transaction : wb.fy_transactions) {
//			transaction.post();
//		}

		// Set the maps to the incomeStatement object
		incomeStatement.setRevenueAccounts(wb.revenueAccounts);
		incomeStatement.setOperatingExpenseAccounts(wb.operatingExpensesAccounts);
		incomeStatement.setOtherExpenseAccounts(wb.otherExpensesAccounts);

		// Calculate totals
		incomeStatement.setTotalRevenue(wb.revenueAccounts.stream().mapToDouble(Account::getBalance).sum());
		incomeStatement.setTotalOperatingExpenses(wb.operatingExpensesAccounts.stream().mapToDouble(Account::getBalance).sum());
		incomeStatement.setTotalOtherExpenses(wb.operatingExpensesAccounts.stream().mapToDouble(Account::getBalance).sum());
		
		//incomeStatement.setNetIncome(incomeStatement.getTotalRevenue().subtract(incomeStatement.getTotalExpenses()));

		return incomeStatement;
	}

	/**
	 * 
	 * @param fiscal_year
	 * @return
	 */
	public IncomeStatementDto generateIncomeStatement(int fiscal_year) {
		Set<Transaction> transactions = this.gls.getLedger().getTransactions();
		populateMap();
		IncomeStatementWhiteBoard wb = wbBoards.get(fiscal_year);
		DateBoundaries boundaries = fys.getBoundaries(fiscal_year);		
		return generateIncomeStatement(transactions, boundaries.date_start, boundaries.date_end, wb);
	}

}
