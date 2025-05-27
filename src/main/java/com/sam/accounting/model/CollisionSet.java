package com.sam.accounting.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.sam.accounting.model.entities.Transaction;

public class CollisionSet {
	public String account;
	public Double amount;
	public TransactionNature nature;
	public Date date;
	public Set<Transaction> collitions;
	CollisionSet()
	{
		collitions = new HashSet();
	}
	
	public CollisionSet(Transaction transaction) {
		this();
		amount = Math.abs(transaction.getAmount());
		date = transaction.getDate();
		nature = transaction.getTransactionNature();
		account = transaction.getAccount();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(account, amount, date, nature);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CollisionSet other = (CollisionSet) obj;
		return Objects.equals(account, other.account) && Objects.equals(amount, other.amount)
				&& Objects.equals(date, other.date) && nature == other.nature;
	}
	
	
	
}
