package com.sam.accounting.model.dto;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.format.annotation.NumberFormat;

import com.sam.accounting.model.IncomeStatementWhiteBoard;
import com.sam.accounting.model.entities.qb.Account;

public class IncomeStatementDto {

	public List<Account> revenueAccounts;
	public List<Account> operatingExpenseAccounts;
	public List<Account> otherExpenseAccounts;
	@NumberFormat(style = NumberFormat.Style.CURRENCY)
	public BigDecimal totalRevenue;
	@NumberFormat(style = NumberFormat.Style.CURRENCY)
	public BigDecimal totalOperatingExpenses;
	public BigDecimal totalOtherExpenses;

	public IncomeStatementWhiteBoard wb;
	
	@NumberFormat(style = NumberFormat.Style.CURRENCY)
	public BigDecimal incomeBeforeTax;
	public BigDecimal incomeTax;
	public BigDecimal incomeAfterTax;
	
	
	public void setOperatingExpenseAccounts(List<Account> expenseAccountMap) {
		this.operatingExpenseAccounts = expenseAccountMap;

	}

	public void setRevenueAccounts(List<Account> revenueAccountMap) {
		this.revenueAccounts = revenueAccountMap;
	}

	public void setTotalRevenue(double sum) {
		totalRevenue = BigDecimal.valueOf(sum);

	}

	public void setTotalOperatingExpenses(double sum) {
		totalOperatingExpenses = BigDecimal.valueOf(sum);

	}
	@NumberFormat(style = NumberFormat.Style.CURRENCY)
	public BigDecimal getTotalRevenue() {

		return totalRevenue;
	}
	@NumberFormat(style = NumberFormat.Style.CURRENCY)
	public BigDecimal getTotalOperatingExpenses() {
		return totalOperatingExpenses;
	}

	public void setIncomeBeforeTax(double d) {
		this.incomeBeforeTax = BigDecimal.valueOf(d);


	}
	public void setIncomeBeforeTax(BigDecimal d) {
		this.incomeBeforeTax = d;


	}

	public List<Account> getRevenueAccounts() {
		return revenueAccounts;
	}

	public List<Account> getOperatingExpenseAccounts() {
		return operatingExpenseAccounts;
	}
	@NumberFormat(style = NumberFormat.Style.CURRENCY)
	public BigDecimal getIncomeBeforeTax() {
		return incomeBeforeTax;
	}

	public List<Account> getOtherExpenseAccounts() {
		return otherExpenseAccounts;
	}

	public void setOtherExpenseAccounts(List<Account> otherExpenseAccounts) {
		this.otherExpenseAccounts = otherExpenseAccounts;
	}

	public BigDecimal getTotalOtherExpenses() {
		return totalOtherExpenses;
	}

	public void setTotalOtherExpenses(BigDecimal totalOtherExpenses) {
		this.totalOtherExpenses = totalOtherExpenses;
	}
	public void setTotalOtherExpenses(double totalOtherExpenses) {
		this.totalOtherExpenses = BigDecimal.valueOf(totalOtherExpenses);
	}

	public IncomeStatementWhiteBoard getWb() {
		return wb;
	}

	public void setWb(IncomeStatementWhiteBoard wb) {
		this.wb = wb;
	}

	public void setTotalRevenue(BigDecimal totalRevenue) {
		this.totalRevenue = totalRevenue;
	}

	public void setTotalOperatingExpenses(BigDecimal totalOperatingExpenses) {
		this.totalOperatingExpenses = totalOperatingExpenses;
	}

}
