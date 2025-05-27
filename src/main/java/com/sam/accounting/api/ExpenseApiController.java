package com.sam.accounting.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sam.accounting.model.dto.BulkUpdateExpenseDTO;
import com.sam.accounting.model.dto.ExpenseUpdateDto;
import com.sam.accounting.model.dto.MergeRequest;
import com.sam.accounting.model.entities.ExploitationExpense;
import com.sam.accounting.repository.ExploitationExpenseRepository;
import com.sam.accounting.service.CompanyProfileService;
import com.sam.accounting.service.ExpensesService;
import com.sam.accounting.service.TransactionService;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseApiController {
	private ExpensesService expensesService;
	private ExploitationExpenseRepository expenseRepo;
	private TransactionService transactionService;
	private CompanyProfileService service;

	@Autowired
	ExpenseApiController(ExpensesService expensesService, CompanyProfileService service,
			ExploitationExpenseRepository expenseRepo, TransactionService transactionService) {
		this.expenseRepo = expenseRepo;
		this.transactionService = transactionService;
		this.service = service;
		this.expensesService = expensesService;
	}

	@GetMapping("/{id}")
	public ResponseEntity<ExploitationExpense> getExpense(@PathVariable("id") Long id) {

		ExploitationExpense e = null;
		e = expenseRepo.findById(id).get();
		return ResponseEntity.ok(e);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ExploitationExpense> updateExpense(@PathVariable Long id,
			@RequestBody ExpenseUpdateDto expense) {
		try {
			return ResponseEntity.ok(expensesService.updateExpense(id, expense));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/bulkupdate")
	public ResponseEntity<Map<String, Object>> bulkUpdateExpenses(@RequestBody BulkUpdateExpenseDTO bulkUpdateRequest) {
		// code to find and update expenses matching the description
		expensesService.fixExpense(bulkUpdateRequest.getDescription(), bulkUpdateRequest.getNewType());

		Map<String, Object> response = new HashMap<>();
		response.put("message", "Expenses updated successfully");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping("/inferFromTransaction")
	public ResponseEntity<Map<String, Object>> updateInferExpensesFromTransactions() {
		expensesService.removeExpenseTransactions();
		int affectedRows = expensesService.inferFromTransactions();
		Map<String, Object> response = new HashMap<>();

		response.put("affectedRows", affectedRows);
		response.put("message", "Expenses inferred from transactions successfully.");
		return ResponseEntity.ok(response);

	}

	@PutMapping("/inferFromAssetLegs")
	public ResponseEntity<Map<String, Object>> inferFromAssetLegs() {

		int affectedRows = expensesService.inferFromAssetLegs();
		Map<String, Object> response = new HashMap<>();

		response.put("affectedRows", affectedRows);
		response.put("message", "Expenses inferred from Asset Legs successfully.");
		return ResponseEntity.ok(response);

	}

	@PostMapping("/merge")
	public ResponseEntity<?> mergeExpenses(@RequestBody MergeRequest mergeRequest) {
		try {
			expensesService.mergeExpenses(mergeRequest.getIds());
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

}
