package com.example.springboot.accounting.service.ledger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springboot.accounting.repository.RuleRepository;
import com.example.springboot.accounting.service.util.RuleStatementPromptFactory;

@Service
public class RuleMatcher {

	@Autowired
	RuleStatementPromptFactory prompt;
	
	@Autowired
	private RuleRepository ruleRepo;
}
