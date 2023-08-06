package com.example.springboot.accounting.model.entities;

import java.util.Date;

import org.hibernate.annotations.Formula;

import com.example.springboot.accounting.model.TransactionNature;
import com.example.springboot.accounting.model.TransactionType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "transaction",
uniqueConstraints = {@UniqueConstraint(columnNames = {"date", "description","amount","account"})}
)
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private Date date;
	private String account;
	private String description;
    private double amount;
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    @Formula("(CASE WHEN type IN ('SalesRevenue', 'CostOfGoods', 'OperatingExpenses', 'AssetPurchased', 'AssetSales', 'DeptRepayment', 'LoanProceeds', 'Dividend', 'InvestmentIncome') THEN true ELSE false END)")
    private boolean isCashFlow;
    private String note;
    @Enumerated(EnumType.STRING)
    private TransactionNature transactionNature;
    

    
    
    /**
     * 
     * @param id
     * @param date
     * @param description
     * @param amount
     * @param type
     * @param isCashFlow
     */
	public Transaction(Long id, Date date, String description, double amount, TransactionType type,
			boolean isCashFlow) {
		super();
		this.id = id;
		this.date = date;
		this.description = description;
		this.amount = amount;
		this.type = type;
		this.isCashFlow = isCashFlow;
	}
	public Transaction() {
	
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public TransactionType getType() {
		return type;
	}
	public void setType(TransactionType type) {
		this.type = type;
	}
	public boolean isCashFlow() {
		return isCashFlow;
	}
	public void setCashFlow(boolean cashFlow) {
		this.isCashFlow = cashFlow;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public TransactionNature getTransactionNature() {
		return transactionNature;
	}
	public void setTransactionNature(TransactionNature transactionNature) {
		this.transactionNature = transactionNature;
	}
	
	
	
    
}
