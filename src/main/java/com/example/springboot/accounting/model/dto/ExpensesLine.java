package com.example.springboot.accounting.model.dto;

import java.util.Date;

public class ExpensesLine {

	public long id;
	public double amount;
	public String description;
	public String expenseType;
	public String payee;
	public double tbst;
	/**
	 * Sales Taxes
	 */
	public double st;
	public Date date;
	public double getAmount() {
		return amount;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getExpenseType() {
		return expenseType;
	}
	public void setExpenseType(String expenseType) {
		this.expenseType = expenseType;
	}
	public String getPayee() {
		return payee;
	}
	public void setPayee(String payee) {
		this.payee = payee;
	}
	public double getTbst() {
		return tbst;
	}
	public void setTbst(double tbst) {
		this.tbst = tbst;
	}
	public double getSt() {
		return st;
	}
	public void setSt(double st) {
		this.st = st;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	

}
