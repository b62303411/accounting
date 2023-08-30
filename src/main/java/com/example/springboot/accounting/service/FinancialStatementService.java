package com.example.springboot.accounting.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.springboot.accounting.model.dto.ExpensesLine;
import com.example.springboot.accounting.model.dto.FinancialStatementLine;
import com.example.springboot.accounting.model.dto.RevenueLine;
import com.example.springboot.accounting.model.entities.BankStatement;
import com.example.springboot.accounting.model.entities.KnownDescription;
import com.example.springboot.accounting.model.entities.Transaction;
import com.example.springboot.accounting.repository.BankStatementRepository;
import com.example.springboot.accounting.repository.TransactionRepository;

@Service
public class FinancialStatementService {

	public final IncomeStatementService incomeStatementService;
	private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;
    private final BankStatementRepository bankStatementRepository;
    private HashMap<String,String> tooltipMap;
    public FinancialStatementService(IncomeStatementService incomeStatementService, TransactionRepository transactionRepository,BankStatementRepository bankStatementRepository,TransactionService transactionService) {
        this.transactionRepository = transactionRepository;
        this.bankStatementRepository=bankStatementRepository;
        this.transactionService=transactionService;
        this.incomeStatementService=incomeStatementService;
        
    }

    public List<FinancialStatementLine> getIncomeStatement(Integer year) {
    	return incomeStatementService.getIncomeStatementForFiscalYear(year);
    }
    
    public List<RevenueLine> getRevenues(Integer year) {
    	return incomeStatementService.getRevenuesForFiscalYear(year);
    }
	public List<ExpensesLine> getOtherExpenses(Integer year) {
		return incomeStatementService.getOtherExpenses(year);
	}
	public List<ExpensesLine> getExpenses(Integer year) {
		return incomeStatementService.getExpensesForFiscalYear(year);
	}
	public Map<String, Double> getExpenseReport(Integer year) {
		return incomeStatementService.getExpenseReport(year);
		
	}
  
    public List<FinancialStatementLine> getBalanceSheet(Date date) {
        // a method that retrieves and sums transactions up to a given date
        //List<FinancialStatementLine> balanceSheetLines = transactionRepository.sumTransactionsByTypeAndDate(date);
        return  new ArrayList<FinancialStatementLine>();
    }

    public List<FinancialStatementLine> getCashFlowStatement(Integer year) {
        // a method that retrieves and sums cash flow related transactions based on their type and date
        //List<FinancialStatementLine> cashFlowStatementLines = transactionRepository.sumCashFlowTransactionsByTypeAndYear(year);
        return new ArrayList<FinancialStatementLine>();
    }

    public List<Transaction> getTransactions(Integer year) {
		return transactionRepository.findAllTransactionsByYear(year);
	}
    

    /**
     * Income Tax: The amount of tax that the company is liable to pay based on its profit.
     */
    public double getIncomeTax() 
    {
    	return 0;
    }
    
    /**
     * Net Income: 
     * This is the company's total earnings, 
     * calculated by subtracting income taxes from the pretax income. 
     * This is often referred to as "the bottom line" as it generally appears at the 
     * bottom of the income statement.
     */
    public double netIncome() 
    {
    	return 0;
    }

    /**
     * 
     * @param year
     * @return
     */
	public List<BankStatement> getBankStatements(Integer year) {
		return bankStatementRepository.findAllByYear(year);
	}
	
	/**
	 * 
	 * @param accountNumber
	 * @param year
	 * @return
	 */
	public List<BankStatement> getBankStatements(String accountNumber, Integer year) {
		return bankStatementRepository.findAllByYearAndAcc(year,accountNumber);
	}
	
	/**
	 * 
	 * @return
	 */
	public List<KnownDescription> findAllKnownDescriptions()
	{
        return transactionService.getKnownDescriptions();
	}


	
}
