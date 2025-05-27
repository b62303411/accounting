package com.example.springboot.accounting.service;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springboot.accounting.model.IncomeStatementWhiteBoard;
import com.example.springboot.accounting.model.Sequence;
import com.example.springboot.accounting.model.entities.qb.Account;
import com.example.springboot.accounting.model.entities.qb.Ledger;
import com.example.springboot.accounting.model.entities.qb.Transaction;
import com.example.springboot.accounting.model.entities.qb.TransactionEntry;
import com.example.springboot.accounting.service.FiscalYearService.DateBoundaries;

@Service
public class SimplifiedSalesTaxesService implements IBoardRepository {
	@Autowired
	public  SimplifiedSalesTaxesStrategy simplifiedSalesTaxesStrategy;

	@Autowired
	public FiscalYearService fys;
	
	@Autowired
	public IncomeStatementWhiteBoardFactory iswbf;
	
	private Map<Integer, IncomeStatementWhiteBoard> wbBoards = new HashMap<Integer, IncomeStatementWhiteBoard>();

	HashMap<Integer, Set<Transaction>> temp_transactions_per_year = new HashMap<Integer, Set<Transaction>>();
	
	Set<Transaction> temp_transactions = new HashSet();

	public Ledger ledger;
	
	public void run(Sequence seq,Set<Transaction> transactions, Ledger l) {
		
		this.ledger= l;
		populateWhiteBoards();	
		
		translateTransactions(transactions);	
		
		postTempTransactions();
		
		IRunner generateSalesTaxesRun = new IRunner() {
			@Override
			public void run(int fiscal_year, IncomeStatementWhiteBoard wb, Date endDate) {
				generateSalesTaxesBalenceTransactions(fiscal_year, wb, endDate, seq);

			}
		};

		FiscalStrategyRunner runner = new FiscalStrategyRunner(generateSalesTaxesRun, this);
		runner.run();
	}

	private void translateTransactions(Set<Transaction> transactions) {
		for (Transaction t : transactions) {
			if (t.getDate() != null) {
				translateTransaction(t);
			} else {
				System.err.println();
			}
		}
	}

	private void populateWhiteBoards() {
		for (int fiscal_year = 2014; fiscal_year < 2026; fiscal_year++) {
			IncomeStatementWhiteBoard wb = wbBoards.get(fiscal_year);
			if (null == wb) {
				wb = iswbf.makeWhiteBoard();
				wb.fiscal_year = fiscal_year;
				wb.boundaries = fys.getBoundaries(fiscal_year);
				wbBoards.put(fiscal_year, wb);
				temp_transactions_per_year.put(fiscal_year, new HashSet<Transaction>());
			}
		}
	}

	@Override
	public IncomeStatementWhiteBoard getBoard(int fiscal_year) {
		return wbBoards.get(fiscal_year);
	}

	@Override
	public DateBoundaries getBoundaries(int fiscal_year) {
		return fys.getBoundaries(fiscal_year);
	}

	private void generateSalesTaxesBalenceTransactions(int fiscal_year, IncomeStatementWhiteBoard wb, Date endDate,
			Sequence seq) {
		Account salesRevenueAccount = wb.getAccountByNo("R001");
		Account collectedTaxAccount = wb.getAccountByNo("L004");
		double sales_revenue = salesRevenueAccount.getBalance();
		double collected_taxes = collectedTaxAccount.getBalance();
		if (sales_revenue > 0) {
			Transaction t = simplifiedSalesTaxesStrategy.recordQuickMethodRemittance(sales_revenue, collected_taxes,
					endDate, fiscal_year, seq);
			executeTaxAdjustment(wb, t);
			System.out.println(t);
		}
	}

	private void executeTaxAdjustment(IncomeStatementWhiteBoard wb, Transaction t) {
		if (!temp_transactions.contains(t)) {
			if (!wb.hasTransaction(t)) {
				postTransactionToLedger(t);
				Transaction please_post = translateTransaction(t);
				temp_transactions.add(please_post);
				please_post.post();
			} else {
				System.err.println();
			}
		}
	}
	
	private void postTransactionToLedger(Transaction t) {
		ledger.postTransaction(t);
		//gls.clearCashedLedger();
		
	}

	private Transaction translateTransaction(Transaction t) {
		int fy = this.fys.getFiscalYear(t.getDate());
		IncomeStatementWhiteBoard wb = wbBoards.get(fy);
		if (null == wb) {
			System.err.println(fy);
		}
		Transaction fy_t = new Transaction(t);
		for (TransactionEntry entry : t.getEntries()) {
			Account account = wb.accountMap.get(entry.getAccount().getAccountNumber());
			TransactionEntry fy_entry = new TransactionEntry(entry, account);
			wb.addEntry(fy_entry);
			fy_t.addEntry(fy_entry);

		}
		wb.getTransactions().add(fy_t);
		addTransaction(fy, fy_t);
		return fy_t;
	}

	private void addTransaction(int fy, Transaction fy_t) {
		temp_transactions_per_year.get(fy).add(fy_t);
		temp_transactions.add(fy_t);
	}
	private void postTempTransactions() {
		for (Transaction transaction : temp_transactions) {
			transaction.post();
//			TransactionEntry e = wbBoards.get(2023).entries.get(UUID.fromString("185d782e-a90c-47c1-8b20-be2144e742f1"));
//			System.err.println(e.getVendor_client());
		}
	}
}
