package com.sam.accounting.service.ledger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sam.accounting.model.entities.qb.AccountManager;
import com.sam.accounting.model.entities.qb.Ledger;
import com.sam.accounting.model.entities.qb.LedgerRuleFactory;
import com.sam.accounting.repository.RuleRepository;
import com.sam.accounting.service.TaxService;

@Service
public class GeneralLedgerFactory {

	@Autowired
	MutualFundStrategy mutual;
	
	@Autowired
	public RuleRepository ruleRepo;
	
	@Autowired
	public AccountManager accountManager;
	
	@Autowired
	public LedgerRuleFactory ruleFactory;
	
	@Autowired
	public TaxService taxService;
	
	public Ledger makeLedger() 
	{
		Ledger ledger = new Ledger(mutual,ruleRepo ,accountManager, ruleFactory,taxService);
		
		return ledger;
	}
}
