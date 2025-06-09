package com.sam.accounting.model.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.sam.accounting.model.ExpenseKey;
import com.sam.accounting.model.entities.ExploitationExpense;

public class ExpenseDuplicateGroup {

	public List<ExploitationExpense> expenses;
	public ExpenseKey key;

	public List<Long> getIds() {
		List<Long> ids = new ArrayList<Long>();
		for (ExploitationExpense e : expenses) {
			ids.add(e.getId());
		}
		return ids;
	}

	public String getJoinedIds() {
		return getIds().stream().map(String::valueOf).collect(Collectors.joining(","));
	}

	public List<ExploitationExpense> getExpenses() {
		return expenses;
	}

	public void setExpenses(List<ExploitationExpense> expenses) {
		this.expenses = expenses;
	}

	public ExpenseKey getKey() {
		return key;
	}

	public void setKey(ExpenseKey key) {
		this.key = key;
	}

}
