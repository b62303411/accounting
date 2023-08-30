package com.example.springboot.accounting.presentation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.springboot.accounting.model.dto.LedgerEntryDTO;
import com.example.springboot.accounting.service.GeneralLedgerService;

@Controller
@RequestMapping("/view/ledger")
public class GeneralLedgerController {

	@Autowired
	public GeneralLedgerService gls;

	
	public GeneralLedgerController() {

	}

	@GetMapping
	public String expenses(Model model) {
//		List<ExpensesLine> expenses = financeStatementService.getExpenses(year);
//		List<ExpensesLine> otherExpenses = financeStatementService.getOtherExpenses(year);
//		Map<String, Double> spendingData = financeStatementService.getExpenseReport(year);
//		ExpensesLine totals = getTotalLine(expenses);
//	
		// ExpensesLine totalOthers= getTotalLine(otherExpenses);
		// ... other categories

		List<LedgerEntryDTO> list = gls.getLedgerDtos();
		
		model.addAttribute("ledgerEntries", list);

		gls.getLedger().printAccounts();
		return "generalLedger";
	}

	

}
