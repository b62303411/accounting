package com.example.springboot.accounting.presentation;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.springboot.accounting.model.TransactionType;
import com.example.springboot.accounting.model.dto.AccountValidation;
import com.example.springboot.accounting.model.dto.FinancialStatementLine;
import com.example.springboot.accounting.model.dto.ReportAvailable;
import com.example.springboot.accounting.model.dto.RevenueLine;
import com.example.springboot.accounting.model.entities.Account;
import com.example.springboot.accounting.model.entities.Asset;
import com.example.springboot.accounting.model.entities.BankStatement;
import com.example.springboot.accounting.model.entities.Invoice;
import com.example.springboot.accounting.model.entities.KnownDescription;
import com.example.springboot.accounting.model.entities.Transaction;
import com.example.springboot.accounting.repository.AccountRepository;
import com.example.springboot.accounting.repository.AssetRepository;
import com.example.springboot.accounting.repository.InvoiceRepository;
import com.example.springboot.accounting.service.AssetService;
import com.example.springboot.accounting.service.CompanyProfileService;
import com.example.springboot.accounting.service.FinancialStatementService;

@Controller
@RequestMapping("/view")
public class FinanceThimeleafController {
	private final FinancialStatementService financeStatementService;
	private final CompanyProfileService service;
	private final AccountRepository accountRepo;
	private final AssetService assetService;
	private AssetRepository assetRepository;
	private InvoiceRepository invoiceRepo;
	private final NavigationFixture navFixture;

	@Autowired
	public FinanceThimeleafController(NavigationFixture navFixture, AssetService assetService,
			AssetRepository assetRepository, AccountRepository accountRepo, CompanyProfileService service,
			FinancialStatementService financeStatementService, InvoiceRepository invoiceRepo) {
		this.financeStatementService = financeStatementService;
		this.accountRepo = accountRepo;
		this.assetRepository = assetRepository;
		this.service = service;
		this.assetService = assetService;
		this.invoiceRepo = invoiceRepo;
		this.navFixture = navFixture;

	}

	@GetMapping("/revenues/{year}")
	public String revenues(Model model, @PathVariable("year") Integer year) {
		List<RevenueLine> revenues = financeStatementService.getRevenues(year);
	
		Date date = null;

		double grossAmount = 0;
		double taxes = 0;
		double revenue = 0;
		for (RevenueLine revenueLine : revenues) {
			grossAmount += revenueLine.getAmount();
			taxes += revenueLine.getTpsTvq();
			revenue += revenueLine.getRevenue();
		}
		RevenueLine totals = new RevenueLine(grossAmount, taxes, revenue, "Total", date);

		navFixture.insertOptions(year, model);

		model.addAttribute("currentPage", "Revenues");

		model.addAttribute("revenues", revenues);

		model.addAttribute("totals", totals);
		return "revenue";
	}

	@GetMapping("/bills/{year}")
	public String bills(Model model, @PathVariable("year") Integer year) {
		List<Invoice> bills = new ArrayList<Invoice>();
		bills = invoiceRepo.findAll();
		model.addAttribute("bills", bills);
		navFixture.insertOptions(year, model);
		model.addAttribute("currentPage", "Bills");
		return "bills";
	}

	@GetMapping("/transactions/{year}")
	public String transactions(Model model, @PathVariable("year") Integer year) {

		if (year == null) {
			year = 2023;
		}
		List<Transaction> transactions = financeStatementService.getTransactions(year);
		model.addAttribute("companyName", service.getProfile().getName());
		model.addAttribute("transactions", transactions);
		model.addAttribute("selected_report_type", "t");

		model.addAttribute("currentPage", "Transaction");

		navFixture.insertOptions(year, model);

		List<Long> assetLessTransactions = new ArrayList<Long>();
		for (Transaction transaction : transactions) {
			if (transaction.getType() == TransactionType.AssetPurchased) {
				Asset asset = assetRepository.findByPurchaceTransaction(transaction);
				if (null == asset) {
					assetLessTransactions.add(transaction.getId());
				}
			}

		}
		model.addAttribute("assetLessTransactions", assetLessTransactions);

		List<AccountValidation> account_validations = new ArrayList<AccountValidation>();
		List<Account> accounts = this.accountRepo.findAll();
		for (Account account : accounts) {
			List<ReportAvailable> availabilities = getReportAvailabilities(account.getAccountNo(), year);
			AccountValidation val = new AccountValidation();
			val.setAvailabilities(availabilities);
			val.setNumber(Integer.valueOf(account.getAccountNo()));
			account_validations.add(val);
		}

		List<String> months = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov",
				"Dec");
		model.addAttribute("months", months);
		model.addAttribute("accounts", account_validations);
		List<KnownDescription> descriptions = financeStatementService.findAllKnownDescriptions();
		model.addAttribute("knownDescriptions", descriptions);

		return "Transactions";
	}

	private List<ReportAvailable> getReportAvailabilities(String accountNumber, Integer year) {
		if (year == null) {
			year = 2023;
		}
		List<BankStatement> bankStatements = financeStatementService.getBankStatements(accountNumber, year);
		List<String> froms = new ArrayList<String>();
		for (BankStatement bankStatement : bankStatements) {
			froms.add(bankStatement.getTo().split("_")[0]);
		}
		List<String> months = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov",
				"Dec");
		List<ReportAvailable> availabilities = new ArrayList<ReportAvailable>();
		for (String string : months) {
			ReportAvailable availability = new ReportAvailable();
			availability.month = string;
			availability.available = froms.contains(string);
			availabilities.add(availability);
		}
		return availabilities;
	}

	@GetMapping("/balance/{year}")
	public String balance(Model model, @PathVariable("year") Integer year) {
		if (year == null) {
			year = 2023;
		}
		
		model.addAttribute("date", Instant.now().toString());
		List<FinancialStatementLine> assets = assetService.getAssetFinantialStatement();
		model.addAttribute("assets", assets);
		navFixture.insertOptions(year, model);
		model.addAttribute("currentPage", "Balance Sheet");
		return "BalanceSheet";
	}

	@GetMapping("/incomeStatement/{year}")
	public String incomeStatement(Model model, @PathVariable("year") Integer year) {
		if (year == null) {
			year = 2023;
		}
		List<FinancialStatementLine> lines = financeStatementService.getIncomeStatement(year);
		model.addAttribute("selected_report_type", "transactions");
		navFixture.insertOptions(year, model);
		model.addAttribute("currentPage", "Income Statement");

		model.addAttribute("incomeStatement", lines);

		return "IncomeStatement";
	}

	/*
	 * @GetMapping("/initiative/new") public String createInitiative(Model model) {
	 * Initiative initiative = new Initiative(); // Set default values for the new
	 * story here, if needed financeStatementService.save(initiative); return
	 * "redirect:/userstories"; }
	 * 
	 * @PostMapping("/initiatives/{id}/addEpic") public String addEpic(Model
	 * model,@PathVariable("id") Long id) { Initiative initiative =
	 * financeStatementService.getById(id); // Set default values for the new story
	 * here, if needed Epic e = new Epic(); e.setInitiative(initiative);
	 * initiative.addEpic(e); financeStatementService.save(initiative); return
	 * "redirect:/userstories"; }
	 */

}
