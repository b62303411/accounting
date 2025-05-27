package com.sam.accounting.service.ledger;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sam.accounting.model.TransactionType;

@Service
public class StrategyService {

	@Autowired
	CashStrategy cash;
	
	@Autowired
	LiabilityStrategy liability;
	
	@Autowired
	MutualFundStrategy mutual;
	
	
	
	private Map<TransactionType,TransactionStrategy> strategies = new HashMap<TransactionType, TransactionStrategy>();
	
	public StrategyService()
	{
		strategies.put(TransactionType.Cash, cash);
		strategies.put(TransactionType.Liability, liability);
		strategies.put(TransactionType.Liability, liability);
		strategies.put(TransactionType.OperatingExpenses, liability);
	}
}
