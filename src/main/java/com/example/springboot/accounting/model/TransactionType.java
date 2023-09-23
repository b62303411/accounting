package com.example.springboot.accounting.model;

public enum TransactionType {
	Unknown,
	Dividend(TransactionCategory.Equity),
	SalesRevenue(TransactionCategory.Revenue),
	CostOfGoods(TransactionCategory.Expense),
	Depreciation,//: This is a reduction in the value of an asset over time. It's an accounting method used to allocate the cost of a tangible asset over its useful life. Depreciation affects the value of an asset on the balance sheet and is also an expense on the income statement, but it doesn't involve an actual movement of cash, so it's not a cash flow.
	AccruedExpenses,//: These are expenses that a business has incurred but has not yet paid. For example, a business may receive goods or services from a supplier but not pay the invoice until a later date. The expense is recognized when the goods are received, but the cash doesn't actually leave the business until the invoice is paid.
	UnrealizedGainsLosses,//: These are increases or decreases in the value of an asset that a business holds, such as stocks or other investments, that have not yet been sold. These gains or losses are reflected on the income statement and can affect the value of assets on the balance sheet, but they don't involve an actual movement of cash unless the asset is sold.
    OperatingExpenses(TransactionCategory.Expense), 
    InvestmentIncome(TransactionCategory.Revenue), 
    LoanProceeds(TransactionCategory.Liability), //the money that you actually get from the bank or financial institution when you take out a loan.
    DeptRepayment(TransactionCategory.Liability), 
    AssetSales(TransactionCategory.Asset), 
    AssetPurchased(TransactionCategory.Asset),
    Debit,
    Credit,
    Transfer,
    Liability,
    Cash,
    Income(TransactionCategory.Revenue),
    BankFees(TransactionCategory.Expense), // For charges and potentially refunds
    Refunds(TransactionCategory.Revenue); // If you want to categorize refunds separately
 
	TransactionType() {
		this.category = null;
	}

	TransactionType(TransactionCategory category) {
		this.category = category;
	}

	public TransactionCategory getCategory() {
		return category;
	}

	private TransactionCategory category;
}
