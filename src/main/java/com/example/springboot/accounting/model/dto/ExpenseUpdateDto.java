package com.example.springboot.accounting.model.dto;

import com.example.springboot.accounting.model.ExploitationExpenseType;

public class ExpenseUpdateDto {
	
	private Long transactionId;

	private Long invoiceId;

	private Double totalBeforeSalesTaxes;

	private Double tps;

	private Double tvq;

	private String description;

	private String date;
	
	private ExploitationExpenseType expenseType;

	public ExploitationExpenseType getExpenseType() {
		return expenseType;
	}

	public void setExpenseType(ExploitationExpenseType expenseType) {
		this.expenseType = expenseType;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transaction_id) {
		this.transactionId = transaction_id;
	}

	public Long getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(Long invoice_id) {
		this.invoiceId = invoice_id;
	}

	public Double getTotalBeforeSalesTaxes() {
		return totalBeforeSalesTaxes;
	}

	public void setTotalBeforeSalesTaxes(Double totalBeforeSalesTaxes) {
		this.totalBeforeSalesTaxes = totalBeforeSalesTaxes;
	}

	public Double getTps() {
		return tps;
	}

	public void setTps(Double tps) {
		this.tps = tps;
	}

	public Double getTvq() {
		return tvq;
	}

	public void setTvq(Double tvq) {
		this.tvq = tvq;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	
}
