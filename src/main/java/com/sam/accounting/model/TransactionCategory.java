package com.sam.accounting.model;

public enum TransactionCategory {
	Revenue, // Income generated from business operations, like sales or services.
	Expense, // Costs associated with running the business, including COGS, operating
				// expenses, etc.
	Investment, // Income or expenses related to investments such as dividends, interest,
				// purchase/sale of assets.
	Liability, // Transactions related to borrowing, lending, and repayments.
	Equity, // Transactions related to owners' equity, including capital contributions,
			// withdrawals, dividends.
	OtherIncome, // Other miscellaneous income not related to normal business operations.
	OtherExpense, // Other miscellaneous expenses not related to normal business operations.
	Asset, // Transactions related to assets including acquisition, depreciation, sales.
	Adjustment // Transactions used for adjustments, accruals, corrections, etc.
	// ... other categories as needed for your specific business context.
}
