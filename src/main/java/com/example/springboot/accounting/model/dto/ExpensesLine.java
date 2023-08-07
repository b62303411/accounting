package com.example.springboot.accounting.model.dto;

import java.util.Date;

public class ExpensesLine {

	public double amount;
	public String description;
	public String expenseType;
	public Date date;
	public double getAmount() {
		return amount;
	}

}
