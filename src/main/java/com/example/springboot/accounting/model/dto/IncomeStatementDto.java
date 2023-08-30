package com.example.springboot.accounting.model.dto;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.format.annotation.NumberFormat;

import com.example.springboot.accounting.model.IncomeStatementWhiteBoard;
import com.example.springboot.accounting.model.entities.qb.Account;

public class IncomeStatementDto {

	public List<Account> revenueAccounts;
	public List<Account> expenseAccounts;
	@NumberFormat(style = NumberFormat.Style.CURRENCY)
	public BigDecimal totalRevenue;
	@NumberFormat(style = NumberFormat.Style.CURRENCY)
	public BigDecimal totalExpenses;
	@NumberFormat(style = NumberFormat.Style.CURRENCY)
	public BigDecimal netIncome;
	public IncomeStatementWhiteBoard wb;
	
	
	public void setExpenseAccounts(List<Account> expenseAccountMap) {
		this.expenseAccounts = expenseAccountMap;

	}

	public void setRevenueAccounts(List<Account> revenueAccountMap) {
		this.revenueAccounts = revenueAccountMap;
	}

	public void setTotalRevenue(double sum) {
		totalRevenue = BigDecimal.valueOf(sum);

	}

	public void setTotalExpenses(double sum) {
		totalExpenses = BigDecimal.valueOf(sum);

	}
	@NumberFormat(style = NumberFormat.Style.CURRENCY)
	public BigDecimal getTotalRevenue() {

		return totalRevenue;
	}
	@NumberFormat(style = NumberFormat.Style.CURRENCY)
	public BigDecimal getTotalExpenses() {
		return totalExpenses;
	}

	public void setNetIncome(double d) {
		this.netIncome = BigDecimal.valueOf(d);


	}
	public void setNetIncome(BigDecimal d) {
		this.netIncome = d;


	}

	public List<Account> getRevenueAccounts() {
		return revenueAccounts;
	}

	public List<Account> getExpenseAccounts() {
		return expenseAccounts;
	}
	@NumberFormat(style = NumberFormat.Style.CURRENCY)
	public BigDecimal getNetIncome() {
		return netIncome;
	}

}
