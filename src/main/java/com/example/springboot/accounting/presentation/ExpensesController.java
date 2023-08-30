package com.example.springboot.accounting.presentation;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.springboot.accounting.model.dto.ExpenseDuplicateGroup;
import com.example.springboot.accounting.model.dto.ExpensesLine;
import com.example.springboot.accounting.model.entities.ExploitationExpense;
import com.example.springboot.accounting.model.entities.Transaction;
import com.example.springboot.accounting.repository.ExploitationExpenseRepository;
import com.example.springboot.accounting.service.ExpensesService;
import com.example.springboot.accounting.service.FinancialStatementService;

@Controller
@RequestMapping("/view/expenses")
public class ExpensesController {
	private final ExpensesService es;
	private final FinancialStatementService financeStatementService;
	private ExploitationExpenseRepository repo;
	private NavigationFixture navFixture;
	@Autowired
	public ExpensesController(
			NavigationFixture navFixture,
			ExploitationExpenseRepository repo,
			FinancialStatementService financeStatementService,
			ExpensesService es) 
	{
		this.repo=repo;
		this.es=es;
		this.navFixture=navFixture;
		this.financeStatementService=financeStatementService;
	}
	
	@GetMapping("/{id}/edit")
	public String editExpense(Model model, @PathVariable("id") Long id) {
		Optional<ExploitationExpense> expense = repo.findById(id);
		model.addAttribute("expense", expense.get());
		
		return "expense_edit";
	}
	
	@GetMapping("/{year}")
	public String expenses(Model model, @PathVariable("year") Integer year) {
		List<ExpensesLine> expenses = financeStatementService.getExpenses(year);
		List<ExpensesLine> otherExpenses = financeStatementService.getOtherExpenses(year);
		Map<String, Double> spendingData = financeStatementService.getExpenseReport(year);
		ExpensesLine totals = getTotalLine(expenses);
	
		ExpensesLine totalOthers= getTotalLine(otherExpenses);	
		// ... other categories

		model.addAttribute("spendingData", spendingData);
		
		navFixture.insertOptions(year,model);
		
		model.addAttribute("currentPage","Expenses");
		
		model.addAttribute("expenses", expenses);
		
		model.addAttribute("otherExpenses",otherExpenses);
		
		model.addAttribute("totals", totals);
		
		model.addAttribute("totalOthers", totalOthers);
		
		model.addAttribute("sampleTransaction", new Transaction());
		List<ExpenseDuplicateGroup> duplicates = es.getDuplicates();
		model.addAttribute("duplicates",duplicates);
		
		return "expenses";
	}
	
	
	private ExpensesLine getTotalLine(List<ExpensesLine> expenses) {
		double grossAmount=0;
		double tbst=0;
		double st = 0;
		for (ExpensesLine revenueLine : expenses) {
			grossAmount+=revenueLine.getAmount();
			tbst+=revenueLine.tbst;
			st += revenueLine.st;
			//taxes+= revenueLine.getTpsTvq();
			//revenue+=revenueLine.getRevenue();
		}
		ExpensesLine totals = new ExpensesLine();
		totals.amount=grossAmount;
		totals.tbst=tbst;
		totals.st = st;
		totals.description= "Total";
		return totals;
	}
}
