package com.example.springboot.accounting.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.accounting.model.ExploitationExpenseType;
import com.example.springboot.accounting.model.dto.BulkUpdateExpenseDTO;
import com.example.springboot.accounting.model.entities.ExploitationExpense;
import com.example.springboot.accounting.repository.ExploitationExpenseRepository;
import com.example.springboot.accounting.service.CompanyProfileService;
import com.example.springboot.accounting.service.ExpensesService;
import com.example.springboot.accounting.service.TransactionService;
@RestController
@RequestMapping("/api/expenses")
public class ExpenseApiController {
	private ExpensesService expensesService;
	private ExploitationExpenseRepository expenseRepo;
	private TransactionService transactionService;
	private CompanyProfileService service;
	
	@Autowired
	ExpenseApiController(ExpensesService expensesService,CompanyProfileService service,ExploitationExpenseRepository expenseRepo,TransactionService transactionService)
	{
		this.expenseRepo = expenseRepo;
		this.transactionService = transactionService;
		this.service=service;
		this.expensesService=expensesService;
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ExploitationExpense> getExpense(@PathVariable("id")Long id) {
		
		ExploitationExpense e = null;
		e = expenseRepo.findById(id).get();
		return ResponseEntity.ok(e);
	}
	
	@PostMapping("/bulkupdate")
	
	public ResponseEntity<Map<String, Object>> bulkUpdateExpenses(@RequestBody BulkUpdateExpenseDTO bulkUpdateRequest) {
	    // code to find and update expenses matching the description
	    expensesService.fixExpense(bulkUpdateRequest.getDescription(), bulkUpdateRequest.getNewType());

	    Map<String, Object> response = new HashMap<>();
	    response.put("message", "Expenses updated successfully");
	    return new ResponseEntity<>(response, HttpStatus.OK);
	}


}
