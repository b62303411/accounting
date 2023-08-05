package com.example.springboot.accounting.presentation;



import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.springboot.accounting.model.entities.Account;
import com.example.springboot.accounting.model.entities.CompanyProfile;
import com.example.springboot.accounting.repository.AccountRepository;
import com.example.springboot.accounting.service.CompanyProfileService;

@Controller
@RequestMapping("/view")
public class AdministrationController {
	private CompanyProfileService companyProfileService;

	private AccountRepository accountRepository;
	@Autowired
	public AdministrationController(AccountRepository accountRepository,CompanyProfileService companyProfileService) 
	{
	
		this.companyProfileService=companyProfileService;
		this.accountRepository=accountRepository;
	}
	
	@GetMapping("/companyProfile")
	public String companyProfile(Model model) 
	{
	    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		CompanyProfile profile = companyProfileService.getProfile();
		model.addAttribute("companyProfile", profile);
		model.addAttribute("selectedYear", currentYear);
		model.addAttribute("year", currentYear);
		return "companyProfile";
	}
	
	@GetMapping("/accounts")
	public String accounts(Model model) 
	{
	    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		List<Account> accounts = accountRepository.findAll();
		model.addAttribute("accounts", accounts);
		model.addAttribute("selectedYear", currentYear);
		model.addAttribute("year", currentYear);
		return "accounts";
	}
	
	
}
