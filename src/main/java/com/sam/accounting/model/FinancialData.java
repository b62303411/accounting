package com.sam.accounting.model;

import java.util.HashMap;
import java.util.Map;

public class FinancialData {
	public Map<String, Double> balanceSheetAssets;
	public Map<String, Double> balanceSheetLiabilitiesAndEquity;
	public Map<String, Double> incomeStatementData;

	public FinancialData() {
		balanceSheetAssets = new HashMap<>();
		incomeStatementData = new HashMap<>();
		balanceSheetLiabilitiesAndEquity = new HashMap<>();
	}

	public void populateSampleData() {
		// Balance Sheet - Assets
		balanceSheetAssets.put("Cash", 12000.0);
		balanceSheetAssets.put("Accounts Receivable", 8000.0);
		balanceSheetAssets.put("Inventory", 15000.0);
		balanceSheetAssets.put("Prepaid Expenses", 3000.0);
		balanceSheetAssets.put("Total Current Assets", 40000.0);
		balanceSheetAssets.put("Property, Plant, and Equipment", 80000.0);
		balanceSheetAssets.put("Intangible Assets", 15000.0);
		balanceSheetAssets.put("Total Non-Current Assets", 95000.0);
		balanceSheetAssets.put("TOTAL ASSETS", 135000.0);

		// Balance Sheet - Liabilities & Equity
		balanceSheetLiabilitiesAndEquity.put("Accounts Payable", 5000.0);
		balanceSheetLiabilitiesAndEquity.put("Short-Term Debt", 10000.0);
		balanceSheetLiabilitiesAndEquity.put("Total Current Liabilities", 15000.0);
		balanceSheetLiabilitiesAndEquity.put("Long-Term Debt", 40000.0);
		balanceSheetLiabilitiesAndEquity.put("Owner's Equity", 50000.0);
		balanceSheetLiabilitiesAndEquity.put("Retained Earnings", 30000.0);
		balanceSheetLiabilitiesAndEquity.put("TOTAL LIABILITIES & EQUITY", 135000.0);

		// Income Statement
		incomeStatementData.put("Sales Revenue", 250000.0);
		incomeStatementData.put("Cost of Goods Sold", -150000.0);
		incomeStatementData.put("Gross Profit", 100000.0);
		incomeStatementData.put("Operating Expenses", -50000.0);
		incomeStatementData.put("Net Income", 50000.0);
	}
}
