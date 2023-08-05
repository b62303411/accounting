package com.example.springboot.accounting.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springboot.accounting.model.dto.FinancialStatementLine;
import com.example.springboot.accounting.repository.TransactionRepository;

@Service
public class IncomeStatementService {

	private final CompanyProfileService profile;
	private final TransactionService transactionService;
	private final TransactionRepository transactionRepository;
	private final FinancialStatementLineFactory fsf;

	@Autowired
	public IncomeStatementService(CompanyProfileService profile, FinancialStatementLineFactory fsf,
			TransactionRepository transactionRepository, TransactionService transactionService) {
		this.transactionRepository = transactionRepository;
		this.transactionService = transactionService;
		this.fsf = fsf;
		this.profile = profile;

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
		statement.getOperatingIncome();
		statement.setOtherExpenses(getOtherExpenses(start, stop));
		statement.otherRevenue = getOtherRevenue(start, stop);
		return statement;
	}

	private Double getOtherRevenue(Date start, Date stop) {
		Double value =  transactionRepository.getOtherIncomesForFiscalYear(start, stop);
		if(null == value)
			return 0.0;
		else return value;
	}

	private Double getOtherExpenses(Date start, Date stop) {
		Double value =  transactionRepository.getOtherExpensesForFiscalYear(start, stop);
		if(null == value)
			return 0.0;
		else return value;
		
	}

	private Double getOperatingExpenses(Date start, Date stop) {
		return transactionRepository.getOperatingExpensesForFiscalYear(start, stop);
	}

	private Double getCogs(Date start, Date stop) {
		return transactionRepository.getCOGSForFiscalYear(start, stop);
	}

	private Double getRevenue(Date start, Date stop) {
		Double grossAmount = transactionRepository.getSalesRevenueForFiscalYear(start, stop);
		if (null == grossAmount)
			return 0.0;
		// the gross amount including TPS and TVQ
		double taxRate = 0.14975; // combined TPS and TVQ rate

		double netAmount = grossAmount / (1 + taxRate); // this is the amount to be recorded as sales revenue
		double taxes = grossAmount - netAmount; // this is the amount to be remitted to the government
		return netAmount;
	}

	public FinantialStatement getFinantialStatement(Integer year) {
		FinantialStatement statement = new FinantialStatement();
		statement.revenue = getRevenue(year);
		statement.setCogs(getCogs(year));
		statement.getGrossProfit();
		statement.setOperatingExpenses(getOperatingExpenses(year));
		statement.getOperatingIncome();
		statement.setOtherExpenses(getOtherExpenses(year));
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
		Map<String, LocalDate> boudnaries = profile.getProfile().getFiscalYearEnd().getFiscalYearBoundaries(year);
		LocalDate start = boudnaries.get("start");
		LocalDate end = boudnaries.get("end");

		Date date_start = Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date date_end = Date.from(end.atStartOfDay(ZoneId.systemDefault()).toInstant());

		double incomeTax;
		FinantialStatement statement = getFinantialStatement(date_start, date_end);

		List<FinancialStatementLine> lines = convertToFinLines(statement);
		return lines;
	}

	private List<FinancialStatementLine> convertToFinLines(FinantialStatement statement) {
		List<FinancialStatementLine> lines = new ArrayList();
		FinancialStatementLine revenue_line = makeFs(statement.revenue, "Revenue");
		FinancialStatementLine cogs_line = makeFs(statement.cogs, "Cogs");
		FinancialStatementLine grossProfit_line = makeFs(statement.getGrossProfit(), "Gross Profit");
		FinancialStatementLine operatingExpenses_line = makeFs(statement.operatingExpenses, "Operating Expenses");
		FinancialStatementLine operatingIncome_line = makeFs(statement.getOperatingIncome(), "Operating Income");
		FinancialStatementLine otherExpenses_line = makeFs(statement.otherExpenses, "Other Expenses");
		FinancialStatementLine otherIncome_line = makeFs(statement.otherRevenue, "Other Revenue");
		FinancialStatementLine pretaxIncome_line = makeFs(statement.getPretaxIncome(), "Pretax Income");
		FinancialStatementLine incomeTax_line = makeFs(statement.incomeTax, "Income Tax");
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

	public Double getOtherExpenses(Integer year) {
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
}
