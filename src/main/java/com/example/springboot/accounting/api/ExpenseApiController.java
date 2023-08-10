package com.example.springboot.accounting.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.accounting.model.dto.AssetCreationRequest;
import com.example.springboot.accounting.model.entities.Amortisation;
import com.example.springboot.accounting.model.entities.Asset;
import com.example.springboot.accounting.model.entities.ExploitationExpense;
import com.example.springboot.accounting.model.entities.Transaction;
import com.example.springboot.accounting.repository.AssetRepository;
import com.example.springboot.accounting.repository.ExploitationExpenseRepository;
import com.example.springboot.accounting.service.CompanyProfileService;
import com.example.springboot.accounting.service.TransactionService;
@RestController
@RequestMapping("/api/expenses")
public class ExpenseApiController {
	private ExploitationExpenseRepository expenseRepo;
	private TransactionService transactionService;
	private CompanyProfileService service;
	
	@Autowired
	ExpenseApiController(CompanyProfileService service,ExploitationExpenseRepository expenseRepo,TransactionService transactionService)
	{
		this.expenseRepo = expenseRepo;
		this.transactionService = transactionService;
		this.service=service;
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ExploitationExpense> getExpense(@PathVariable("id")Long id) {
		
		ExploitationExpense e = null;
		e = expenseRepo.findById(id).get();
		return ResponseEntity.ok(e);
	}
	
	
}
