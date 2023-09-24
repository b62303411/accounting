package com.example.springboot.accounting.service.ledger;

import com.example.springboot.accounting.model.TransactionNature;
import com.example.springboot.accounting.model.entities.qb.Account;
import com.example.springboot.accounting.model.entities.qb.TransactionAccount;

public interface TransactionStrategy {

	/**
	 * 
	 * @param amount
	 * @param transactionAccount
	 * @param cardinality
	 * @param type
	 * @param balence
	 */
	void populate(double amount, 
			Account transactionAccount, TransactionAccount cardinality, TransactionNature type, Double balence);
}
