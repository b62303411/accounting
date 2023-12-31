package com.example.springboot.accounting.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springboot.accounting.model.Sequence;
import com.example.springboot.accounting.model.dto.ExpensesLine;
import com.example.springboot.accounting.model.dto.FinancialStatementLine;
import com.example.springboot.accounting.model.dto.IncomeStatementDto;
import com.example.springboot.accounting.model.dto.RevenueLine;
import com.example.springboot.accounting.model.entities.Expense;
import com.example.springboot.accounting.model.entities.ExploitationExpense;
import com.example.springboot.accounting.model.entities.Transaction;
import com.example.springboot.accounting.repository.AmortisationLegRepository;
import com.example.springboot.accounting.repository.ExploitationExpenseRepository;
import com.example.springboot.accounting.repository.TransactionRepository;
import com.example.springboot.accounting.service.FiscalYearService.DateBoundaries;

@Service
public class IncomeStatementService {

	private final CompanyProfileService profile;

	private final TransactionRepository transactionRepository;
	
	private final FinancialStatementLineFactory fsf;
	
	private final ExploitationExpenseRepository exploitationExpenseRepo;
	
	private final GeneralLedgerService gls;
	
	private ExpensesService expenseService;

	private final FiscalYearService fys;

	private IncomeStatementFromLedgerService isfls;
	

	
	@Autowired
	public IncomeStatementService(
			IncomeStatementFromLedgerService isfls,
			FiscalYearService fys,
			GeneralLedgerService gls,
			ExpensesService expenseService,
			AmortisationLegRepository legRepository,
			ExploitationExpenseRepository exploitationExpenseRepo, CompanyProfileService profile,
			FinancialStatementLineFactory fsf, TransactionRepository transactionRepository,
			TransactionService transactionService, AssetService assetServices) {
		this.fys= fys;
		this.exploitationExpenseRepo = exploitationExpenseRepo;
		this.transactionRepository = transactionRepository;
		this.expenseService = expenseService;
		this.gls= gls;
		this.fsf = fsf;
		this.profile = profile;
		this.isfls=isfls;


	}

	/**
	 * 
	 * @param start
	 * @param stop
	 * @return
	 */
	private FinantialStatement getFinantialStatement(Date start, Date stop) {
		FinantialStatement statement = new FinantialStatement();
		statement.revenue = getRevenue(start, stop);
		statement.setCogs(getCogs(start, stop));
		statement.getGrossProfit();
		statement.setOperatingExpenses(getOperatingExpenses(start, stop));

		statement.setOtherExpenses(getOtherExpenses(start, stop));
		// Small-Business Tax Rate: 3.2% (Quebec portion) + 9% (Federal portion) = 12.2%
		// combined rate
		Double operatingIncome = statement.getOperatingIncome();
		if (operatingIncome > 0) {
			double incomeTax = operatingIncome * 12.2 / 100;
			statement.setIncomeTax(incomeTax);
			statement.setQuebecTax(operatingIncome * 3.2 / 100);
			statement.setFederalTax(operatingIncome * 9 / 100);
		} else {
			statement.setIncomeTax(0.0);
			statement.setQuebecTax(0.0);
			statement.setFederalTax(0.0);
		}
		statement.otherRevenue = getOtherRevenue(start, stop);
		return statement;
	}

	private Double getOtherRevenue(Date start, Date stop) {
		Double value = transactionRepository.getOtherIncomesForFiscalYear(start, stop);
		if (null == value)
			return 0.0;
		else
			return value;
	}

	private Double getOtherExpenses(Date start, Date stop) {
		Double value = transactionRepository.getOtherExpensesBetween(start, stop);
		if (null == value)
			return 0.0;
		else
			return value;

	}

	private Double getOperatingExpenses(Date start, Date stop) {
		return getTotalOperatingExpensesBetween(start, stop);
		// return transactionRepository.getOperatingExpensesForFiscalYear(start, stop);
	}

	private Double getTotalOperatingExpensesBetween(Date start, Date stop) {
		List<ExpensesLine> expenses = getExpensesLinesBetween(start, stop);
		double grossAmount = 0;
		for (ExpensesLine revenueLine : expenses) {
			grossAmount += revenueLine.getAmount();
		}
		return grossAmount;
	}

	private Double getCogs(Date start, Date stop) {
		return transactionRepository.getCOGSForFiscalYear(start, stop);
	}



	private class TaxBreakDown {
		double grossAmount;
		double netAmount;
		double taxes;

	}

	private TaxBreakDown breakDownAmount(double grossAmount) {
		TaxBreakDown tbd = new TaxBreakDown();
		double taxRate = 0.14975; // combined TPS and TVQ rate
		tbd.grossAmount = grossAmount;
		tbd.netAmount = grossAmount / (1 + taxRate); // this is the amount to be recorded as sales revenue
		tbd.taxes = grossAmount - tbd.netAmount; // this is the amount to be remitted to the government

		return tbd;
	}

	private Double getRevenue(Date start, Date stop) {
		Double grossAmount = transactionRepository.getSalesRevenueForFiscalYear(start, stop);
		if (null == grossAmount)
			return 0.0;
		// the gross amount including TPS and TVQ

		TaxBreakDown tbd = breakDownAmount(grossAmount);

		return tbd.netAmount;
	}

	public FinantialStatement getFinantialStatement(Integer year) {
		FinantialStatement statement = new FinantialStatement();
		statement.revenue = getRevenue(year);
		statement.setCogs(getCogs(year));
		statement.getGrossProfit();
		statement.setOperatingExpenses(getOperatingExpenses(year));
		statement.getOperatingIncome();
		statement.setOtherExpenses(getOtherExpensesTotal(year));
		statement.otherRevenue = getOtherRevenue(year);
		return statement;
	}

	/**
	 * 
	 * @param year
	 * @return
	 */
	public List<FinancialStatementLine> getIncomeStatement(Integer year) {
		// List<FinancialStatementLine> transactions =
		// financeStatementService.getIncomeStatement(year);

		double pretaxIncome;
		double incomeTax;
		FinantialStatement statement = getFinantialStatement(year);

		List<FinancialStatementLine> lines = convertToFinLines(statement);
		return lines;
	}



	/**
	 * When referring to a fiscal year, it's generally named after the year in which
	 * it ends. For example, a fiscal year that runs from July 1, 2023, to June 30,
	 * 2024, would be fiscal year 2024 or FY2024.
	 * 
	 * @param year
	 * @return
	 */
	public List<FinancialStatementLine> getIncomeStatementForFiscalYear(Integer year) {
		// List<FinancialStatementLine> transactions =
		// financeStatementService.getIncomeStatement(year);
		DateBoundaries b = fys.getBoundaries(year);

		double incomeTax = 0;
		FinantialStatement statement = getFinantialStatement(b.date_start, b.date_end);

		List<FinancialStatementLine> lines = convertToFinLines(statement);
		return lines;
	}

	public double getTotalOperatingExpensesForFiscalYear(Integer year) {
		List<ExpensesLine> expenses = getExpensesForFiscalYear(year);
		double grossAmount = 0;
		for (ExpensesLine revenueLine : expenses) {
			grossAmount += revenueLine.getAmount();

		}
		return grossAmount;
	}



	public Map<String, Double> getExpenseReport(Integer year) {
		DateBoundaries b = fys.getBoundaries(year);
		return getExpenseReport(b.date_start, b.date_end);

	}

	/**
	 * 
	 * @param start
	 * @param stop
	 * @return
	 */
	public Map<String, Double> getExpenseReport(Date start, Date stop) {
		Map<String, Double> maps = new HashMap<>();
		List<Expense> list = getExpensesBetween(start, stop);

		for (Expense expense : list) {
			String type = expense.getTypeStr();
			double amount = expense.getTotalBeforeSalesTaxes();
			maps.put(type, maps.getOrDefault(type, 0.0) + amount); // Sum amounts by type
		}

		return maps;
//		Map<String, Double> maps = new HashMap<String, Double>();
//		List<Expense> list = getExpensesBetween(start, stop);
//		double FraisBancaires = 0;
//		double HonoraireProfessionel = 0;
//		double FraisDeplacement = 0;
//		double Fournitures = 0;
//		double Licences = 0;
//		for (Expense expense : list) {
//			switch (expense.getTypeStr()) {
//			case "FraisBancaires":
//				FraisBancaires += expense.getTransaction().getAmount();
//				break;
//			case "HonoraireProfessionel":
//				HonoraireProfessionel += expense.getTransaction().getAmount();
//				break;
//			case "FraisDeplacement":
//				FraisDeplacement += expense.getTransaction().getAmount();
//				break;
//			case "Fournitures":
//				Fournitures += expense.getTransaction().getAmount();
//				break;
//			case "Licences":
//				Licences += expense.getTransaction().getAmount();
//				break;
//			}
//		}
//		maps.put("FraisBancaires", FraisBancaires);
//		maps.put("HonoraireProfessionel", HonoraireProfessionel);
//		maps.put("FraisDeplacement", FraisDeplacement);
//		maps.put("Fournitures", Fournitures);
//		maps.put("Licences", Fournitures);
//		return maps;

	}

	/**
	 * 
	 * @param start
	 * @param stop
	 * @return
	 */
	public List<Expense> getExpensesBetween(Date start, Date stop) {
		List<ExploitationExpense> exploitationExpenses = exploitationExpenseRepo.findAllBetween(start, stop);
		List<Expense> expenses = exploitationExpenses.stream().map(expense -> (Expense) expense)
				.collect(Collectors.toList());

		return expenses;
	}

	/**
	 * 
	 * @param start
	 * @param stop
	 */
	public void inferExpensesFromTransactions(Date start, Date stop) {
		expenseService.inferExpensesFromTransactions(start, stop);
	}





	/**
	 * 
	 * @param start
	 * @param stop
	 * @return
	 */
	public List<ExpensesLine> getExpensesLinesBetween(Date start, Date stop) {

		//inferExpensesFromTransactions(start, stop);
		//inferExpensesFromAssetDepreciation(start);

		List<Expense> expenses = getExpensesBetween(start, stop);
		List<ExpensesLine> lines = getLines(expenses);

		return lines;
	}

	private List<ExpensesLine> getLines(List<Expense> expenses) {
		List<ExpensesLine> lines = new ArrayList<ExpensesLine>();
		for (Expense expense : expenses) {
			ExpensesLine line = new ExpensesLine();
			line.amount = expense.getTotal();
			line.date = expense.getDate();
			line.st = expense.getSalesTaxes();
			line.id = expense.getId();
			line.tbst = expense.getTotalBeforeSalesTaxes();
			line.description = expense.getDescription();
			line.expenseType = expense.getTypeStr();
			line.payee = expense.getPayee();
			lines.add(line);
		}
		return lines;
	}

	public List<ExpensesLine> getOtherExpensesBetween(Date start, Date stop) {

		List<Transaction> value = transactionRepository.getOtherExpensesTransactionsBetween(start, stop);
		List<ExpensesLine> lines = getLinesFromTransactions(value);
		return lines;
	}

	private List<ExpensesLine> getLinesFromTransactions(List<Transaction> value) {
		List<ExpensesLine> lines = new ArrayList<ExpensesLine>();
		for (Transaction transaction : value) {
			ExpensesLine line = new ExpensesLine();
			switch (transaction.getTransactionNature()) {
			case Credit:
				line.amount = Math.abs(transaction.getAmount());
				break;
			case Debit:
				line.amount = -Math.abs(transaction.getAmount());
				break;
			default:
				line.amount = transaction.getAmount();
			}
			ExploitationExpense ex = exploitationExpenseRepo.findByTransaction(transaction);
			line.expenseType = ex.getTypeStr();
			line.description = transaction.getDescription();
			line.date = transaction.getDate();
			line.id = ex.getId();
			lines.add(line);
		}
		return lines;
	}

	public List<ExpensesLine> getExpensesForFiscalYear(Integer year) {
		DateBoundaries b = fys.getBoundaries(year);
		return getExpensesLinesBetween(b.date_start, b.date_end);

	}

	public List<ExpensesLine> getOtherExpenses(Integer year) {
		DateBoundaries b = fys.getBoundaries(year);
		return getOtherExpensesBetween(b.date_start, b.date_end);
	}

	public List<RevenueLine> getRevenuesForFiscalYear(Integer year) {
		DateBoundaries b = fys.getBoundaries(year);
		List<Transaction> value = transactionRepository.getSalesTransactionsForFiscalYear(b.date_start, b.date_end);
		List<RevenueLine> lines = new ArrayList<RevenueLine>();
		for (Transaction transaction : value) {

			Double amount = transaction.getAmount();
			TaxBreakDown tbd = breakDownAmount(amount);
			Double tpsTvq = tbd.taxes;
			Double revenue = tbd.netAmount;
			String description = transaction.getDescription();
			Date date = transaction.getDate();
			RevenueLine line = new RevenueLine(amount, tpsTvq, revenue, description, date);
			lines.add(line);
		}

		return lines;
	}

	private List<FinancialStatementLine> convertToFinLines(FinantialStatement statement) {
		List<FinancialStatementLine> lines = new ArrayList<FinancialStatementLine>();
		FinancialStatementLine revenue_line = makeFs(statement.revenue, "Revenue");
		FinancialStatementLine cogs_line = makeFs(statement.cogs, "Cogs");
		FinancialStatementLine grossProfit_line = makeFs(statement.getGrossProfit(), "Gross Profit");
		FinancialStatementLine operatingExpenses_line = makeFs(statement.operatingExpenses, "Operating Expenses");
		FinancialStatementLine operatingIncome_line = makeFs(statement.getOperatingIncome(), "Operating Income");
		FinancialStatementLine otherExpenses_line = makeFs(statement.otherExpenses, "Other Expenses");
		FinancialStatementLine otherIncome_line = makeFs(statement.otherRevenue, "Other Revenue");
		FinancialStatementLine pretaxIncome_line = makeFs(statement.getPretaxIncome(), "Pretax Income");
		FinancialStatementLine incomeTax_line = makeFs(statement.incomeTax,
				"Income Tax Small-Business Tax Rate: 12.2%");
		FinancialStatementLine federalIncomeTax_line = makeFs(statement.getFederalTax(), "9% (Federal portion)");
		FinancialStatementLine provincialIncomeTax_line = makeFs(statement.getQuebecTax(), " 3.2% (Quebec portion)");
		FinancialStatementLine netIncome_line = makeFs(statement.getNetIncome(), "Net Income");
		lines.add(revenue_line);
		lines.add(cogs_line);
		lines.add(grossProfit_line);
		lines.add(operatingExpenses_line);
		lines.add(operatingIncome_line);
		lines.add(otherExpenses_line);
		lines.add(otherIncome_line);
		lines.add(pretaxIncome_line);
		lines.add(incomeTax_line);
		lines.add(federalIncomeTax_line);
		lines.add(provincialIncomeTax_line);

		lines.add(netIncome_line);
		return lines;
	}

	/**
	 * 
	 * @param statement
	 * @param value
	 * @param title
	 * @return
	 */
	private FinancialStatementLine makeFs(Double value, String title) {
		return fsf.makeFs(value, title);
	}

	/**
	 * Revenue: This is the total amount of money a company earns from its business
	 * activities, often categorized as operating revenue (from core business
	 * operations) and non-operating revenue (from non-core activities like
	 * investments).
	 */
	public Double getRevenue(Integer year) {
		Double revenue = transactionRepository.getSalesRevenueForYear(year);
		if (null == revenue)
			return 0.0;
		return revenue;
	}

	/**
	 * Cost of Goods Sold (COGS): This section includes the direct costs
	 * attributable to the production of the goods sold by a company. This includes
	 * material costs and direct labor costs.
	 */
	public Double getCogs(Integer year) {
		Double cogs = transactionRepository.getCOGSForYear(year);
		if (null == cogs)
			return 0.0;
		return cogs;
	}

	/**
	 * Operating Expenses: These are the costs associated with running the business
	 * that aren't directly tied to the production of goods or services. It might
	 * include selling, general, and administrative expenses (SG&A), including rent,
	 * salaries, marketing expenses, etc.
	 * 
	 * @param year
	 */
	public Double getOperatingExpenses(Integer year) {
		return transactionRepository.getOperatingExpensesForYear(year);
	}

	public Double getOtherExpensesTotal(Integer year) {
		return transactionRepository.getOtherExpensesForYear(year);
	}

	public Double getOtherRevenue(Integer year) {
		Double value = transactionRepository.getOtherIncomesForYear(year);
		if (null == value)
			return 0.0;
		return value;
	}

	/**
	 * Other Expenses/Income: This category includes items that are not related to a
	 * company’s main business operations. It could be interest paid, interest
	 * received, losses or gains from investments, etc.
	 */
	public double getOtherExpensesIncome(Integer year) {
		return transactionRepository.getOperatingExpensesForYear(year);
	}
	
	public IncomeStatementDto generateIncomeStatement(int fiscal_year) 
	{
		Sequence seq = this.gls.getLedger().getSeq();
		return this.isfls.generateIncomeStatement(fiscal_year, seq);
	} 
	


	

}
