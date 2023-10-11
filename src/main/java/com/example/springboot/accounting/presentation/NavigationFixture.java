package com.example.springboot.accounting.presentation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.example.springboot.accounting.model.FiscalYearEnd;
import com.example.springboot.accounting.model.MenuOptions;
import com.example.springboot.accounting.model.dto.MenuOption;
import com.example.springboot.accounting.service.CompanyProfileService;

@Service
public class NavigationFixture {
	
	private final CompanyProfileService service;
	
	@Autowired
	public NavigationFixture(CompanyProfileService service)
	{
		this.service=service;
	}
	
	public void insertOptions(Integer year, Model model) {
		List<MenuOption> menuOptions = MenuOptions.getOptions();
		model.addAttribute("selectedYear", year);
		model.addAttribute("menuOptions", menuOptions);
		model.addAttribute("companyName", service.getProfile().getName());	
		FiscalYearEnd fiscalYearEnd = service.getProfile().getFiscalYearEnd();
		model.addAttribute("year",year);
		model.addAttribute("fiscal_end_day", fiscalYearEnd.day);
		model.addAttribute("fiscal_end_month", fiscalYearEnd.month);
		model.addAttribute("fiscal_end_year", year);
	}
}
