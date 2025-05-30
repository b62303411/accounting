package com.sam.accounting.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sam.accounting.service.GeneralLedgerService;

@Controller
@RequestMapping("/view/ledger")
public class GeneralLedgerController {

	@Autowired
	public GeneralLedgerService gls;

	
	public GeneralLedgerController() {

	}

	@GetMapping
	public String expenses(Model model) {
		//List<LedgerEntryDTO> list = gls.getLedgerDtos();
		
		//model.addAttribute("ledgerEntries", list);

		//gls.getLedger().printAccounts();
		return "generalLedger";
	}

	

}
