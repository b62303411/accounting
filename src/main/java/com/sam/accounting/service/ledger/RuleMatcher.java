package com.sam.accounting.service.ledger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sam.accounting.repository.RuleRepository;
import com.sam.accounting.service.util.RuleStatementPromptFactory;

@Service
public class RuleMatcher {

	@Autowired
	RuleStatementPromptFactory prompt;
	
	@Autowired
	private RuleRepository ruleRepo;
}
