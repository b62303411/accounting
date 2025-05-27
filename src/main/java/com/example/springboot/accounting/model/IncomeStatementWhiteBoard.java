package com.example.springboot.accounting.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.example.springboot.accounting.model.entities.qb.Account;
import com.example.springboot.accounting.model.entities.qb.Transaction;
import com.example.springboot.accounting.model.entities.qb.TransactionEntry;
import com.example.springboot.accounting.service.FiscalYearService.DateBoundaries;

public class IncomeStatementWhiteBoard {
	public DateBoundaries boundaries;
	private Set<Transaction> fy_transactions = new HashSet<Transaction>();
	private Map<UUID, TransactionEntry> entries = new HashMap<UUID, TransactionEntry>();
	public Set<TransactionEntry> entriesSet = new HashSet<TransactionEntry>();
	public List<Account> allAccounts = null;
	public List<Account> revenueAccounts = new ArrayList<Account>();
	public List<Account> assetAccounts = new ArrayList<Account>();
	public List<Account> equityAccounts = new ArrayList<Account>();
	public List<Account> expensesAccounts = new ArrayList<Account>();
	public List<Account> operatingExpensesAccounts = new ArrayList<Account>();
	public List<Account> otherExpensesAccounts = new ArrayList<Account>();
	public List<Account> liabilityAccounts = new ArrayList<Account>();
	public Map<String, Account> accountMap = new HashMap<>();
	public int fiscal_year;
	public double beforeTaxIncome;
	public double totalTaxes;
	public double adjustedBeforeTaxIncome;
	public double adjustedIncomeTax;

	public Account getAccountByNo(String no) {
		return accountMap.get(no);
	}

	public void post() {
		for (Transaction transaction : getTransactions()) {
			transaction.post();
		}
	}

	public void addEntry(TransactionEntry e) {
		if (!entriesSet.contains(e)) {
			entries.put(e.getId(), e);
			entriesSet.add(e);
		}
	}

	public Set<Transaction> getTransactions() {
		return fy_transactions;
	}

	public void setTransactions(Set<Transaction> fy_transactions) {
		this.fy_transactions = fy_transactions;
	}

	public double getTotalOperatingExpenses() {
		return getTotal(operatingExpensesAccounts);
	}

	public double getTotalOtherExpenses() {
		return getTotal(otherExpensesAccounts);
	}

	public double getTotalRevenue() {
		return getTotal(revenueAccounts);
	}
	
	private double getTotal(List<Account> list) {
		return list.stream().mapToDouble(Account::getBalance).sum();
	}

	public boolean hasTransaction(Transaction t) {
		for (TransactionEntry e : entriesSet) {
			for (TransactionEntry e2 : t.getEntries()) 
			{
				if(e.equals(e2))
					return true;
			}
		}
		for (TransactionEntry e : t.getEntries()) {
			if(this.entriesSet.contains(e))
				return true;
			
		}
		return false;
	}

	public void setBeforeTaxIncome(double beforeTaxIncome) {
		this.beforeTaxIncome=beforeTaxIncome;
		this.adjustedBeforeTaxIncome=beforeTaxIncome;
		
	}
	public void setTotalTaxes(double tax) 
	{
		this.totalTaxes=tax;
		this.adjustedIncomeTax=tax;
	}

}
