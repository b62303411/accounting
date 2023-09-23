package com.example.springboot.accounting.service.ledger;

import com.example.springboot.accounting.model.entities.qb.Account;
import com.example.springboot.accounting.model.entities.qb.TransactionAccount;

public interface TransactionStrategy {

	void populate(double amount, Account transactionAccount, TransactionAccount cardinality, String type, Double balence);
}
