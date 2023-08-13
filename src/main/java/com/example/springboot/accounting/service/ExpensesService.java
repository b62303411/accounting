package com.example.springboot.accounting.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springboot.accounting.model.ExploitationExpenseType;
import com.example.springboot.accounting.model.entities.ExploitationExpense;
import com.example.springboot.accounting.repository.ExploitationExpenseRepository;

@Service
public class ExpensesService {

	ExploitationExpenseRepository repo;
	
	@Autowired
	public ExpensesService(ExploitationExpenseRepository repo) 
	{
		this.repo=repo;
	}
	
	public void fixExpense(String description,ExploitationExpenseType type ) {
		List<ExploitationExpense> expenses = repo.findAllLikeDescription(description);
		boolean changed = false;
		for (ExploitationExpense exploitationExpense : expenses) {
			if(exploitationExpense.getExpenseType() == null) 
			{
				exploitationExpense.setExpenseType(type);
				changed = true;
			}	
		}
		if(changed)
			repo.saveAll(expenses);
	}
}
