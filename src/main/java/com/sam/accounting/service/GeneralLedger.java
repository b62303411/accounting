package com.sam.accounting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sam.accounting.model.entities.qb.Ledger;
import com.sam.accounting.service.ledger.GeneralLedgerFactory;

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
