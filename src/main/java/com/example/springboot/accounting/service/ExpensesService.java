package com.example.springboot.accounting.service;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springboot.accounting.model.ExpenseKey;
import com.example.springboot.accounting.model.ExploitationExpenseType;
import com.example.springboot.accounting.model.FiscalYearEnd;
import com.example.springboot.accounting.model.SalesTaxeRates;
import com.example.springboot.accounting.model.dto.ExpenseDuplicateGroup;
import com.example.springboot.accounting.model.dto.ExpenseUpdateDto;
import com.example.springboot.accounting.model.entities.AmortisationLeg;
import com.example.springboot.accounting.model.entities.Asset;
import com.example.springboot.accounting.model.entities.ExploitationExpense;
import com.example.springboot.accounting.model.entities.Transaction;
import com.example.springboot.accounting.repository.AmortisationLegRepository;
import com.example.springboot.accounting.repository.ExploitationExpenseRepository;
import com.example.springboot.accounting.repository.TransactionRepository;

@Service
public class ExpensesService {
	private final CompanyProfileService profile;
	ExploitationExpenseRepository repo;
	TransactionRepository transactionRepository;
	private final AmortisationLegRepository legRepository;
	AssetService assetService;

	@Autowired
	public ExpensesService(AmortisationLegRepository legRepository, CompanyProfileService profile,
			AssetService assetService, ExploitationExpenseRepository repo,
			TransactionRepository transactionRepository) {
		this.repo = repo;
		this.transactionRepository = transactionRepository;
		this.assetService = assetService;
		this.profile = profile;
		this.legRepository = legRepository;
	}

	public void fixExpense(String description, ExploitationExpenseType type) {
		List<ExploitationExpense> expenses = repo.findAllLikeDescription(description);
		boolean changed = false;
		for (ExploitationExpense exploitationExpense : expenses) {
			if (exploitationExpense.getExpenseType() == null) {
				exploitationExpense.setExpenseType(type);
				changed = true;
			}
		}
		if (changed)
			repo.saveAll(expenses);
	}

	public ExploitationExpense updateExpense(Long id, ExpenseUpdateDto expenseDto) {
		Optional<ExploitationExpense> expense = repo.findById(id);
		expense.get().setExpenseType(expenseDto.getExpenseType());
		repo.save(expense.get());
		return expense.get();
	}

	public int inferFromTransactions() {
		List<Transaction> value = transactionRepository.findAllExpensesTransactions();
		return inferExpensesFromTransactions(value);
	}

	public int removeExpenseTransactions() {
		int row = 0;
		List<Transaction> value = transactionRepository.findAll();
		for (Transaction transaction : value) {
			if (null == transaction.getType()) {
				ExploitationExpense ex = repo.findByTransaction(transaction);
				if (null != ex) {
					deleteExploitationExpense(ex);
				}
			} else {
				switch (transaction.getType()) {
				case OperatingExpenses:
				case Depreciation:
				case AccruedExpenses:
					break;
				default:
					ExploitationExpense ex = repo.findByTransaction(transaction);
					if (null != ex) {
						deleteExploitationExpense(ex);
					}

				}
			}

		}
		return row;

	}

	private void deleteExploitationExpense(ExploitationExpense ex) {
		repo.delete(ex);

	}

	public int inferExpensesFromTransactions(List<Transaction> transactions) {

		int rowAffected = 0;
		for (Transaction transaction : transactions) {
			boolean valueChanged = false;
			ExploitationExpense ex = repo.findByTransaction(transaction);
			if (null == ex) {
				ex = new ExploitationExpense();
				ex.setTransaction(transaction);
				updateValueFromStransaction(transaction, ex);
				repo.save(ex);
				rowAffected++;
			} else {
				if (ex.getDate() == null && transaction.getDate() != null) {
					valueChanged = true;
					ex.setDate(transaction.getDate());
				}
				if (ex.getDescription() == null && transaction.getDescription() != null) {
					valueChanged = true;
					ex.setDescription(transaction.getDescription());
				}
				if (ex.getTotalBeforeSalesTaxes() == 0) {
					valueChanged = true;
					updateValueFromStransaction(transaction, ex);
				}
				if (valueChanged) {
					repo.save(ex);
					rowAffected++;
				}

			}
		}
		return rowAffected;
	}

	private void updateValueFromStransaction(Transaction transaction, ExploitationExpense ex) {
		double total;
		switch (transaction.getTransactionNature()) {
		case Credit:
			total = Math.abs(transaction.getAmount());
			break;
		case Debit:
			total = -Math.abs(transaction.getAmount());
			break;
		default:
			total = transaction.getAmount();
		}
		if (ex.getExpenseType() == null) {
			invertTpsTvq(ex, total);
			ex.setExpenseType(ExploitationExpenseType.Unknown);
		} else {
			switch (ex.getExpenseType()) {
			case Interets:
			case FraisBancaires:
			case Amortisation:
			case Immobilisation:
			case Taxes:
				ex.setTotalBeforeSalesTaxes(total);
				break;

			default:
				invertTpsTvq(ex, total);

			}
		}
	}

	public void inferExpensesFromTransactions(Date start, Date stop) {

		// fixExpense();

		List<Transaction> value = transactionRepository.getExpensesTransactionsForFiscalYear(start, stop);
		inferExpensesFromTransactions(value);

	}

	public void invertTpsTvq(ExploitationExpense ex, double total) {
		double TPS_RATE = SalesTaxeRates.TPS_RATE;
		double TVQ_RATE = SalesTaxeRates.TVQ_RATE;
		double beforeTaxes = total / (1 + TPS_RATE + TPS_RATE * TVQ_RATE + TVQ_RATE);
		// Calculate TPS and TVQ
		double tps = beforeTaxes * TPS_RATE;
		double tvq = (beforeTaxes + tps) * TVQ_RATE;
		ex.setTps(tps);
		ex.setTvq(tvq);
		ex.setTotalBeforeSalesTaxes(beforeTaxes);
	}

	public int inferFromAssetLegs() {
		int row = 0;
		List<Asset> assetList = assetService.findAll();
		for (Asset asset : assetList) {
			for (AmortisationLeg leg : asset.getDepreciationLegs()) {
				if ((leg.getExpense() == null || leg.getDate() == null) && leg.isRealized()) {
					int fy = leg.getFiscalYear();
					inferFromLeg(fy, asset, leg);
					row++;
				}
			}
		}
		return row;
	}

	private void inferExpensesFromAssetDepreciation(Date start) {
		List<Asset> assetList = assetService.findAll();
		int fy = getFiscalYear(start);
		for (Asset asset : assetList) {
			for (AmortisationLeg leg : asset.getDepreciationLegs()) {

				if (leg.getFiscalYear() == fy && (leg.getExpense() == null || leg.getDate() == null)) {

					inferFromLeg(fy, asset, leg);

				}
			}

		}

	}

	private void inferFromLeg(int fy, Asset asset, AmortisationLeg leg) {
		ExploitationExpense e = new ExploitationExpense();
		e.setTotalBeforeSalesTaxes(-leg.getAmount());
		e.setExpenseType(ExploitationExpenseType.Amortisation);
		e.setDescription("Depreciation " + asset.getPurchaceTransaction().getDescription());
		if (leg.getDate() == null) {
			FiscalYearEnd fye = profile.getProfile().getFiscalYearEnd();
			Date date = fye.getLastDayDate(fy);
			leg.setDate(date);
		}
		e.setDate(leg.getDate());
		e = repo.save(e);
		leg.setExpense(e);
		legRepository.save(leg);
	}

	private void fixExpense() {
		List<ExploitationExpense> expenses = repo.findAllLikeDescription("Depreciation ");
		boolean changed = false;
		for (ExploitationExpense exploitationExpense : expenses) {
			if (exploitationExpense.getExpenseType() == null) {
				exploitationExpense.setExpenseType(ExploitationExpenseType.Amortisation);
				changed = true;
			}

		}
		if (changed)
			repo.saveAll(expenses);
	}

	public int getFiscalYear(Date start) {

		System.out.println("Today's date: " + start);
		Calendar calendar = Calendar.getInstance();
		// Create a Calendar instance and set it to the current date

		calendar.setTime(start);
		// Add one day to the current date
		calendar.add(Calendar.DATE, 1);
		Date tomorrow = calendar.getTime();

		return profile.getProfile().getFiscalYearEnd().getFiscalYear(tomorrow);
	}

	public List<ExpenseDuplicateGroup> getDuplicates() {
		List<ExploitationExpense> allExpenses = repo.findAll(); // Fetch or initialize your list of expenses

		// Step 1: Group the expenses by ExpenseKey
		Map<ExpenseKey, List<ExploitationExpense>> groupedByExpenseKey = allExpenses.stream()
				.collect(Collectors.groupingBy(
						expense -> new ExpenseKey(expense.getTotal(), expense.getDate(), expense.getPayee()),
						LinkedHashMap::new, Collectors.toList()));

		// Step 2: Transform the grouped data into ExpenseDuplicateGroup objects
		List<ExpenseDuplicateGroup> groupedExpenses = groupedByExpenseKey.entrySet().stream().map(entry -> {
			ExpenseDuplicateGroup group = new ExpenseDuplicateGroup();
			group.setExpenses(entry.getValue());
			group.setKey(entry.getKey());
			return group;
		}).collect(Collectors.toList());

		// Step 3: Filter out groups that don't have duplicates
		List<ExpenseDuplicateGroup> duplicates = groupedExpenses.stream()
				.filter(group -> group.getExpenses().size() > 1).collect(Collectors.toList());

		return duplicates;
	}

	public void mergeExpenses(List<Long> ids) {
		// Logic to merge expenses
		// 1. Fetch all expenses with those IDs
		// 2. Determine which one to keep and which ones to delete
		// 3. If there are related transactions, handle them as well
		// 4. Persist any changes

		List<ExploitationExpense> expenses = repo.findAllById(ids);

		boolean allSameTransactions = validateExpensesHaveSameTransactions(expenses);
		if(allSameTransactions) 
		{
			
		}
		else 
		{
			
			System.err.println();
		}
		// Your merge logic here. For simplicity, we'll just remove all but the first:
		ExploitationExpense retainedExpense = expenses.get(0);

		for (int i = 1; i < expenses.size(); i++) {
			// Handle related transactions if any, then
			repo.delete(expenses.get(i));
			Transaction t = expenses.get(i).getTransaction();
			transactionRepository.delete(t);
		}

		// Update retainedExpense if needed, then save
		// expenseRepository.save(retainedExpense);
	}

	private boolean validateExpensesHaveSameTransactions(List<ExploitationExpense> expenses) {
		if (expenses == null || expenses.isEmpty())
			return false;

		Transaction firstExpenseTransactions = expenses.get(0).getTransaction();
		for (int i = 1; i < expenses.size(); i++) {
			Transaction currentExpenseTransactions = expenses.get(i).getTransaction();
			if(!firstExpenseTransactions.equals(currentExpenseTransactions))
				return false;
			
		}
		return true;
	}

	public List<ExploitationExpense> findAllByPayee(String name) {
		return repo.findAllByPayee(name);
		
	}

	public List<ExploitationExpense> findAll() {
		return repo.findAll();
		
	}

	public ExploitationExpense findById(Long id) {
		return repo.findById(id).get();
	}

	public ExploitationExpense save(ExploitationExpense expense) {
		return repo.save(expense);
		
	}

	public ExploitationExpense findByTransaction(Transaction transaction) {
		return repo.findByTransaction(transaction);
	}

}
