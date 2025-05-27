package com.sam.accounting.presentation;

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

import com.sam.accounting.model.AccountLedger;
import com.sam.accounting.model.TransactionType;
import com.sam.accounting.model.dto.AccountValidation;
import com.sam.accounting.model.dto.FinancialStatementLine;
import com.sam.accounting.model.dto.IncomeStatementDto;
import com.sam.accounting.model.dto.LedgerEntryDTO;
import com.sam.accounting.model.dto.ReportAvailable;
import com.sam.accounting.model.dto.RevenueLine;
import com.sam.accounting.model.entities.Account;
import com.sam.accounting.model.entities.Asset;
import com.sam.accounting.model.entities.Invoice;
import com.sam.accounting.model.entities.KnownDescription;
import com.sam.accounting.model.entities.Transaction;
import com.sam.accounting.repository.AccountRepository;
import com.sam.accounting.repository.AssetRepository;
import com.sam.accounting.repository.InvoiceRepository;
import com.sam.accounting.service.AssetService;
import com.sam.accounting.service.CompanyProfileService;
import com.sam.accounting.service.FinancialStatementService;
import com.sam.accounting.service.LedgerService;
import com.sam.accounting.service.LedgerTransactionToDto;

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
    private final LedgerService ls;
	private final  LedgerTransactionToDto dtoParser;
	@Autowired
	public FinanceThimeleafController(LedgerService ls,LedgerTransactionToDto dtoParser,NavigationFixture navFixture, AssetService assetService,
			AssetRepository assetRepository, AccountRepository accountRepo, CompanyProfileService service,
			FinancialStatementService financeStatementService, InvoiceRepository invoiceRepo) {
		this.financeStatementService = financeStatementService;
		this.accountRepo = accountRepo;
		this.assetRepository = assetRepository;
		this.service = service;
		this.assetService = assetService;
		this.invoiceRepo = invoiceRepo;
		this.navFixture = navFixture;
		this.dtoParser=dtoParser;
		this.ls = ls;

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
	
	@GetMapping("/fy")
	public String fiscalYear(Model model) {
		List<Invoice> bills = new ArrayList<Invoice>();
		bills = invoiceRepo.findAll();
		model.addAttribute("currentPage", "FiscalYear");
		navFixture.insertOptions(2023, model);
		return "fiscal-year";
	}
	
	@GetMapping("/fy/{year}")
	public String fiscalYears(Model model, @PathVariable("year") Integer year) {
		List<Invoice> bills = new ArrayList<Invoice>();
		bills = invoiceRepo.findAll();
		model.addAttribute("bills", bills);
		navFixture.insertOptions(year, model);
		model.addAttribute("currentPage", "FiscalYear");
		model.addAttribute("currentFyPage", "Bills");
		return "fiscal-year";
	}
	
	@GetMapping("/bills")
	public String bills(Model model) {
		List<Invoice> bills = new ArrayList<Invoice>();
		bills = invoiceRepo.findAll();
		model.addAttribute("bills", bills);
		navFixture.insertOptions(2023, model);
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
		model.addAttribute("currentPage", "FiscalYear");
		model.addAttribute("currentFyPage", "Transaction");
		model.addAttribute("transaction_types",TransactionType.values());

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
			List<ReportAvailable> availabilities = this.financeStatementService.getReportAvailabilities(account.getAccountNo(), year);
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

	

	
	@GetMapping("/balance/{year}")
	public String balance(Model model, @PathVariable("year") Integer year) {
		if (year == null) {
			year = 2023;
		}
		
		model.addAttribute("date", Instant.now().toString());
		List<FinancialStatementLine> assets = assetService.getAssetFinantialStatement();
		model.addAttribute("assets", assets);
		navFixture.insertOptions(year, model);
		model.addAttribute("currentPage", "FiscalYear");
		model.addAttribute("currentFyPage", "Balance Sheet");
		return "BalanceSheet";
	}

	
	@GetMapping("/incomeStatement/{year}")
	public String incomeStatement(Model model, @PathVariable("year") Integer year) 
	{
		if (year == null) {
			year = 2023;
		}
		model.addAttribute("selected_report_type", "transactions");
		navFixture.insertOptions(year, model);
		model.addAttribute("currentPage", "FiscalYear");
		model.addAttribute("currentFyPage", "Income Statement");
		
		IncomeStatementDto dto = this.financeStatementService.incomeStatementService.generateIncomeStatement(year);
		//dto.expenseAccounts
		model.addAttribute("operatingExpenseAccounts",dto.operatingExpenseAccounts);
		model.addAttribute("otherExpenseAccounts",dto.otherExpenseAccounts);
		model.addAttribute("revenueAccounts",dto.revenueAccounts);
		model.addAttribute("totalRevenue",dto.totalRevenue);
		model.addAttribute("totalOperatingExpenses",dto.totalOperatingExpenses);
		model.addAttribute("totalOtherExpenses",dto.totalOtherExpenses);
		model.addAttribute("incomeBeforeTax", dto.incomeBeforeTax);
		model.addAttribute("incomeTax", dto.incomeTax);
		model.addAttribute("incomeAfterTax", dto.incomeAfterTax);
		List<LedgerEntryDTO> dtos = dtoParser.convertToLedgerEntryDTOs(dto.wb.getTransactions());
		model.addAttribute("ledgerEntries",dtos);
	    List<AccountLedger> ledgers = ls.createPagesPerAccounts(year);
		
		model.addAttribute("accounts_ledgers",ledgers);
		return "income-statement";
	}
	
	@GetMapping("/incomeStatement-old/{year}")
	public String incomeStatementOld(Model model, @PathVariable("year") Integer year) {
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
