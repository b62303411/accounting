package com.sam.accounting.presentation;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sam.accounting.model.MenuOptions;
import com.sam.accounting.model.dto.MenuOption;
import com.sam.accounting.model.entities.CompanyProfile;
import com.sam.accounting.model.entities.qb.Account;
import com.sam.accounting.model.entities.qb.AccountBalance;
import com.sam.accounting.model.entities.qb.AccountManager;
import com.sam.accounting.model.entities.qb.AccountType;
import com.sam.accounting.model.entities.qb.Ledger;
import com.sam.accounting.repository.AccountRepository;
import com.sam.accounting.service.AccountService;
import com.sam.accounting.service.CompanyProfileService;
import com.sam.accounting.service.GeneralLedgerService;

@Controller
@RequestMapping("/view")
public class AdministrationController {
	private CompanyProfileService companyProfileService;
	private GeneralLedgerService gls;
	private AccountService accountService;

	@Autowired
	public AdministrationController(AccountService accountService, GeneralLedgerService gls,
			AccountRepository accountRepository, CompanyProfileService companyProfileService) {

		this.companyProfileService = companyProfileService;
		this.accountService = accountService;
		this.gls = gls;
	}

	@GetMapping("/companyProfile")
	public String companyProfile(Model model) {
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		CompanyProfile profile = companyProfileService.getProfile();
		model.addAttribute("companyProfile", profile);
		model.addAttribute("selectedYear", currentYear);
		model.addAttribute("year", currentYear);
		insertOptions(model);
		return "companyProfile";
	}

	@GetMapping("/accounts")
	public String accounts(Model model) {
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		// List<Account> accounts = accountRepository.findAll();
		Ledger l = gls.getLedger();
		
		AccountManager am = l.getAccountManager();

		List<Account> accounts = am.getAccounts();
		Comparator<Account> compare = new Comparator<Account>() {

			@Override
			public int compare(Account o1, Account o2) {
				return o1.getAccountType().compareTo(o2.getAccountType());
			}
		};
		Collections.sort(accounts, compare);
		model.addAttribute("accounts", accounts);
		model.addAttribute("selectedYear", currentYear);
		model.addAttribute("year", currentYear);
		insertOptions(model);
		return "accounts";
	}

	@GetMapping("/balance-sheet")
	public String getBalanceSheet(Model model) {
		model.addAttribute("currentDate", LocalDate.now());

		model.addAttribute("assets", accountService.getAccountsByType(AccountType.ASSET));
		model.addAttribute("totalAssets", accountService.getTotalByType(AccountType.ASSET));

		model.addAttribute("liabilities", accountService.getAccountsByType(AccountType.LIABILITY));
		model.addAttribute("totalLiabilities", accountService.getTotalByType(AccountType.LIABILITY));

		model.addAttribute("equity", accountService.getAccountsByType(AccountType.EQUITY));
		model.addAttribute("totalEquity", accountService.getTotalByType(AccountType.EQUITY));
        model.addAttribute("AccountBalance",AccountBalance.class);
		return "balance-sheet"; // This should be the name of your Thymeleaf template
	}
	
	@GetMapping("/expense-balance-sheet")
	public String getExpenseBalanceSheet(Model model) {
		model.addAttribute("currentDate", LocalDate.now());

		model.addAttribute("taxable_expenses", accountService.getTaxableAccountsByType(AccountType.EXPENSE));
		model.addAttribute("taxable_totalExpenes", accountService.getTotalTaxableByType(AccountType.EXPENSE));
		
		model.addAttribute("non_taxable_expenses", accountService.getNonTaxableAccountsByType(AccountType.EXPENSE));
		model.addAttribute("non_taxable_totalExpenes", accountService.getTotalNonTaxableByType(AccountType.EXPENSE));

		
		model.addAttribute("revenues", accountService.getAccountsByType(AccountType.REVENUE));
		model.addAttribute("totalRevenues", accountService.getTotalByType(AccountType.REVENUE));
		model.addAttribute("AccountBalance",AccountBalance.class);
		

		return "expense-balance-sheet"; // This should be the name of your Thymeleaf template
	}

	private void insertOptions(Model model) {
		List<MenuOption> menuOptions = MenuOptions.getOptions();
		model.addAttribute("menuOptions", menuOptions);
	}

}
