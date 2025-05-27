package com.example.springboot.accounting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springboot.accounting.model.entities.qb.Ledger;
import com.example.springboot.accounting.service.ledger.GeneralLedgerFactory;

@Service
public class GeneralLedger {
	@Autowired
	private GeneralLedgerFactory glFactory;
	
	Ledger ledger = null;
	
	public Ledger getLedger() 
	{
		if(null == ledger) 
		{
			ledger=glFactory.makeLedger();
		}
		ledger.recalculateLedger();
		return ledger;
	}
}
