package com.example.springboot.accounting.model.entities.qb;

import java.util.List;

public class TransactionAccount {
	public String vendor_client;
	public Account debited;
	public Account credited;
	public double amount;
	public String vendor_client_from;
	public String vendor_client_to;
	public List<TransactionAccount> split;
}
