package com.example.springboot.accounting.model.entities.qb;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.example.springboot.accounting.TestFixture;
import com.example.springboot.accounting.model.dto.TransactionDTO;

class FinancialRecordTest {

	/**
	 * Yes, in the context of accounting and financial record-keeping, an expense is
	 * considered a type of transaction. Transactions are the fundamental building
	 * blocks of accounting, representing various financial activities and events
	 * that impact a company's financial position.
	 * 
	 * An expense transaction involves the outflow of money or other valuable
	 * resources from a business to cover costs or expenditures. This can include
	 * payments for goods, services, salaries, utilities, and other operational
	 * expenses. When you record an expense transaction, you are capturing the
	 * details of the expense, such as the amount, date, payee, and accounts
	 * affected.
	 * 
	 * In accounting, transactions are typically categorized into different types,
	 * such as:
	 * 
	 * Expense Transactions: These involve the payment of money or other assets to
	 * cover costs and expenditures related to the business's operations.
	 * 
	 * Revenue Transactions: These represent the inflow of money or other resources
	 * into the business, such as sales revenue, interest income, and other sources
	 * of income.
	 * 
	 * Asset Transactions: These involve changes in the ownership or value of the
	 * business's assets, such as purchasing equipment, vehicles, or property.
	 * 
	 * Liability Transactions: These involve changes in the business's obligations
	 * or debts, such as taking out loans, paying off debt, or accruing interest.
	 * 
	 * Equity Transactions: These reflect changes in the owner's equity or the
	 * company's capital, such as owner investments, withdrawals, and retained
	 * earnings.
	 * 
	 * By categorizing financial activities into different transaction types,
	 * businesses can track and analyze their financial performance, prepare
	 * financial statements, and make informed decisions about their operations.
	 * 
	 * So, in summary, an expense is indeed a type of transaction in accounting, and
	 * recording expense transactions is essential for accurately representing a
	 * business's financial activities and maintaining reliable financial records.
	 * 
	 * 
	 * 
	 * 
	 * User ok and model wise the transaction in the bank account and the finantial
	 * reccord how would they be modeled in java ? ChatGPT In Java, you can model
	 * bank transactions and financial records using classes and objects that
	 * represent the various components of these entities. Here's a simplified
	 * example of how you might model bank transactions and financial records:
	 * 
	 * java Copy code import java.time.LocalDate;
	 * 
	 * public class BankTransaction { private LocalDate date; private String
	 * description; private double amount;
	 * 
	 * // Constructor, getters, setters, and other methods }
	 * 
	 * public class FinancialRecord { private String transactionType; // Expense,
	 * Revenue, etc. private LocalDate transactionDate; private String description;
	 * private double amount; private String account; // Associated account for the
	 * transaction
	 * 
	 * // Constructor, getters, setters, and other methods } In this example,
	 * BankTransaction represents an individual bank transaction, and
	 * FinancialRecord represents a financial transaction or record that is part of
	 * your business's accounting system.
	 * 
	 * You might use these classes as follows:
	 * 
	 * java Copy code // Creating a bank transaction BankTransaction bankTransaction
	 * = new BankTransaction(); bankTransaction.setDate(LocalDate.now());
	 * bankTransaction.setDescription("Withdrawal for dividend payment");
	 * bankTransaction.setAmount(-1000.0); // Negative amount for withdrawals
	 * 
	 * // Creating a financial record for an expense transaction FinancialRecord
	 * expenseRecord = new FinancialRecord();
	 * expenseRecord.setTransactionType("Expense");
	 * expenseRecord.setTransactionDate(LocalDate.now());
	 * expenseRecord.setDescription("Dividend payment to owner");
	 * expenseRecord.setAmount(1000.0); // Positive amount for expenses
	 * expenseRecord.setAccount("Dividends Paid"); // Example account
	 * 
	 * // Similarly, you can create financial records for revenue, asset, liability
	 * transactions, etc. Keep in mind that this is a simplified example. In a
	 * real-world application, you might have more attributes and methods to handle
	 * various aspects of transactions, such as categorization, reconciliation, and
	 * generating financial statements. Additionally, frameworks like Spring Boot
	 * and JPA can be used to facilitate database storage and manipulation of these
	 * transaction objects.
	 * 
	 * 
	 * 
	 * 
	 * User i tougth the convention was to allways have positive amount but then
	 * have debit or credit to define if it was an addition or substraction ChatGPT
	 * You're absolutely correct, and I apologize for any confusion. In accounting,
	 * the convention is indeed to use positive amounts and then indicate whether a
	 * transaction is a debit or credit based on its effect on accounts.
	 * 
	 * Here's the corrected example using the conventional accounting approach:
	 * 
	 * java Copy code import java.time.LocalDate;
	 * 
	 * public class BankTransaction { private LocalDate date; private String
	 * description; private double amount;
	 * 
	 * // Constructor, getters, setters, and other methods }
	 * 
	 * public class FinancialRecord { private String transactionType; // Expense,
	 * Revenue, etc. private LocalDate transactionDate; private String description;
	 * private double amount; private String account; // Associated account for the
	 * transaction private String debitOrCredit; // Debit or Credit
	 * 
	 * // Constructor, getters, setters, and other methods } And the usage:
	 * 
	 * java Copy code // Creating a bank transaction BankTransaction bankTransaction
	 * = new BankTransaction(); bankTransaction.setDate(LocalDate.now());
	 * bankTransaction.setDescription("Withdrawal for dividend payment");
	 * bankTransaction.setAmount(1000.0); // Positive amount for withdrawals
	 * 
	 * // Creating a financial record for an expense transaction FinancialRecord
	 * expenseRecord = new FinancialRecord();
	 * expenseRecord.setTransactionType("Expense");
	 * expenseRecord.setTransactionDate(LocalDate.now());
	 * expenseRecord.setDescription("Dividend payment to owner");
	 * expenseRecord.setAmount(1000.0); // Positive amount for expenses
	 * expenseRecord.setAccount("Dividends Paid"); // Example account
	 * expenseRecord.setDebitOrCredit("Credit"); // Indicate if it's a credit
	 * 
	 * // Similarly, you can create financial records for revenue, asset, liability
	 * transactions, etc. Thank you for pointing out the correct convention, and I
	 * apologize for any confusion caused by my previous response.
	 * 
	 * 
	 * 
	 * 
	 * User and do we need to keep track of who is getting paid ? are account like
	 * dividents paid re stored in the same type of account has bank account ?
	 * ChatGPT Yes, keeping track of who is getting paid is important in accounting,
	 * especially when dealing with transactions like dividend payments. Dividend
	 * payments are typically made to the owners or shareholders of a company, and
	 * it's essential to record who is receiving the payment and for what purpose.
	 * 
	 * In accounting, different types of accounts are used to track various
	 * financial activities. Dividends Paid is an example of an account that is used
	 * to track dividend payments to owners or shareholders. This account is not the
	 * same as a bank account; it is an account category used in the company's chart
	 * of accounts to categorize and track dividend-related transactions.
	 * 
	 * Here's how it works:
	 * 
	 * Bank Account: This represents the actual bank account where financial
	 * transactions occur. Bank accounts are used to track the inflow and outflow of
	 * funds, and they store the actual money owned by the business.
	 * 
	 * Dividends Paid Account: This account is used to track dividend payments made
	 * to owners or shareholders. It's a special account category in the chart of
	 * accounts and falls under the equity section. It's not a bank account but
	 * rather a ledger account that keeps track of dividend payments as part of the
	 * company's financial records.
	 * 
	 * When you make a dividend payment, the entry is typically recorded in both the
	 * bank account and the Dividends Paid account. The bank account entry reflects
	 * the actual outflow of funds from the bank account, while the Dividends Paid
	 * account entry represents the accounting record of the dividend payment to
	 * owners.
	 * 
	 * For example, the Dividends Paid account might be categorized as an equity
	 * account with the following details:
	 * 
	 * Account Name: Dividends Paid Account Type: Equity Description: Represents
	 * dividend payments made to owners or shareholders So, to answer your question,
	 * while Dividends Paid is not a bank account, it is a distinct account used to
	 * track dividend-related transactions in the company's financial records.
	 * 
	 * 
	 * 
	 * 
	 * User and should we use the same class or is there any different fields that
	 * require this to be separate objects ChatGPT In most accounting systems, you
	 * would use separate classes or objects to represent different types of
	 * accounts and transactions. While there may be some fields shared between
	 * different types of transactions, it's generally a good practice to keep
	 * separate classes for clarity and to accurately model the different aspects of
	 * accounting.
	 * 
	 * Here's an example of how you might structure your classes to represent
	 * different types of accounts and transactions:
	 * 
	 * java Copy code import java.time.LocalDate;
	 * 
	 * public class BankTransaction { private LocalDate date; private String
	 * description; private double amount;
	 * 
	 * // Constructor, getters, setters, and other methods }
	 * 
	 * public class FinancialRecord { private String transactionType; // Expense,
	 * Revenue, etc. private LocalDate transactionDate; private String description;
	 * private double amount; private String account; // Associated account for the
	 * transaction private String debitOrCredit; // Debit or Credit
	 * 
	 * // Constructor, getters, setters, and other methods }
	 * 
	 * public class DividendPayment { private LocalDate paymentDate; private String
	 * recipient; // Owner or shareholder receiving the dividend private double
	 * amount;
	 * 
	 * // Constructor, getters, setters, and other methods }
	 * 
	 * public class Account { private String accountName; private String
	 * accountType; private String description;
	 * 
	 * // Constructor, getters, setters, and other methods } In this example, the
	 * BankTransaction class represents bank transactions, the FinancialRecord class
	 * represents various financial transactions, the DividendPayment class
	 * represents dividend payments, and the Account class represents different
	 * types of accounts in the company's chart of accounts.
	 * 
	 * By using separate classes, you can maintain a clear and organized code
	 * structure that accurately reflects the different aspects of accounting. Each
	 * class can have its own set of attributes, methods, and behaviors specific to
	 * its role in the accounting process. This separation also makes it easier to
	 * extend and maintain the codebase as your application's accounting
	 * requirements evolve.
	 * 
	 * Log In to QuickBooks Online: Log in to your QuickBooks Online account and
	 * make sure you have the necessary permissions to record transactions.
	 * 
	 * Navigate to the Transactions Menu: Depending on the version of QuickBooks
	 * Online, you might need to navigate to different sections. Look for the
	 * "Transactions" or "Expenses" menu.
	 * 
	 * Create an Expense Transaction: Create a new expense transaction for the
	 * dividend payment. Choose the date of the payment and select the appropriate
	 * bank account from which the payment will be made.
	 * 
	 * Add Payee Information: In the expense transaction, add the payee information.
	 * This could be the owner's name or a related account if you have set it up
	 * that way.
	 * 
	 * Categorize the Expense: Categorize the expense appropriately. Dividend
	 * payments are typically categorized under an account like "Dividends Paid" or
	 * "Owner's Withdrawals."
	 * 
	 * Enter Payment Details: Enter the payment amount and any additional details.
	 * You might also include a memo or note explaining the purpose of the payment.
	 * 
	 * Save the Transaction: Save the expense transaction. This will record the
	 * dividend payment in your QuickBooks Online account.
	 * 
	 * Reconcile Bank Account: After recording the expense, make sure to reconcile
	 * the bank account associated with the payment. This will ensure that your
	 * records match your actual bank statements.
	 * 
	 * Review Reports: QuickBooks provides various reports that allow you to track
	 * dividend payments and other financial activities. You can generate reports to
	 * see a summary of dividend payments over a specific period.
	 * 
	 * 
	 * 
	 * 
	 */
//
//	String checking = "TD_EVERY_DAY_A_BUSINESS_PLAN";
//	String credit = "TD BUSINESS VISA";

	@Test
	void test() {

//		// Creating a bank transaction
//		BankTransaction bankTransaction = new BankTransaction(null);
//		bankTransaction.setDate(Date.from(Instant.now()));
//		bankTransaction.setDescription("Withdrawal for dividend payment");
//		bankTransaction.setAmount(-1000.0); // Negative amount for withdrawals
//
//		// Creating a financial record for an expense transaction
//		FinancialRecord expenseRecord = new FinancialRecord(null);
//		expenseRecord.setTransactionType("Expense");
//		expenseRecord.setDate(Date.from(Instant.now()));
//		expenseRecord.setDescription("Dividend payment to owner");
//		expenseRecord.setAmount(1000.0); // Positive amount for expenses
//		expenseRecord.setAccount("Dividends Paid"); // Example account
//		expenseRecord.setDebitOrCredit("Credit"); // Indicate if it's a credit
//		fail("Not yet implemented");
	}

	@Test
	public void testBalancedBooksAfterDividendPayment() {
//		AccountManager accountManager = new AccountManager();
//
//		accountManager.addAccount("CREDIT_CARD", "003", AccountType.LIABILITY, false);
//		accountManager.addAccount("CHECKING", "002", AccountType.ASSET, false);
//		accountManager.addAccount("INVESTMENT", "004", AccountType.ASSET, false);
//		// Simulate a dividend payment
//		FinancialRecord dividendRecord = new FinancialRecord(null);
//		dividendRecord.setTransactionType("Expense");
//		dividendRecord.setDate(Date.from(Instant.now()));
//		dividendRecord.setDescription("Dividend payment to owner");
//		dividendRecord.setAmount(1000.0); // Positive amount for expenses
//		dividendRecord.setAccount("Dividends Paid");
//
//		// Simulate a corresponding bank withdrawal
//		BankTransaction bankWithdrawal = new BankTransaction(null);
//		bankWithdrawal.setDate(Date.from(Instant.now()));
//		bankWithdrawal.setDescription("Withdrawal for dividend payment");
//		bankWithdrawal.setAmount(-1000.0); // Negative amount for withdrawals
//
//		// Simulate an offsetting bank deposit
//		BankTransaction bankDeposit = new BankTransaction(null);
//		bankDeposit.setDate(Date.from(Instant.now()));
//		bankDeposit.setDescription("Offsetting deposit for dividend payment");
//		bankDeposit.setAmount(1000.0); // Positive amount for deposits
//
//		// Create a list of transactions
//		List<Transaction> transactions = new ArrayList<Transaction>();
//		transactions.add(dividendRecord);
//		transactions.add(bankWithdrawal);
//		transactions.add(bankDeposit);
//
//		// Validate that the transactions balance out
//		double totalDebits = transactions.stream().mapToDouble(Transaction::getAmount).filter(amount -> amount < 0)
//				.sum();
//		double totalCredits = transactions.stream().mapToDouble(Transaction::getAmount).filter(amount -> amount > 0)
//				.sum();
//
//		assertEquals(totalDebits, totalCredits, 0.001); // Tolerate small rounding errors
//
//		// Additional validation or business logic checks can be added here
	}

	

	/**
	 * Purchasing Items with a Split Payment: Suppose a business buys equipment
	 * worth $1,000 and decides to pay $700 in cash and put $300 on credit. This
	 * transaction impacts three accounts: Equipment (Asset), Cash (Asset), and
	 * Accounts Payable (Liability). The journal entries would be: Debit Equipment:
	 * $1,000 Credit Cash: $700 Credit Accounts Payable: $300
	 */
	@Test
	void testSplitPaymentTransaction() {
//		TestFixture f = new TestFixture();
//		Date date = Date.from(Instant.now());
//		Sequence seq = new Sequence();
//		// Creating a split payment transaction
//		Transaction purchaseEquipment = new Transaction(seq);
//
//		String vendor_client = "";
//		// Equipment is increased by $1,000
//		purchaseEquipment.addEntry(new TransactionEntry(f.equipment, vendor_client, date, 1000, EntryType.DEBIT));
//
//		// Cash is decreased by $700
//		purchaseEquipment.addEntry(new TransactionEntry(f.cash, vendor_client, date, 700, EntryType.CREDIT));
//
//		// Accounts Payable (liability) is increased by $300
//		purchaseEquipment.addEntry(new TransactionEntry(f.payable, vendor_client, date, 300, EntryType.CREDIT));
//
//		f.ledger.postTransaction(purchaseEquipment);
//
//		// Asserting the final balances
//		assertEquals(1000, f.equipment.getBalance());
//		assertEquals(-700, f.cash.getBalance()); // Cash is reduced
//		assertEquals(300, f.payable.getBalance()); // Liability is increased
	}

	/**
	 * Sales with Sales Tax: If a business sells a product worth $500 and collects
	 * $30 in sales tax, the revenue is split between the sales and the sales tax
	 * payable account. The transaction impacts three accounts: Cash (or Accounts
	 * Receivable if sold on credit), Sales Revenue, and Sales Tax Payable.
	 * 
	 * Debit Cash/Accounts Receivable: $530 Credit Sales Revenue: $500 Credit Sales
	 * Tax Payable: $30
	 */
	@Test
	void testSalesWithSalesTaxTransaction() {
//		TestFixture f = new TestFixture();
//		Sequence seq = new Sequence();
//		// Creating a sales with sales tax transaction
//		Transaction salesTransaction = new Transaction(seq);
//
//		Date date = Date.from(Instant.now());
//		String vendor_client = "";
//		// Cash is increased by the total amount including tax: $530
//		salesTransaction.addEntry(new TransactionEntry(f.cash, vendor_client, date, 530, EntryType.DEBIT));
//
//		// Sales Revenue is credited by the product's price: $500
//		salesTransaction.addEntry(new TransactionEntry(f.salesRevenue, vendor_client, date, 500, EntryType.CREDIT));
//
//		// Sales Tax Payable is credited by the tax amount: $30
//		salesTransaction.addEntry(new TransactionEntry(f.salesTaxPayable, vendor_client, date, 30, EntryType.CREDIT));
//
//		f.ledger.postTransaction(salesTransaction);
//
//		// Asserting the final balances
//		assertEquals(530, f.cash.getBalance());
//		assertEquals(500, f.salesRevenue.getBalance()); // Sales Revenue is credited
//		assertEquals(30, f.salesTaxPayable.getBalance()); // Sales Tax Payable is credited
	}

	/**
	 * Distributing Dividends: A company might decide to distribute dividends from
	 * its Retained Earnings to its shareholders. It impacts Dividends Declared (an
	 * equity account), Retained Earnings, and Cash (when dividends are paid).
	 * 
	 * Debit Retained Earnings for the dividend declared. Credit Dividends Declared.
	 * Later, when paying the dividends: Debit Dividends Declared. Credit Cash.
	 * Alright, let's create a unit test for a dividend declaration and payment
	 * scenario:
	 * 
	 * A company might decide to distribute dividends from its Retained Earnings to
	 * its shareholders. Let's say the total dividend amount is $300.
	 * 
	 * There are typically two main steps when handling dividends:
	 * 
	 * Declaration of Dividends: When a company declares dividends, it creates a
	 * liability (Dividends Declared). At this point, the money hasn't left the
	 * company yet, but the company has made a promise to pay its shareholders.
	 * 
	 * Debit Retained Earnings: $300 Credit Dividends Declared: $300 Payment of
	 * Dividends: When the company pays out the dividends to shareholders, it
	 * reduces its cash and clears the dividend liability.
	 * 
	 * Debit Dividends Declared: $300 Credit Cash: $300
	 */

	@Test
	public void testDividendPaymentToString() {
//		TestFixture f = new TestFixture();
//		Sequence seq = new Sequence();
//		// Step 1: Declaring the Dividends
//		Transaction declareDividends = new Transaction(seq);
//
//		Date date = Date.from(Instant.now());
//		String vendor_client = "";
//		// Retained Earnings is decreased by the declared dividend amount
//		declareDividends.addEntry(new TransactionEntry(f.retainedEarnings, vendor_client, date, 300, EntryType.DEBIT));
//
//		// Dividends Declared (liability) is increased by the declared amount
//		declareDividends
//				.addEntry(new TransactionEntry(f.dividendsDeclared, vendor_client, date, 300, EntryType.CREDIT));
//
//		f.ledger.postTransaction(declareDividends);
//
//		// Asserting balances after declaring dividends
//		assertEquals(-300, f.retainedEarnings.getBalance()); // Retained Earnings is debited
//		assertEquals(300, f.dividendsDeclared.getBalance()); // Dividends Declared is credited
//
//		// Step 2: Paying the Dividends
//		Transaction payDividends = new Transaction(seq);
//
//		// Dividends Declared (liability) is decreased by the payment amount
//		payDividends.addEntry(new TransactionEntry(f.dividendsDeclared, vendor_client, date, 300, EntryType.DEBIT));
//
//		// Cash is decreased by the payment amount
//		payDividends.addEntry(new TransactionEntry(f.cash, vendor_client, date, 300, EntryType.CREDIT));
//
//		f.ledger.postTransaction(payDividends);
//
//		// Asserting balances after paying dividends
//		assertEquals(0, f.dividendsDeclared.getBalance()); // Dividends Declared is back to zero
//		assertEquals(-300, f.cash.getBalance()); // Cash is reduced
	}

	@Test
	public void testReal() {

//		TestFixture f = new TestFixture();
//		Sequence seq = new Sequence();
//		Transaction depositToChecking = new Transaction(seq);
//		Date date = Date.from(Instant.now());
//		depositToChecking.addEntry(new TransactionEntry(f.checking, "", date, 1000, EntryType.DEBIT));
//
////		Transaction transferToSavings = new Transaction();
////		transferToSavings.addEntry(new TransactionEntry(checking, 500, "Credit"));
////		transferToSavings.addEntry(new TransactionEntry(savings, 500, "Debit"));
//
//	
//		Transaction payCreditCard = new Transaction(seq );
//		payCreditCard.addEntry(new TransactionEntry(f.checking, "", date, 200, EntryType.CREDIT));
//		payCreditCard.addEntry(new TransactionEntry(f.creditCard, "", date, 200, EntryType.DEBIT));
//
//		Transaction earnInvestment = new Transaction(seq);
//		earnInvestment.addEntry(new TransactionEntry(f.investment, "", date, 100, EntryType.DEBIT));
//
//		Transaction amazonInvoice = new Transaction(seq);
//		amazonInvoice.addEntry(new TransactionEntry(f.amazon, "", date, 200, EntryType.CREDIT));
//		amazonInvoice.addEntry(new TransactionEntry(f.checking, "", date, 200, EntryType.DEBIT));
//
//		Transaction lawyerInvoice = new Transaction(seq);
//		lawyerInvoice.addEntry(new TransactionEntry(f.lawyer, "", date, 500, EntryType.CREDIT));
//		lawyerInvoice.addEntry(new TransactionEntry(f.checking, "", date, 500, EntryType.DEBIT));
//
//		Transaction accountantInvoice = new Transaction(seq);
//		accountantInvoice.addEntry(new TransactionEntry(f.accountant, "", date, 300, EntryType.CREDIT));
//		accountantInvoice.addEntry(new TransactionEntry(f.checking, "", date, 300, EntryType.DEBIT));
//
//		Transaction governmentTaxDue = new Transaction(seq);
//		String vendor_client = "";
//		governmentTaxDue.addEntry(new TransactionEntry(f.government, vendor_client, date, 1500, EntryType.CREDIT));
//		governmentTaxDue.addEntry(new TransactionEntry(f.checking, vendor_client, date, 1500, EntryType.DEBIT));
//
//		Transaction payAmazonInvoice = new Transaction(seq);
//		payAmazonInvoice.addEntry(new TransactionEntry(f.amazon, vendor_client, date, 200, EntryType.DEBIT)); // Invoice
//																												// is
//																												// settled.
//		payAmazonInvoice.addEntry(new TransactionEntry(f.checking, vendor_client, date, 200, EntryType.CREDIT));
//
//		f.ledger.postTransaction(amazonInvoice);
//		f.ledger.postTransaction(lawyerInvoice);
//		f.ledger.postTransaction(accountantInvoice);
//		f.ledger.postTransaction(governmentTaxDue);
//		f.ledger.postTransaction(payAmazonInvoice);
//
//		f.ledger.postTransaction(depositToChecking);
//		// ledger.postTransaction(transferToSavings);
//		f.ledger.postTransaction(payCreditCard);
//		f.ledger.postTransaction(earnInvestment);
//
//		// assertEquals(300, checking.getBalance());
//		// assertEquals(500, savings.getBalance());
//		assertEquals(-200, f.creditCard.getBalance());
//		assertEquals(100, f.investment.getBalance());
//
//		assertEquals(-2500, f.checking.getBalance()); // Assuming we're starting from zero.
//		assertEquals(0, f.amazon.getBalance());
//		assertEquals(500, f.lawyer.getBalance());
//		assertEquals(300, f.accountant.getBalance());
//		assertEquals(1500, f.government.getBalance());

	}

	@Test
	public void liability() {
		TestFixture f = new TestFixture();
		
		// Initial balances
		assertEquals(0, f.getCheckingBalance());
		assertEquals(0, f.accountManager.getBalance("Bank Fees"));
//		+------------+-----------+--------------------------+-----+------------------+----------+----------+
//		| Date       | Type      | Name                     | No  | Vendor/Client    |  Debit   | Credit   |
//		+------------+-----------+--------------------------+-----+------------------+----------+----------+
//		| 15/06/2023 | LIABILITY | Accounts Payable         | 070 | Cloutier Longtin | $2,000.00|   -      |
//		| 15/06/2023 | ASSET     | Loan to Owner            | 100 | Your Name        |   -      | $2,000.00|
//		+------------+-----------+--------------------------+-----+------------------+----------+----------+
		TransactionDTO dto = new TransactionDTO();
		
		dto.setAccountName(f.getCheckingAccountName());
		dto.setDate("5/31/2018");
		dto.setDescription("Cloutier Longtin");
		dto.setOriginalDescription("CHQ#00042-4144595130");
		dto.setTransactionType("Credit");
		dto.setAmount(2000);
		dto.setCategory("OperatingExpenses");
	
		f.ledger.addTransaction(dto);
		// The amount that you owe to the company also reduces, so the asset account
		// "Loan to Owner" (the loan from the company to you) is credited.
		assertEquals(2000.00, f.accountManager.getBalance("Loan to Owner"));
		// The company's liability (what it owes to Cloutier Longtin) reduces, so we
		// debit "Accounts Payable".
		assertEquals(2000.00, f.accountManager.getBalance("Accounts Payable"));
		f.ledger.printLedger();
	}

	@Test
	public void ok() {
		TestFixture f = new TestFixture();

		// Initial balances
		assertEquals(0, f.getCheckingBalance());
		assertEquals(0, f.accountManager.getBalance("Bank Fees"));

		f.ledger.addTransaction("5/31/2018", "Frais Mens Plan", "FRAIS MENS PLAN SERV", "19.00", "debit", "Bank Fee",
				f.getCheckingAccountName(), 0.0);

		// After bank fee is charged
		assertEquals(-19, f.getCheckingBalance());
		assertEquals(19, f.accountManager.getBalance("Bank Fees"));

		// After bank fee is refunded
		f.ledger.addTransaction("5/31/2018", "Red Solde", "Cpte	RED SOLDE CPTE", "19.00", "credit", "Income",
				f.getCheckingAccountName(), 0.0);

		assertEquals(0,f.getCheckingBalance());
		assertEquals(0,f. accountManager.getBalance("Bank Fees"));

		f.ledger.printAccounts();

		assertEquals(0, f.getCreditBalance());

		f.ledger.addTransaction("5/28/2018", "Transfer to TD BUSINESS VISA", "PMT PREAUTOR VISA TD", "276.72", "debit",
				"Credit Card Payment", f.getCheckingAccountName(), 0.0);

		assertEquals(-276.72, f.getCheckingBalance());
		assertEquals(-276.72, f.getCreditBalance());

		assertEquals(0, f.accountManager.getBalance("Owner's Draw"));
		f.ledger.addTransaction("5/22/2018", "View Cheque Chq", "View Cheque CHQ#00037-3000249272", "4000.00	", "debit",
				"Dividend & Cap Gains", f.getCheckingAccountName(), 0.0);
		assertEquals(-4000.00, f.accountManager.getBalance("Owner's Draw"));
		assertEquals(-4276.72, f.getCheckingBalance());

		/// Ok so there we need to divide this is a total of 8 entry.
		// Accounts Receivable
		//
		// Type , Name ,Client , Debit , Credit.
		// Asset , Accounts Receivable,Incloud Solutio Fac, 5472.81, -
		// Revenue , Sales ,Incloud Solutio Fac, - , 5472.81
		f.ledger.addTransaction("5/14/2018", "Incloud Solutio Fac", "INCLOUD SOLUTIO FAC", "5472.81	", "debit",
				"Invoice", f.getCheckingAccountName(), 0.0);
		f.ledger.printLedger();

		assertEquals(5472.81, f.accountManager.getBalance("Accounts Receivable"), 0.001);
		assertEquals(5472.81, f.accountManager.getBalance("Consulting Revenue"), 0.001);

		// (Payment Received from Client A)
		// Type , Name ,Client , Debit , Credit.
		// Asset , Check ,Incloud Solutio Fac, 5472.81, -
		// Revenue , Accounts Receivable,Incloud Solutio Fac, - , 5472.81
		f.ledger.addTransaction("5/14/2018", "Incloud Solutio Fac", "INCLOUD SOLUTIO FAC", "5472.81	", "debit",
				"Income", f.getCheckingAccountName(), 0.0);

		f.ledger.printLedger();

		assertEquals(1196.09, f.getCheckingBalance(), 0.001);
		assertEquals(5472.81, f.accountManager.getBalance("Consulting Revenue"), 0.001);

//		addTransaction(ledger,accountManager, "4/30/2018", "Frais Mens Plan", "FRAIS MENS PLAN SERV", "19.00", "debit",
//				"Bank Fee", "TD EVERY DAY A BUSINESS PLAN");

		// Post transactions
//		postTransaction(ledger, checking, bankFeeExpense, -19.00);
//		postTransaction(ledger, checking, income, 19.00);
//		postTransaction(ledger, checking, creditCardPayable, -276.72);
//		postTransaction(ledger, checking, dividendCapGains, -4000.00);
//		postTransaction(ledger, checking, income, 5472.81);

	}

	@Test
	public void testBankFees() {
		TestFixture f = new TestFixture();
		AccountManager accountManager = f.accountManager;
		Ledger ledger = f.ledger;
		// Initial balances
		assertEquals(0, f.getCheckingBalance());
		assertEquals(0, accountManager.getBalance("Bank Fees"));

		// Entry from TD bank Account
		ledger.addTransaction("5/31/2018", "Frais Mens Plan", "FRAIS MENS PLAN SERV", "19.00", "debit", "Bank Fee",
				f.getCheckingAccountName(), 0.0);

		// After bank fee is charged
		assertEquals(-19, f.getCheckingBalance());
		assertEquals(19, accountManager.getBalance("Bank Fees"));

		// After bank fee is refunded
		ledger.addTransaction("5/31/2018", "Red Solde", "Cpte	RED SOLDE CPTE", "19.00", "credit", "Income",
				f.getCheckingAccountName(), 0.0);

		assertEquals(0, f.getCheckingBalance());
		assertEquals(0, accountManager.getBalance("Bank Fees"));

		ledger.printAccounts();
		ledger.printLedger();

	}

	@Test
	public void testDividend() {
		TestFixture f = new TestFixture();
		AccountManager accountManager = f.accountManager;
		Ledger ledger = f.ledger;
		assertEquals(0, f.getCheckingBalance());
		assertEquals(0, accountManager.getBalance("Owner's Draw"));

		ledger.addTransaction("5/22/2018", "View Cheque Chq", "View Cheque CHQ#00037-3000249272", "4000.00", "debit",
				"Dividend & Cap Gains", f.getCheckingAccountName(), 0.0);
		double amount = 4000.00;
		/**
		 * Debit (Dr) to Owner's Draw or Dividends (an Equity account): This represents
		 * the amount of money or profit being distributed to the owner. Debiting this
		 * account decreases the equity of the business since the owner is taking money
		 * out
		 */
		assertEquals(-amount, accountManager.getBalance("Owner's Draw"));
		assertEquals(-amount, f.getCheckingBalance());
		ledger.printAccounts();
		ledger.printLedger();
	}

	

	@Test
	public void testVisaPayment() {
		TestFixture f = new TestFixture();
		AccountManager accountManager = f.accountManager;
		Ledger ledger = f.ledger;

		assertEquals(0,f.getCreditBalance());
		assertEquals(0, f.getCheckingBalance());

		ledger.addTransaction("5/28/2018", "AMZNMktpCA*2L6OB8HT1", "amazon", "276.72", "credit", "OperatingExpenses",
				f.getCreditAccountName(), 0.0);

		ledger.printAccounts();
		ledger.printLedger();
		/**
		 * 
		 * Credit Card Payable (a liability account) increases by $276 because you owe
		 * this amount to the credit card company.
		 */
		assertEquals(276.72,f.getCreditBalance());
		assertEquals(0, f.getCheckingBalance());
		/**
		 * Office Supplies Expense (an expense account) increases by $276 because you've
		 * incurred an expense.
		 */
		assertEquals(276.72, accountManager.getBalance("Office Supplies"));

		ledger.addTransaction("5/28/2018", "Transfer to TD BUSINESS VISA", "PMT PREAUTOR VISA TD", "276.72", "debit",
				"Credit Card Payment",  f.getCheckingAccountName(), 0.0);

		assertEquals(-276.72,f.getCheckingBalance(), 0.001);
		assertEquals(0.0, f.getCreditBalance(), 0.001);

		ledger.printAccounts();
		ledger.printLedger();
	}

	

	// +------------+-----------+---------------------+-----+--------------+----------+----------+
	// | Date       |  Type     | Name                | No  | Vendor/Client| Debit    |   Credit |
	// +------------+-----------+---------------------+-----+--------------+----------+----------+
	// | 01/06/2023 | ASSET     | Accounts Receivable | 060 | Client XYZ   | $5,472.81|     -    |
	// | 01/06/2023 | REVENUE   | Consulting Revenue  | 061 | Client XYZ   |    -     | $4739.44 |
	// | 01/06/2023 | LIABILITY | Sales Tax Collected | 061 | Client XYZ   |    -     | $1000.44 |
	// | 01/06/2023 | ASSET     | Checking Acc        | 062 | Client XYZ   |    -     | $5,472.81|
	// | 01/06/2023 | REVENUE   | Accounts Receivable | 063 | Client XYZ   |    -     | $5,472.81|
	// +------------+-----------+---------------------+-----+--------------+----------+----------+
	@Test
	void testTdPlacementSellingRevenue() {
		TestFixture f = new TestFixture();

		Ledger ledger = f.ledger;

		TransactionDTO dto = new TransactionDTO();

		assertEquals(0, f.getCheckingBalance());
		assertEquals(0, f.getBalance(f.getInvestmentAccountName()));

		dto.setAccountName(f.getCheckingAccountName());
		dto.setDate("5/18/2017");
		dto.setDescription("FONDS MUTUELS TD");
		dto.setOriginalDescription("FONDS MUTUELS TD");
		dto.setTransactionType("Credit");
		double original_investment = 60000;
		dto.setAmount(original_investment);
		dto.setCategory("Investment");

		ledger.addTransaction(dto);

		assertEquals(original_investment, f.getBalance(f.getInvestmentAccountName()));
		assertEquals(-original_investment, f.getCheckingBalance());
		
		double original_gain = 15225;
		
		dto.setAccountName(f.getInvestmentAccountName());
		dto.setDate("5/18/2019");
		dto.setDescription("Capital Gain");
		dto.setOriginalDescription("FONDS MUTUELS TD");
		dto.setTransactionType("Credit");
		dto.setAmount(original_gain);
		dto.setCategory("Income");


		
		ledger.addTransaction(dto);
		assertEquals(original_gain, f.getBalance("Unearned Revenue"));
		
		double total_value  = original_investment+original_gain;
		double gain_amount=15225;
		double amount = 12000;
		
		double distributed_gain = amount/total_value * gain_amount;
		
		dto.setAccountName(f.getCheckingAccountName());
		dto.setDate("2/05/2020");
		dto.setDescription("FONDS MUTUELS TD");
		dto.setOriginalDescription("FONDS MUTUELS TD");
		dto.setTransactionType("Debit");
		dto.setAmount(amount);
		dto.setCategory("Investment");
		ledger.addTransaction(dto);
		assertEquals(total_value - amount, f.getBalance(f.getInvestmentAccountName()), 0.001);
		assertEquals(-48000, f.getCheckingBalance());
		assertEquals(original_gain-distributed_gain, f.getBalance("Unearned Revenue"), 0.001);
		
		assertEquals(distributed_gain, f.getBalance("Realized Gain on Sale of Investments"), 0.001);
		
	}
	
	
	// +------------+-----------+---------------------+-----+--------------+----------+----------+
	// | Date       |  Type     | Name                | No  | Vendor/Client| Debit    |   Credit |
	// +------------+-----------+---------------------+-----+--------------+----------+----------+
	// | 01/06/2023 | ASSET     | Accounts Receivable | 060 | Client XYZ   | $5,472.81|     -    |
	// | 01/06/2023 | REVENUE   | Consulting Revenue  | 061 | Client XYZ   |    -     | $4739.44 |
	// | 01/06/2023 | LIABILITY | Sales Tax Collected | 061 | Client XYZ   |    -     | $1000.44 |
	// | 01/06/2023 | ASSET     | Checking Acc        | 062 | Client XYZ   |    -     | $5,472.81|
	// | 01/06/2023 | REVENUE   | Accounts Receivable | 063 | Client XYZ   |    -     | $5,472.81|
	// +------------+-----------+---------------------+-----+--------------+----------+----------+
	@Test
	void testConsultingRevenue() {
		TestFixture f = new TestFixture();
		AccountManager accountManager = f.accountManager;
		Ledger ledger = f.ledger;
		
		                                           //Accounts Receivable
		assertEquals(0, accountManager.getBalance("Accounts Receivable"));
		assertEquals(0,f.getCheckingBalance());

		Double invoiceAmount = 5472.81;
		ledger.addTransaction("5/14/2018", 
				"Incloud Solutio Fac", 
				"INCLOUD SOLUTIO FAC", 
				invoiceAmount.toString(), 
				"debit", 
				"Income",
				f.getCheckingAccountName(), 0.0);

		assertEquals(4739.44, accountManager.getBalance("Consulting Revenue"), 0.001);
		assertEquals(invoiceAmount-4739.44, accountManager.getBalance("Sales Tax Payable"), 0.001);
		assertEquals(invoiceAmount, f.getCheckingBalance(), 0.001);
		assertEquals(0, accountManager.getBalance("Accounts Receivable"));

	}
	
	
	
	// +------------+-----------+---------------------+-----+--------------+----------+----------+
	// | Date       |  Type     | Name                | No  | Vendor/Client| Debit    |   Credit |
	// +------------+-----------+---------------------+-----+--------------+----------+----------+
	// | 01/06/2023 | ASSET     | Accounts Receivable | 060 | Client XYZ   | $5,472.81|     -    |
	// | 01/06/2023 | REVENUE   | Consulting Revenue  | 061 | Client XYZ   |    -     | $4739.44 |
	// | 01/06/2023 | LIABILITY | Sales Tax Collected | 061 | Client XYZ   |    -     | $1000.44 |
	// | 01/06/2023 | ASSET     | Checking Acc        | 062 | Client XYZ   |    -     | $5,472.81|
	// | 01/06/2023 | REVENUE   | Accounts Receivable | 063 | Client XYZ   |    -     | $5,472.81|
	// +------------+-----------+---------------------+-----+--------------+----------+----------+
	@Test
	public void testRevenueSales() {
		TestFixture f = new TestFixture();
		AccountManager accountManager = f.accountManager;
		Ledger ledger = f.ledger;

		assertEquals(0, f.getCheckingBalance());
		assertEquals(0, accountManager.getBalance("Accounts Receivable"));
		assertEquals(0, accountManager.getBalance("Consulting Revenue"));
		assertEquals(0, f.getCheckingBalance());
		assertEquals(0, accountManager.getBalance("Sales Tax Payable"));
	
		// Type , Name ,Client , Debit , Credit.
		// Asset , Accounts Receivable,Incloud Solutio Fac, 5472.81, -
		// Revenue , Sales ,Incloud Solutio Fac, - , 5472.81
		ledger.addTransaction("5/14/2018", "Incloud Solutio Fac", "INCLOUD SOLUTIO FAC", "5472.81	", "debit",
				"Invoice",f.getCheckingAccountName(), 0.0);
		ledger.printLedger();

		double invoiceAmount = 5472.81;
		assertEquals(0, f.getCheckingBalance());
		/**
		 * Your Accounts Receivable (an asset account) increases by $5472 because
		 * someone owes you this amount.
		 */
		assertEquals(invoiceAmount, accountManager.getBalance("Accounts Receivable"), 0.001);
		/**
		 * Your Sales/Revenue (a revenue account) also increases by $5472, recognizing
		 * that you've made a sale/earned this revenue.
		 */
		assertEquals(4739.44, accountManager.getBalance("Consulting Revenue"), 0.001);
		assertEquals(invoiceAmount-4739.44, accountManager.getBalance("Sales Tax Payable"), 0.001);

//		assertEquals(0, accountManager.getBalance(checking));
//		assertEquals(-invoiceAmount, accountManager.getBalance("Accounts Receivable"),0.001);
//		assertEquals(invoiceAmount, accountManager.getBalance("Consulting Revenue"),0.001);

		// Your Accounts Receivable decreases by $5472.81 because the client no longer
		// owes you this amount.
		ledger.addTransaction("5/15/2018", "Incloud Solutio Fac", "INCLOUD SOLUTIO FAC", "5472.81	", "debit",
				"Income", f.getCheckingAccountName(), 0.0);
		assertEquals(invoiceAmount, accountManager.getBalance("Accounts Receivable"), 0.001);
		assertEquals(invoiceAmount, f.getCheckingBalance(), 0.001);
		//assertEquals(invoiceAmount, accountManager.getBalance("Consulting Revenue"), 0.001);
		ledger.printAccounts();
		ledger.printLedger();
	}

	
}
