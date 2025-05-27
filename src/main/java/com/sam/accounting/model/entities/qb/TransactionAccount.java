package com.sam.accounting.model.entities.qb;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TransactionAccount {
	public String vendor_client;
	public Account debited;
	public Set<Account> creditedAccounts = new HashSet();
	
	public double amount;
	public String vendor_client_from;
	public String vendor_client_to;
	public List<TransactionAccount> split;
	public Double credited_balence;
	
	public void setCredited(Account account) 
	{
		creditedAccounts.add(account);
	}
	
}
