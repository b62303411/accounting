package com.sam.accounting.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sam.accounting.model.IncomeStatementWhiteBoard;
import com.sam.accounting.model.NOLRecord;
import com.sam.accounting.model.Sequence;
import com.sam.accounting.model.dto.IncomeStatementDto;
import com.sam.accounting.model.entities.Rates;
import com.sam.accounting.model.entities.qb.Account;
import com.sam.accounting.model.entities.qb.Ledger;
import com.sam.accounting.model.entities.qb.Transaction;
import com.sam.accounting.model.entities.qb.TransactionEntry;
import com.sam.accounting.service.FiscalYearService.DateBoundaries;

@Service
public class IncomeStatementFromLedgerService implements IBoardRepository {

	@Autowired
	private FiscalYearService fys;
	
	@Autowired
	private SimplifiedSalesTaxesStrategy simplifiedSalesTaxesStrategy;

	@Autowired
	GeneralLedger gl;

	@Autowired
	SmallBusinessTaxRateService smallBusinessTaxRateService;

	@Autowired
	TaxService taxesService;

	@Autowired
	TaxAdjustmentService taxAdjustmentService;

	@Autowired
	IncomeStatementWhiteBoardFactory iswbf;
	
	Map<Integer, IncomeStatementWhiteBoard> wbBoards = new HashMap<Integer, IncomeStatementWhiteBoard>();

	HashMap<Integer, Set<Transaction>> temp_transactions_per_year = new HashMap<Integer, Set<Transaction>>();
	Set<Transaction> temp_transactions = new HashSet();

	Map<Integer, NOLRecord> nolLedger = new HashMap<>();

	public IncomeStatementWhiteBoard getWhiteBoard() {
		return iswbf.makeWhiteBoard();
	}

	public void populateMap() {
		Ledger ledger = gl.getLedger();
		for (int fiscal_year = 2014; fiscal_year < 2026; fiscal_year++) {
			IncomeStatementWhiteBoard wb = wbBoards.get(fiscal_year);
			if (null == wb) {
				wb = getWhiteBoard();
				wb.fiscal_year = fiscal_year;
				wb.boundaries = fys.getBoundaries(fiscal_year);
				wbBoards.put(fiscal_year, wb);
				temp_transactions_per_year.put(fiscal_year, new HashSet<Transaction>());
			}
		}
		Sequence seq = ledger.getSeq();
		Set<Transaction> transactions = ledger.getTransactions();
		for (Transaction t : transactions) {
			if (t.getDate() != null) {
				translateTransaction(t);
			} else {
				System.err.println();
			}
		}

		postTempTransactions();

		IRunner generateSalesTaxesRun = new IRunner() {
			@Override
			public void run(int fiscal_year, IncomeStatementWhiteBoard wb, Date endDate) {
				generateSalesTaxesBalenceTransactions(fiscal_year, wb, endDate,seq);

			}
		};

		FiscalStrategyRunner runner = new FiscalStrategyRunner(generateSalesTaxesRun, this);
		runner.run();
		
		postTempTransactions();
		// ---------------------------------------------------------------
		// Handle Tax Expense.
		// Record Losses.
		// ---------------------------------------------------------------
		for (int fiscal_year = 2014; fiscal_year < 2025; fiscal_year++) {
			IncomeStatementWhiteBoard wb = wbBoards.get(fiscal_year);
			double tot = wb.getTotalRevenue();
			double op = wb.getTotalOperatingExpenses();
			wb.setBeforeTaxIncome(tot - op);
			Date endDate = fys.getBoundaries(fiscal_year).date_end;

			if (wb.beforeTaxIncome > 0) {
				BigDecimal totalTaxes = calculateIncomeTax(BigDecimal.valueOf(wb.beforeTaxIncome), endDate);
				wb.setTotalTaxes(totalTaxes.doubleValue());
				Transaction t = taxAdjustmentService.generateIncomeTaxTransaction(totalTaxes.doubleValue(), endDate,
						seq);
				executeTaxAdjustment(wb, t);
			} else if (wb.beforeTaxIncome < 0) {
				NOLRecord record = new NOLRecord(Math.abs(wb.beforeTaxIncome));
				record.endOfYear = wb.boundaries.date_end;
				record.year = wb.fiscal_year;
				// Set other attributes like incurred and expiry dates
				nolLedger.put(fiscal_year, record);
			}

		}

		Set<Entry<Integer, NOLRecord>> list = nolLedger.entrySet();
		int startYear = 2014;
		// Loop over each NOL Record
		for (Entry<Integer, NOLRecord> entry : list) {
			int nolYear = entry.getKey();
			NOLRecord nol = entry.getValue();
			double availableLoss = nol.getValue();
			if (availableLoss <= 0)
				continue;
			// Try to apply to the past years, starting from 3 years ago
			for (int applyYear = nolYear - 3; applyYear <= nolYear - 1; applyYear++) {
				if (applyYear < startYear)
					continue; // Skip years before your data starts
				availableLoss = test(applyYear, startYear, nol, availableLoss, seq);
				// Break if no more loss is available
				if (availableLoss <= 0) {
					break;
				}
			}
		}
	}

	private void postTempTransactions() {
		for (Transaction transaction : temp_transactions) {
			transaction.post();
//			TransactionEntry e = wbBoards.get(2023).entries.get(UUID.fromString("185d782e-a90c-47c1-8b20-be2144e742f1"));
//			System.err.println(e.getVendor_client());
		}
	}

	/**
	 * 
	 * @param applyYear
	 * @param startYear
	 * @param nol
	 * @param availableLoss
	 * @param seq
	 * @return
	 */
	public double test(int applyYear, int startYear, NOLRecord nol, double availableLoss, Sequence seq) {

		IncomeStatementWhiteBoard wb = wbBoards.get(applyYear);
		Date endOfYearDateOfRecordedLosses = nol.endOfYear;
		Date endOfYearDateOfTheAdjustedYear = wb.boundaries.date_end;
		if (wb != null && wb.adjustedBeforeTaxIncome > 0) {

			double originalValue = wb.adjustedBeforeTaxIncome;

			// Calculate how much of the loss can be used
			double maxSubtractable = Math.min(availableLoss, originalValue);

			// Update the before tax;
			wb.adjustedBeforeTaxIncome -= maxSubtractable;

			// Calculate the income tax on the adjusted amount.
			BigDecimal adjusted_taxes = calculateIncomeTax(BigDecimal.valueOf(wb.adjustedBeforeTaxIncome),
					endOfYearDateOfTheAdjustedYear);

			BigDecimal previously_paid_taxes = BigDecimal.valueOf(wb.adjustedIncomeTax);

			// reduce available losses.
			availableLoss -= maxSubtractable;

			// calculate the amount of the tax deduction.
			BigDecimal amount_of_tax_deduction = previously_paid_taxes.subtract(adjusted_taxes);

			// create the deduction transaction.
			Transaction t = taxAdjustmentService.createTaxAdjustmentForNOL(amount_of_tax_deduction,
					endOfYearDateOfRecordedLosses, endOfYearDateOfTheAdjustedYear, seq);
			executeTaxAdjustment(wb, t);

			// update the paid taxes.
			wb.adjustedIncomeTax -= amount_of_tax_deduction.doubleValue();

			// Update NOL Record
			nol.setValue(availableLoss);

		}
		return availableLoss;
	}

	private void generateSalesTaxesBalenceTransactions(int fiscal_year, IncomeStatementWhiteBoard wb, Date endDate, Sequence seq) {
		Account salesRevenueAccount = wb.getAccountByNo("R001");
		Account collectedTaxAccount = wb.getAccountByNo("L004");
		double sales_revenue = salesRevenueAccount.getBalance();
		double collected_taxes=collectedTaxAccount.getBalance();
		if(sales_revenue >0) {
		Transaction t = simplifiedSalesTaxesStrategy.recordQuickMethodRemittance(sales_revenue,collected_taxes, endDate, fiscal_year, seq);
		executeTaxAdjustment(wb, t);
		System.out.println(t);}
	}

	private void executeTaxAdjustment(IncomeStatementWhiteBoard wb, Transaction t) {
		if (!temp_transactions.contains(t)) {
			if (!wb.hasTransaction(t)) {
				taxAdjustmentService.postTransactionToLedger(t);				
				Transaction please_post = translateTransaction(t);
				temp_transactions.add(please_post);
				please_post.post();
			} else {
				System.err.println();
			}
		}
	}

	/**
	 * Convert the transaction to be only locally executed.
	 * 
	 * @param t
	 * @return
	 */
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

	private void addTransaction(Integer fy, Transaction fy_t) {
		temp_transactions_per_year.get(fy).add(fy_t);
		temp_transactions.add(fy_t);
	}

	public BigDecimal calculateIncomeTax(BigDecimal revenueBeforeTaxes, Date endDate) {
		Rates rate = smallBusinessTaxRateService.getRates(endDate);
		BigDecimal taxes_fed = revenueBeforeTaxes.multiply(BigDecimal.valueOf(rate.getFederal() / 100));
		BigDecimal taxes_prov = revenueBeforeTaxes.multiply(BigDecimal.valueOf(rate.getProvintial() / 100));
		BigDecimal totalTaxes = taxes_fed.add(taxes_prov);
		return totalTaxes;
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
		incomeStatement.wb = wb;

		// Set the maps to the incomeStatement object
		incomeStatement.setRevenueAccounts(wb.revenueAccounts);
		incomeStatement.setOperatingExpenseAccounts(wb.operatingExpensesAccounts);
		incomeStatement.setOtherExpenseAccounts(wb.otherExpensesAccounts);

		// Calculate totals
		incomeStatement.setTotalRevenue(wb.getTotalRevenue());
		incomeStatement.setTotalOperatingExpenses(wb.getTotalOperatingExpenses());
		incomeStatement.setTotalOtherExpenses(wb.getTotalOtherExpenses());

		BigDecimal revenueBeforeTaxes = incomeStatement.getTotalRevenue()
				.subtract(incomeStatement.getTotalOperatingExpenses());

		incomeStatement.setIncomeBeforeTax(revenueBeforeTaxes);
		if (revenueBeforeTaxes.doubleValue() > 0) {
			BigDecimal totalTaxes = calculateIncomeTax(revenueBeforeTaxes, endDate);
			incomeStatement.incomeTax = totalTaxes;

			incomeStatement.incomeAfterTax = revenueBeforeTaxes.subtract(incomeStatement.incomeTax);
		} else {
			incomeStatement.incomeTax = BigDecimal.ZERO;
			incomeStatement.incomeAfterTax = revenueBeforeTaxes;
		}
		return incomeStatement;
	}

	/**
	 * 
	 * @param fiscal_year
	 * @param seq
	 * @return
	 */
	public IncomeStatementDto generateIncomeStatement(int fiscal_year, Sequence seq) {
		Set<Transaction> transactions = this.gl.getLedger().getTransactions();
		populateMap();
		IncomeStatementWhiteBoard wb = wbBoards.get(fiscal_year);
		DateBoundaries boundaries = fys.getBoundaries(fiscal_year);
		return generateIncomeStatement(transactions, boundaries.date_start, boundaries.date_end, wb);
	}

	@Override
	public IncomeStatementWhiteBoard getBoard(int fiscal_year) {
		return wbBoards.get(fiscal_year);
	}

	@Override
	public DateBoundaries getBoundaries(int fiscal_year) {

		return fys.getBoundaries(fiscal_year);
	}

}
