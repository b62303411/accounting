package com.sam.accounting.model.entities.qb;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sam.accounting.model.Sequence;
import com.sam.accounting.model.TransactionNature;
import com.sam.accounting.model.TransactionType;
import com.sam.accounting.model.dto.TransactionDTO;
import com.sam.accounting.model.entities.FixAccountInfo;
import com.sam.accounting.repository.RuleRepository;
import com.sam.accounting.service.TaxService;
import com.sam.accounting.service.ledger.MutualFundStrategy;
import com.sam.accounting.service.ledger.TransactionStrategy;

public class Ledger {
	private RuleRepository ruleRepo;
	private static final String Owner = "Samuel Audet-Arsenault";
	private AccountManager accountManager;
	private Set<String> postedTransactionIds;
	private Set<Transaction> transactions;
	private ClassificationRule revenueTaxrule;
	private ClassificationRule amazon_rule;
	private ClassificationRule cashDepositRule;
	private ClassificationRule creditCardPaymentRule;
	private ClassificationRule tpsTvqPaymentRule;
	private List<String> vendors;
	private TaxService taxService;

	private HashMap<AccountType, TransactionStrategy> strategies;

	PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	FixAccountInfo fixAccountInfo = new FixAccountInfo();

	private Sequence seq;
	private MutualFundStrategy mutual;

	public Sequence getSeq() {
		return seq;
	}

	public void addObserver(PropertyChangeListener l) {
		pcs.addPropertyChangeListener("theProperty", l);
	}

	protected void setSeq(Sequence seq) {
		this.seq = seq;
	}

	public Ledger(MutualFundStrategy mutual, RuleRepository ruleRepo, AccountManager manager, LedgerRuleFactory factory,
			TaxService taxService) {
		seq = new Sequence();
		this.mutual = mutual;
		this.accountManager = manager;
		this.postedTransactionIds = new HashSet<>();
		this.transactions = new HashSet<Transaction>();
		this.vendors = new ArrayList<String>();
		this.taxService = taxService;
		this.ruleRepo = ruleRepo;
		vendors.add("Cloutier & Longtin");
		vendors.add("MTA");

		revenueTaxrule = new ClassificationRule();
		revenueTaxrule.addKeyWord("QUEBEC GOV'T");
		revenueTaxrule.addKeyWord("ARC");
		revenueTaxrule.addKeyWord("Ministere du revenue");
		revenueTaxrule.addKeyWord("Revenue QC");
		revenueTaxrule.addKeyWord("MRQ");
		revenueTaxrule.addKeyWord("Revenue Quebec");
		revenueTaxrule.addKeyWord("CRA");
		revenueTaxrule.addAccountNumber(fixAccountInfo.checkingAccount.accountNo);
		revenueTaxrule.addAccountNumber("1");

		amazon_rule = new ClassificationRule();

		amazon_rule.addAccountNumber(fixAccountInfo.visaAccount.accountNo);
		amazon_rule.addKeyWord("AMZNMktpCA");
		amazon_rule.addKeyWord("amazon");
		amazon_rule.addKeyWord("Amazon.ca");
		amazon_rule.addKeyWord("Amazon*");
		amazon_rule.addKeyWord("Amazon");

		cashDepositRule = new ClassificationRule();
		cashDepositRule.addKeyWord("Owner Initial deposit");
		cashDepositRule.addAccountNumber(fixAccountInfo.checkingAccount.accountNo);
		
		creditCardPaymentRule = new ClassificationRule();
		creditCardPaymentRule.addAccountNumber(fixAccountInfo.checkingAccount.accountNo);
		creditCardPaymentRule.addKeyWord("PMT PREAUTOR VISA TD");
		creditCardPaymentRule.addKeyWord("PAIEMENTPRÉAUTORISÉ");
		
		tpsTvqPaymentRule = new ClassificationRule();
		tpsTvqPaymentRule.addAccountNumber(fixAccountInfo.checkingAccount.accountNo);
		tpsTvqPaymentRule.addKeyWord("TPS&TVQ");
		
	}

	public AccountManager getAccountManager() {
		return accountManager;
	}

	public void setAccountManager(AccountManager accountManager) {
		this.accountManager = accountManager;
	}

	public void postTransaction(Transaction transaction) {
		if (transaction.getStatus() == TransactionStatus.POSTED || postedTransactionIds.contains(transaction.getId())) {
			throw new IllegalStateException("Transaction already posted.");
		}

		// ... process the transaction ...
		transaction.setStatus(TransactionStatus.POSTED);
		postedTransactionIds.add(transaction.getId());
		transactions.add(transaction);
		transaction.post();
		pcs.firePropertyChange("theProperty", transaction, null);
	}

	public void rePostTransaction(Transaction transaction) {
		// ... process the transaction ...
		transaction.setStatus(TransactionStatus.POSTED);
		postedTransactionIds.add(transaction.getId());
		transaction.post();
	}

	private void postTransaction(TransactionAccount c, Date date, String message) {
		Transaction transaction = new Transaction(seq);

		transaction.setMessage(message);
		if (c.vendor_client_from == null) {
			c.vendor_client_from = c.vendor_client;
			c.vendor_client_to = c.vendor_client;
		}
		// "September 12, 2019"
//		if(date.contains("30-nov.-21")) 
//		{
//			System.err.println();
//		}
		// System.out.println(c.credited.getName() + " is CREDITED of " + c.amount);

		TransactionEntry debited = new TransactionEntry(c.debited, c.vendor_client_from, date, c.amount,
				EntryType.DEBIT);
		// System.out.println("Debited before:"+debited.getAccount().getBalance());
		transaction.addEntry(debited);

		// System.out.println(c.debited.getName() + " is DEBITED of " + c.amount);
		for (Account creditedAccount : c.creditedAccounts) {
			TransactionEntry credited = new TransactionEntry(creditedAccount, c.vendor_client_to, date, c.amount,
					EntryType.CREDIT);
			credited.setActualBalence(c.credited_balence);
			// credited.setBalance(c.credited.getBalance());
			transaction.setDate(date);
			transaction.addEntry(credited);

		}

		postTransaction(transaction);
		System.out.println(debited.getDate() + " " + debited.getAccount().getAccountNumber() + " Debited :" + c.amount
				+ "	Balence:" + debited.getBalance());
		// System.out.println(debited.getDate()+"
		// "+credited.getAccount().getAccountNumber()+" Credited :"+c.amount+"
		// Balence:"+credited.getBalance());
	}

	/**
	 * 
	 * @param date
	 * @param message
	 * @param message_o
	 * @param amountStr
	 * @param type
	 * @param category
	 * @param account
	 */
	public void addTransaction(Date date, String message, String message_o, String amountStr, TransactionNature type,
			TransactionType category, String account, Double balence) {
		TransactionAccount cardinality = new TransactionAccount();
		double amount = Double.parseDouble(amountStr);

		// handleRule(message, message_o, type, category,account,amount, cardinality);

		Account receivable = accountManager.getAccount("Accounts Receivable");

		Account checkingAccount = getCheckingAccount();

		Account transactionAccount = accountManager.getAccountByAccountNo(account);

		String vendor_client = "";
		List<String> words = new ArrayList<String>();
		words.add(message.trim());
		if (message_o != null)
			words.add(message_o);

		switch (category) {
		case Transfer:
			handleTransfer(message, type, account, balence, amount, checkingAccount, cardinality);
			break;
		case Income:
			handleIncome(message, message_o, account, cardinality, amount, receivable, checkingAccount);
			break;
		case OperatingExpenses:
			handleOperatingExpense(message, message_o, words, account, amount, checkingAccount, cardinality,
					vendor_client, balence);
			break;
		case Dividend:
			handleDividend(amount, checkingAccount, cardinality, balence);
			break;
		case Liability:
			handleLiability(message, account, balence, amount, checkingAccount, cardinality, words);
			break;
		case Credit:
			if (message.contains("FONDS MUTUELS TD")) {
				populateFontMutuel(amount, checkingAccount, cardinality, type, balence);
			} else if (message.contains("DEPOT")) {
				populateDepot(amount, checkingAccount, cardinality);
			} else {
				populateToClassify(amount, checkingAccount, cardinality, balence);
			}
			break;
		case Debit:
			handleDebit(message, account, balence, amount, checkingAccount, cardinality, words);
			break;
		case BankFees:
			handleBankFee(amount, checkingAccount, cardinality, balence);
			break;
		case Cash:
			handleCash(words, account, cardinality, amount, type, balence);
			break;
		case Depreciation:
//			+------------+----------+--------------------------+-----+----------------+----------+----------+
//			| Date       | Type     | Name                     | No  | Vendor/Client  |  Debit   | Credit   |
//			+------------+----------+--------------------------+-----+----------------+----------+----------+
//			| 31/12/2023 | EXPENSE  | Depreciation Expense     | 031 | -              | $300.00  |   -      |
//			| 31/12/2023 | ASSET    | Equipment                | 032 | -              |   -      | $300.00  |
//			+------------+----------+--------------------------+-----+----------------+----------+----------+	
			cardinality.setCredited(getAccountByName("Office Equipment"));
			cardinality.debited = getAccountByName("Depreciation Expense");
			cardinality.amount = Math.abs(amount);
			cardinality.vendor_client = "Self";
			break;
		case Invoice:
			handleInvoice(message, message_o, type, receivable, amount, cardinality, balence);
			break;
		case Unknown:
			handleUnknown(message, message_o, type, account, balence, amount, checkingAccount, cardinality,
					vendor_client, words);
			break;
		case SalesRevenue:
			handleSalesRevenue(message, message_o, receivable, amount, checkingAccount, cardinality);
			break;
		case DeptRepayment:
			handleDebtRepayment(message, message_o, account, balence, amount, checkingAccount, cardinality, words);
			break;
		case AssetPurchased:
			handleAssetPurchaced(message, words, account, amount, cardinality, balence);
			break;
		case LostOfAssetWriteOff:
			handleLostOfAssetWriteOff(cardinality, amount);
			break;
		default:
			System.err.println(message);
			break;

		}
		if (cardinality.debited == null || cardinality.creditedAccounts.isEmpty()) {

			if (cardinality.split != null) {
				for (TransactionAccount ta : cardinality.split) {
					postTransaction(ta, date, message);
				}
			} else {
				System.err.println("not splitted yet null " + message + " " + account);
				if (isFromCheq(account)) {
					populateToClassify(amount, checkingAccount, cardinality, balence);
					postTransaction(cardinality, date, message);
				}
				else 
				{
					System.err.println(message);
				}
			}

		} else {
			postTransaction(cardinality, date, message);
		}

	}

	private void handleIncome(String message, String message_o, String account, TransactionAccount cardinality,
			double amount, Account receivable, Account checkingAccount) {
		if (account.contains(fixAccountInfo.investmentAccount.accountNo)) {
			cardinality.vendor_client = "TD";
			// This seems to be your refund based on the provided transactions
			cardinality.setCredited(getAccountByName("Unearned Revenue"));
			cardinality.debited = getAccountByNo(fixAccountInfo.investmentAccount.accountNo);
			cardinality.amount = amount;

		} else if (isFromCheq(account)) {
			if (null != message_o && message_o.contains("RED SOLDE CPTE")
					|| null != message_o && message_o.contains("Red Solde")) {

				handleBankFeeRefund(amount, checkingAccount, cardinality);
			} else {
				handleSalesRevenue(message, message_o, receivable, amount, checkingAccount, cardinality);
			}
		} else if (account.contains(fixAccountInfo.visaAccount.accountNo)) {
			if (message.contains("REMISEENARGENTTD") || message.contains("ANNUAL")) {
				handleCreditCardCashback(amount, cardinality);
			}
			else 
			{
				System.err.println(message);
			}
		}
	}

	private Account getAccountByNo(String no) {
		return accountManager.getAccountByAccountNo(no);
	}

	/**
	 * 
	 * @param message
	 * @param message_o
	 * @param type
	 * @param category
	 * @param account
	 * @param amount
	 * @param cardinality
	 */
	private void handleRule(String message, String message_o, String type, String category, String account,
			double amount, TransactionAccount cardinality) {
		boolean found = false;
		List<String> words = new ArrayList<String>();
		words.add(message.trim());
		if (message_o != null)
			words.add(message_o);

//		for (ClassificationRule r : rules) {
//			
//			if(r.keyWordFount(account,words)) 
//			{
////				AccountAction credit = r.getClassification().getCredit();
////				AccountAction debit = r.getClassification().getDebit();
////				cardinality.setCredited(getAccountByName(credit.getAccountName()));
////				cardinality.debited = getAccountByName(debit.getAccountName());
////				cardinality.amount = Math.abs(amount);
////				cardinality.vendor_client = r.getVendor();
////				System.out.println("found:"+r.getRuleName());
////				System.out.println("Credited:"+credit.getAccountName());
////				System.out.println("Debited:"+debit.getAccountName());
//				found = true;
//				break;
//			}
//		}
		if (!found) {
			// prompt.submituery("from account:"+account+" memo:"+message, account);
			System.out.println(message);
		}

	}

	private void handleLiability(String message, String account, Double balence, double amount, Account checkingAccount,
			TransactionAccount cardinality, List<String> words) {
		if (this.revenueTaxrule.keyWordFount(account, words)) {
			switch (account) {
			case "1":
//					+----------+------------------+---------------------+----------------+---------+---------+
//					|   Date   | Account Type     | Account Name        | Vendor/Client  |  Debit  | Credit  |
//					+----------+------------------+---------------------+----------------+---------+---------+
//					| 1-May-23 | ASSET            | Loan to Owner       | Owner's Name   | 5,000   |   -     |
//					| 1-May-23 | LIABILITY        | Income Tax Payable  | ARC            |   -     | 5,000   |
//					+----------+------------------+---------------------+----------------+---------+---------+

				cardinality.vendor_client_from = Owner;
				cardinality.vendor_client_to = "ARC";
				populateLoanPaymentViaTaxRefund(amount, cardinality);

				break;
			default:
				populateTax(amount, checkingAccount, cardinality, balence);
				break;
			}

		} else {
//				+----------+------------------+---------------------+----------------+---------+---------+
//				|   Date   | Account Type     | Account Name        | Vendor/Client  |  Debit  | Credit |
//				+----------+------------------+---------------------+----------------+---------+---------+
//				| 1-May-23 | ASSET            | Loan to Owner       | Owner's Name   | 5,000   |   -    |
//				| 1-May-23 | LIABILITY        | Income Tax Payable  | ARC            |   -     | 5,000  |
//				+----------+------------------+---------------------+----------------+---------+---------+
			if (message.contains("Cloutier & Longtin")) {
				cardinality.vendor_client_from = Owner;
				cardinality.vendor_client_to = "Cloutier & Longtin";
				populateLoanRefundViaPayable(amount, cardinality);
			} else if (message.contains("CPA")) {
				cardinality.vendor_client_from = Owner;
				cardinality.vendor_client_to = "MTA";
				populateLoanRefundViaPayable(amount, cardinality);
			} else if (message.contains("VISA")) {
				cardinality.vendor_client_from = Owner;
				cardinality.vendor_client_to = "VISA";
				populateLoanRefundViaCredit(amount, cardinality);
			} else {
				populateToClassify(amount, checkingAccount, cardinality, balence);
			}

		}
	}

	/**
	 * 
	 * @param message
	 * @param cardinality
	 * @param amount
	 * @param type
	 * @param balence
	 */
	private void handleCash(List<String> message, String account, TransactionAccount cardinality, double amount,
			TransactionNature type, Double balence) {
		String vendor = "Self";
		Account check = getCheckingAccount();
		switch (type) {
		case Credit:
				cardinality.debited = getAccountByName("To Classify");
				cardinality.setCredited(check);
				cardinality.credited_balence = balence;
				cardinality.amount = Math.abs(amount);
				cardinality.vendor_client = vendor;
		
			break;
		case Debit:
//			Date       Account Title         Debit     Credit
//			---------- --------------------  -------   -------
//			XX/XX/XXXX Checking Account      600
//			           Owner's Contributions           600
			if (cashDepositRule.keyWordFount(account, message)) {
			cardinality.debited = check;// To Classify
			cardinality.setCredited(getAccountByName("Owner's Contributions"));
			cardinality.credited_balence = balence;
			cardinality.amount = Math.abs(amount);
			cardinality.vendor_client = vendor;
			}
			break;
			default:
				System.err.println(type);
		}

	}

	private void handleDebit(String message, String account, Double balence, double amount, Account checkingAccount,
			TransactionAccount cardinality, List<String> words) {

		if (message.contains("View Cheque CHQ")) {
			if (amount - Math.floor(amount) > 0) {
				System.out.println("Number has a decimal point.");
				if (amount > 3000) {
					populateTax(amount, checkingAccount, cardinality, balence);
				}
			} else {
				handleDividend(amount, checkingAccount, cardinality, balence);
			}

		} else if (message.contains("TD investment transfer to")) {
			// populateFontMutuel(amount, checkingAccount, cardinality, type);
			System.err.println(message);

		} else {
			populateToClassify(amount, checkingAccount, cardinality, balence);
		}
		if (this.revenueTaxrule.keyWordFount(account, words)) {
			populateTax(amount, checkingAccount, cardinality, balence);

		} else {
			if (isFromCheq(account))
				populateToClassify(amount, checkingAccount, cardinality, balence);
		}
	}

	private boolean isFromCheq(String account) {
		return account.contains(fixAccountInfo.checkingAccount.accountNo);
	}

	private Account getCheckingAccount() {
		return accountManager.getAccountByName(this.fixAccountInfo.checkingAccount.accountName);
	}

	private void handleUnknown(String message, String message_o, TransactionNature type, String account, Double balence,
			double amount, Account checkingAccount, TransactionAccount cardinality, String vendor_client,
			List<String> words) {
		if (this.revenueTaxrule.keyWordFount(account, words)) {
			populateTax(amount, checkingAccount, cardinality, balence);
		} else if (message.contains("View Cheque CHQ")) {
			if (amount - Math.floor(amount) > 0) {
				System.out.println("Number has a decimal point.");
				populateToClassify(amount, checkingAccount, cardinality, balence);
			} else {
				handleDividend(amount, checkingAccount, cardinality, balence);
			}
		} else if (amazon_rule.keyWordFount(account, words)) {
			handleOperatingExpense(message, message_o, words, account, amount, checkingAccount, cardinality,
					vendor_client, balence);
		} else {
			if (type.name().contains("Credit")) {
				if (message.contains("ANNUALCASHBACKCREDIT")) {
					handleBankFeeRefund(amount, checkingAccount, cardinality);
				} else {
					if (isFromCheq(account)) {
						populateToClassify(amount, checkingAccount, cardinality, balence);
						// postTransaction(cardinality, date, message);
					} else
						System.out.println(amount);
				}

			} else {

//					+------------+----------+-------------------------+-----+---------------------+---------+--------+
//					| Date       | Type     | Name                    | No  | Vendor/Client       |  Debit  | Credit |
//					+------------+----------+-------------------------+-----+---------------------+---------+--------+
//					| 28/01/2023 | LIABILITY| Credit Card             | 024 | Credit Card Company | $100.00 |   -    |
//					| 28/01/2023 | REVENUE  | Other Income/Cashback   | 025 | Credit Card Company |   -     | $100.00|
//					+------------+----------+-------------------------+-----+---------------------+---------+--------+
				if (message.contains("ANNUALCASHBACKCREDIT")) {

					handleCreditCardCashback(amount, cardinality);
				} else {
					if (isFromCheq(account)) {
						populateToClassify(amount, checkingAccount, cardinality, balence);

					} else
						System.err.println(message);
				}
			}

		}
	}

	private void handleDebtRepayment(String message, String message_o, String account, Double balence, double amount,
			Account checkingAccount, TransactionAccount cardinality, List<String> words) {
		
		if(creditCardPaymentRule.keyWordFount(account, words)) 
		{
			handleCreditCardPayment(amount, checkingAccount, cardinality, balence);
		}
		else if (tpsTvqPaymentRule.keyWordFount(account, words))
		{
			populateSalesTaxPayment(amount, checkingAccount, cardinality, balence);
		} 
		else if (this.revenueTaxrule.keyWordFount(account, words)) {
			populateTax(amount, checkingAccount, cardinality, balence);
		}else if (message.contains("View Cheque CHQ")) {
			if (amount > 4000)
				populateTax(amount, checkingAccount, cardinality, balence);
			else {
				if (isFromCheq(account))
					populateToClassify(amount, checkingAccount, cardinality, balence);
				else 
				{
					System.err.println(message);
				}
			}
		} else {
			if (isFromCheq(account))
				populateToClassify(amount, checkingAccount, cardinality, balence);
			else 
			{
				System.err.println(message);
			}
		}
	}

	/**
	 * 
	 * @param message
	 * @param type
	 * @param account
	 * @param balence
	 * @param amount
	 * @param checkingAccount
	 * @param cardinality
	 */
	private void handleTransfer(String message, TransactionNature type, String account, Double balence, double amount,
			Account checkingAccount, TransactionAccount cardinality) {
		if (message.equals("TRAITE $CA 01755011")) {
			handleLoanToOwner(amount, checkingAccount, cardinality);
		} else if (message.equals("DEPOT") && isFromCheq(account)) {
			populateDepot(amount, checkingAccount, cardinality);
		} else if (message.contains("CORRECTION")) {
			cardinality.amount = amount;
			cardinality.vendor_client = "self";
			switch (type) {
			case Debit:
				cardinality.debited = checkingAccount;
				cardinality.setCredited(getAccountByName("Unknown"));
				break;
			case Credit:
				cardinality.debited = getAccountByName("Unknown");
				cardinality.setCredited(checkingAccount);
				break;
				default:
					System.err.println(type);
					break;

			}

		} else {
			if (isFromCheq(account))
				populateFontMutuel(amount, checkingAccount, cardinality, type, balence);
			else {
				System.err.println();
			}
		}
	}

	private Account getAccountByName(String name) {
		return accountManager.getAccountByName(name);
	}

	/**
	 * 
	 * @param amount
	 * @param checkingAccount
	 * @param cardinality
	 */
	private void populateDepot(double amount, Account checkingAccount, TransactionAccount cardinality) {
		// +------------+-------------+-----------------------+-----+------------------+----------+----------+
		// | Date | Account Type| Account Name | | Vendor/Client | Debit | Credit |
		// +------------+-------------+-----------------------+-----+------------------+----------+----------+
		// | 30/01/2023 | EXPENSE | To Classify | 027 | Revenue Agency | $5,000.00| - |
		// | 30/01/2023 | ASSET | Checking Account | 028 | Revenue Agency | -
		// |$5,000.00|
		// +------------+-------------+-----------------------+-----+------------------+----------+----------+
		cardinality.debited = checkingAccount;
		cardinality.setCredited(getAccountByName("Income Tax Expense"));
		cardinality.amount = Math.abs(amount);
		cardinality.vendor_client = "ARC-TBD";
	}

//	+------------+---------+---------------------+-----+------------------+---------+--------+
//	| Date       | Type    | Name                | No  | Vendor/Client    |  Debit  | Credit |
//	+------------+---------+---------------------+-----+------------------+---------+--------+
//	| 15/08/2023 | LIABILITY| Credit Card Account | 101 | Cashback Program |   -     | $100.00|
//	| 15/08/2023 | INCOME  | Cashback Income     | 102 | Cashback Program | $100.00 |   -    |
//	+------------+---------+---------------------+-----+------------------+---------+--------+
	private void handleCreditCardCashback(double amount, TransactionAccount cardinality) {
		cardinality.setCredited(getAccountByName("Miscellaneous Revenue"));
		cardinality.debited = getCreditCardAccount();
		cardinality.amount = Math.abs(amount);

		cardinality.vendor_client = "VISA";
	}

	private void populateToClassify(double amount, Account checkingAccount, TransactionAccount cardinality,
			Double balence) {
		// When the tax is actually paid:
		// +------------+-------------+-----------------------+-----+------------------+----------+----------+
		// | Date | Account Type| Account Name | | Vendor/Client | Debit | Credit |
		// +------------+-------------+-----------------------+-----+------------------+----------+----------+
		// | 30/01/2023 | EXPENSE | To Classify | 027 | Revenue Agency | $5,000.00| - |
		// | 30/01/2023 | ASSET | Checking Account | 028 | Revenue Agency | - |
		// $5,000.00|
		// +------------+-------------+-----------------------+-----+------------------+----------+----------+
		cardinality.debited = accountManager.getAccountByName("To Classify");
		cardinality.vendor_client = "self";
		cardinality.setCredited(checkingAccount);
		cardinality.credited_balence = balence;
		cardinality.amount = Math.abs(amount);
	}

	private void handleLoanToOwner(double amount, Account checkingAccount, TransactionAccount cardinality) {
		cardinality.split = new ArrayList<TransactionAccount>();
		TransactionAccount loan = new TransactionAccount();
		loan.vendor_client = Owner;
//		+------------+---------+------------------------+-----+--------------+----------+----------+
//		| Date       | Type    | Name                   | No  | Vendor/Client|  Debit   | Credit   |
//		+------------+---------+------------------------+-----+--------------+----------+----------+
//		| 01/04/2023 | ASSET   | Loan to Owner          | 040 | Owner's Name | $5,000.00|   -      |
//		| 01/04/2023 | ASSET   | Checking Account       | 041 | Owner's Name |   -      | $5,000.00|
//		+------------+---------+------------------------+-----+--------------+----------+----------+

		loan.debited = accountManager.getAccountByName("Loan to Owner");
		double dividend_drawn = 27413;
		loan.amount = Math.abs(amount) - dividend_drawn;
		loan.setCredited(checkingAccount);

		TransactionAccount dividend = new TransactionAccount();
		dividend.vendor_client = Owner;
		populateDividend(checkingAccount, dividend_drawn, dividend);
		cardinality.split.add(dividend);
		cardinality.split.add(loan);
	}

	private void populateLoanPaymentViaTaxRefund(double amount, TransactionAccount cardinality) {
		populateLoanRefundViaLiability(amount, cardinality, "Taxes Payable");
	}

	private void populateLoanRefundViaCredit(double amount, TransactionAccount cardinality) {
		populateLoanRefundViaLiability(amount, cardinality, "VISA_TD_REMISES_AFFAIRES");
	}

	private void populateLoanRefundViaPayable(double amount, TransactionAccount cardinality) {
		populateLoanRefundViaLiability(amount, cardinality, "Accounts Payable");
	}

	private void populateLoanRefundViaLiability(double amount, TransactionAccount cardinality, String liability) {
		cardinality.setCredited(getAccountByName("Loan to Owner"));
		cardinality.debited = accountManager.getAccountByName(liability);
		cardinality.amount = Math.abs(amount);
	}

//	+------------+-----------+--------------------------+-----+--------------+----------+----------+
//	| Date       | Type      | Name                     | No  | Vendor/Client|  Debit   | Credit   |
//	+------------+-----------+--------------------------+-----+--------------+----------+----------+
//	| 01/05/2023 | EQUITY    | Dividends                | 050 | Owner's Name | $2,000.00|   -      |
//	| 01/05/2023 | ASSET     | Checking Account         | 051 | Owner's Name |   -      | $2,000.00|
//	+------------+-----------+--------------------------+-----+--------------+----------+----------+
	private void populateDividend(Account checkingAccount, double dividend_drawn, TransactionAccount dividend) {
		dividend.debited = getAccountByName("Owner's Draw");
		dividend.amount = dividend_drawn;
		dividend.setCredited(checkingAccount);
	}

//	+------------+----------+-------------------------+-----+--------------+---------+--------+
//	| Date       | Type     | Name                    | No  | Vendor/Client|  Debit  | Credit |
//	+------------+----------+-------------------------+-----+--------------+---------+--------+
//	| 28/01/2023 | ASSET    | Checking Account        | 022 | Bank Name    | $50.00  |   -    |
//	| 28/01/2023 | EXPENSE  | Bank Fees               | 023 | Bank Name    |   -     | $50.00 |
//	+------------+----------+-------------------------+-----+--------------+---------+--------+
	private void handleBankFeeRefund(double amount, Account checkingAccount, TransactionAccount cardinality) {
		cardinality.vendor_client = "TD";
		// This seems to be your refund based on the provided transactions
		cardinality.setCredited(getAccountByName("Bank Fees"));
		cardinality.debited = checkingAccount;
		cardinality.amount = amount;
	}

//	+------------+----------+-----------------------+-----+------------------+----------+----------+
//	| Date       | Type     | Name                  | No  | Vendor/Client    |  Debit   | Credit   |
//	+------------+----------+-----------------------+-----+------------------+----------+----------+
//	| 30/12/2022 | EXPENSE  | Income Tax Expense    | 025 | Revenue Agency   | $5,000.00|   -      |
//	| 30/12/2022 | LIABILITY| Taxes Payable         | 026 | Revenue Agency   |   -      | $5,000.00|
//	+------------+----------+-----------------------+-----+------------------+----------+----------+

//	When the tax is actually paid:
//	+------------+----------+-----------------------+-----+------------------+----------+----------+
//	| 30/01/2023 | LIABILITY| Taxes Payable         | 027 | Revenue Agency   | $5,000.00|   -      |
//	| 30/01/2023 | ASSET    | Checking Account      | 028 | Revenue Agency   |   -      | $5,000.00|
//	+------------+----------+-----------------------+-----+------------------+----------+----------+
	private void populateTax(double amount, Account checkingAccount, TransactionAccount cardinality, Double balence) {

		TransactionAccount incomeTaxPayment = new TransactionAccount();

		String vendor = "Revenue Agency";
		Account payable = getAccountByName("Taxes Payable");
		incomeTaxPayment.debited = payable;
		incomeTaxPayment.setCredited(checkingAccount);
		incomeTaxPayment.credited_balence = balence;
		incomeTaxPayment.amount = Math.abs(amount);
		incomeTaxPayment.vendor_client = vendor;

		cardinality.split = new ArrayList<TransactionAccount>();
		// cardinality.split.add(incomeTaxInvoice);
		cardinality.split.add(incomeTaxPayment);
	}

//	When the tax is actually paid:
//	+------------+----------+-----------------------+-----+------------------+----------+----------+
//	| 30/01/2023 | LIABILITY| Taxes Payable         | 027 | TPS&TVQ          | $5,000.00|   -      |
//	| 30/01/2023 | ASSET    | Checking Account      | 028 | TPS&TVQ          |   -      | $5,000.00|
//	+------------+----------+-----------------------+-----+------------------+----------+----------+
	private void populateSalesTaxPayment(double amount, Account checkingAccount, TransactionAccount cardinality,
			Double balence) {
		String vendor = "TPS&TVQ";
		Account payable = getAccountByName("Sales Tax Payable");
		cardinality.debited = payable;
		cardinality.setCredited(checkingAccount);
		cardinality.credited_balence = balence;
		cardinality.amount = Math.abs(amount);
		cardinality.vendor_client = vendor;
	}

//	+------------+---------+--------------------------+-----+-----------------+-----------+-----------+
//	| Date       | Type    | Name                     | No  | Vendor/Client   |  Debit    | Credit    |
//	+------------+---------+--------------------------+-----+-----------------+-----------+-----------+
//	| 01/03/2023 | ASSET   | Investment in Mutual Fund| 032 | Mutual Fund Co. | $10,000.00|   -       |
//	| 01/03/2023 | ASSET   | Checking Account         | 033 | Mutual Fund Co. |   -       | $10,000.00|
//	+------------+---------+--------------------------+-----+-----------------+-----------+-----------+
	private void populateInvestment() {

	}

//	+------------+---------+--------------------------+-----+-----------------+-----------+-----------+
//	| Date       | Type    | Name                     | No  | Vendor/Client   |  Debit    | Credit    |
//	+------------+---------+--------------------------+-----+-----------------+-----------+-----------+
//	| 01/03/2023 | ASSET   | Checking Account         | 032 | Mutual Fund Co. | $10,000.00|   -       |
//	| 01/03/2023 | ASSET   | Investment in Mutual Fund| 033 | Mutual Fund Co. |   -       | $10,000.00|
//	+------------+---------+--------------------------+-----+-----------------+-----------+-----------+
	/**
	 * 
	 * @param amount
	 * @param checkingAccount
	 * @param cardinality
	 * @param type
	 */
	private void populateFontMutuel(double amount, Account transactionAccount, TransactionAccount cardinality,
			TransactionNature type, Double balence) {
		mutual.populate(amount, transactionAccount, cardinality, type, balence);
	}

	private void handleOperatingExpense(String message, String message_o, List<String> words, String account,
			double amount, Account checkingAccount, TransactionAccount cardinality, String vendor_client,
			Double balence) {

		for (ClassificationRule rule : ruleRepo.operatingExpensesRules) {
			if (rule.keyWordFount(account, words)) {
				rule.populate(cardinality, amount, accountManager, balence, account);
				return;
			}
		}
		if (message.contains("RED SOLDE CPTE")) {
			handleBankFeeRefund(amount, checkingAccount, cardinality);
		} else {
			if (isFromCheq(account)) {
				populateToClassify(amount, checkingAccount, cardinality, balence);
				// postTransaction(cardinality, date, message);
			} else
				System.err.println();
		}

	}

	private Account getCreditCardAccount() {
		return accountManager.getAccountByName("VISA_TD_REMISES_AFFAIRES");
	}

	private void handleAssetPurchaced(String message, List<String> words, String account, double amount,
			TransactionAccount cardinality, Double balence) {

		for (ClassificationRule rule : ruleRepo.assetPurchaceRules) {
			if (rule.keyWordFount(account, words)) {
				rule.populate(cardinality, amount, accountManager, balence, account);
				return;
			}
		}

		Account relatedAccount;
		Account oe = getAccountByName("Office Equipment");
		if (this.amazon_rule.keyWordFount(account, words)) {
			cardinality.vendor_client = "Amazon";
			cardinality.amount = amount;
			relatedAccount = accountManager.getAccountByName(account);

			if (relatedAccount != null) {
				cardinality.setCredited(relatedAccount);
				cardinality.debited = oe;
			} else {
				cardinality.setCredited(getCreditCardAccount());
				cardinality.debited = oe;
			}
		} else if (message.contains("BESTBUY")) {

			cardinality.vendor_client = "BESTBUY";
			cardinality.amount = amount;
			cardinality.setCredited(getCreditCardAccount());
			cardinality.debited = oe;

		} else {
			System.err.println();
		}
	}

//	+------------+----------+------------------------+-----+--------------+---------+--------+
//	| Date       | Type     | Name                   | No  | Vendor/Client|  Debit  | Credit |
//	+------------+----------+------------------------+-----+--------------+---------+--------+
//	| 03/01/2023 | EXPENSE  | Bank Service Charges   | 005 | [Bank Name]  | $25.00  |    -   |
//	| 03/01/2023 | ASSET    | Checking Account       | 006 | [Bank Name]  |    -    | $25.00 |
//	+------------+----------+------------------------+-----+--------------+---------+--------+
	private void handleBankFee(double amount, Account checkingAccount, TransactionAccount cardinality, Double balence) {
		cardinality.vendor_client = "TD";
		cardinality.setCredited(checkingAccount);
		cardinality.debited = accountManager.getAccountByName("Bank Fees");
		cardinality.amount = Math.abs(amount);
		cardinality.credited_balence = balence;
	}

	/**
	 * 
	 * @param message
	 * @param message_o
	 * @param type
	 * @param receivable
	 * @param amount
	 * @param cardinality
	 * @param balence
	 */
	private void handleInvoice(String message, String message_o, TransactionNature type, Account receivable,
			double amount, TransactionAccount cardinality, Double balence) {
		boolean vendor = false;
		for (String vendor_str : vendors) {
			if (message.contains(vendor_str)) {
				cardinality.vendor_client = vendor_str;
				populateVendorInvoice(amount, cardinality);
				vendor = true;
				break;
			}
		}
		if (vendor == false)
			populateClientInvoice(receivable, amount, cardinality);
	}

	private TransactionAccount handleSalesRevenue(String message, String message_o, Account receivable, double amount,
			Account checkingAccount, TransactionAccount c) {

		TransactionAccount qc_inc_invoice = new TransactionAccount();
		c.vendor_client = getCorrespondingVendorOrClient(message, message_o);
		qc_inc_invoice.vendor_client = c.vendor_client;

		populateClientInvoice(receivable, amount, qc_inc_invoice);

//		+------------+-----------+-------------------------+-----+--------------+----------+----------+
//		| Date       | Type      | Name                    | No  | Vendor/Client|  Debit   | Credit   |
//		+------------+-----------+-------------------------+-----+--------------+----------+----------+
//		| 10/06/2023 | ASSET     | Checking Account        | 100 | Client XYZ   | $5,000.00|   -      |
//		| 10/06/2023 | ASSET     | Accounts Receivable     | 060 | Client XYZ   |   -      | $5,000.00|
//		+------------+-----------+-------------------------+-----+--------------+----------+----------+
		TransactionAccount invoice_payment = new TransactionAccount();
		invoice_payment.vendor_client = c.vendor_client;
		invoice_payment.amount = amount;
		invoice_payment.debited = checkingAccount;
		invoice_payment.setCredited(receivable);

		// c.from = checkingAccount;
		// c.to = receivable;
		// c.amount = -amount;
		c.split = new ArrayList<TransactionAccount>();
		c.split.addAll(qc_inc_invoice.split);
		c.split.add(invoice_payment);
		return c;
	}

//	+------------+----------+------------------------+-----+--------------+-----------+-----------+
//	| Date       | Type     | Name                   | No  | Vendor/Client|  Debit    | Credit    |
//	+------------+----------+------------------------+-----+--------------+-----------+-----------+
//	| 04/01/2023 | ASSET    | Accounts Receivable    | 007 | Client XYZ   | $1,500.00 |   -       |
//	| 04/01/2023 | REVENUE  | Consulting Services    | 008 | Client XYZ   |    -      | $1,500.00 |
//	+------------+----------+------------------------+-----+--------------+-----------+-----------+
	/**
	 * 
	 * @param receivable
	 * @param amount
	 * @param qc_inc_invoice
	 */
	private void populateClientInvoice(Account receivable, double amount, TransactionAccount qc_inc_invoice) {
		// +------------+-----------+-------------------------+-----+--------------+----------+----------+
		// | Date | Type | Name | No | Vendor/Client| Debit | Credit |
		// +------------+-----------+-------------------------+-----+--------------+----------+----------+
		// | 01/06/2023 | ASSET | Accounts Receivable | 060 | Client XYZ | $5,000.00| -
		// |
		// | 01/06/2023 | REVENUE | Consulting Revenue | 061 | Client XYZ | - |
		// $5,000.00|
		// +------------+-----------+-------------------------+-----+--------------+----------+----------+
		TransactionAccount without_sales_tax_revenue = new TransactionAccount();
		TransactionAccount tax_collected = new TransactionAccount();
		double amount_without_tax = taxService.getBeforeTaxesValue(Math.abs(amount));
		double tax = amount - amount_without_tax;

		without_sales_tax_revenue.amount = amount_without_tax;
		without_sales_tax_revenue.setCredited(getAccountByName("Consulting Revenue"));
		without_sales_tax_revenue.debited = receivable;

		tax_collected.amount = tax;
		tax_collected.setCredited(getAccountByName("Sales Tax Payable"));
		tax_collected.setCredited(getAccountByName("Sales Tax Collected"));
		tax_collected.debited = receivable;

		qc_inc_invoice.split = new ArrayList<TransactionAccount>();
		qc_inc_invoice.split.add(tax_collected);
		qc_inc_invoice.split.add(without_sales_tax_revenue);

	}

	/**
	 * +------------+----------+---------------------+-----+--------------+---------+--------+
	 * | Date | Type | Name | No | Vendor/Client| Debit | Credit |
	 * +------------+----------+---------------------+-----+--------------+---------+--------+
	 * | 02/01/2023 | EXPENSE | Professional Fees | 003 | C&L | $500.00 | - | |
	 * 02/01/2023 | LIABILITY| Accounts Payable | 004 | C&L | - |$500.00 |
	 * +------------+----------+---------------------+-----+--------------+---------+--------+
	 **/
	private void populateVendorInvoice(double amount, TransactionAccount qc_inc_invoice) {
		qc_inc_invoice.amount = Math.abs(amount);
		qc_inc_invoice.debited = getAccountByName("Professional Fees");
		qc_inc_invoice.setCredited(getAccountByName("Accounts Payable"));
	}

	/**
	 * //
	 * +------------+----------+-----------------------+-----+----------------+---------+---------+
	 * // | Date | Type | Name | No | Vendor/Client | Debit | Credit | //
	 * +------------+----------+-----------------------+-----+----------------+---------+---------+
	 * // | 20/01/2023 | LIABILITY| Credit Card Payable | 010 | Credit Card Co.|
	 * $500.00 | - | // | 20/01/2023 | ASSET | Checking Account | 011 | Credit Card
	 * Co.| - | $500.00 | //
	 * +------------+----------+-----------------------+-----+----------------+---------+---------+
	 * 
	 * @param balence
	 */
	private void handleCreditCardPayment(double amount, Account checkingAccount, TransactionAccount cardinality,
			Double balence) {

		cardinality.debited = getCreditCardAccount();
		cardinality.vendor_client = "VISA";
		cardinality.setCredited(checkingAccount);
		cardinality.credited_balence = balence;
		cardinality.amount = Math.abs(amount);

	}

//	+------------+----------+--------------------------+-----+--------------+----------+----------+
//	| Date       | Type     | Name                     | No  | Vendor/Client|  Debit   | Credit   |
//	+------------+----------+--------------------------+-----+--------------+----------+----------+
//	| 25/01/2023 | EQUITY   | Dividends                | 020 | Owner's Name | $1,000.00|   -      |
//	| 25/01/2023 | ASSET    | Checking Account         | 021 | Owner's Name |   -      | $1,000.00|
//	+------------+----------+--------------------------+-----+--------------+----------+----------+
	private void handleDividend(double amount, Account checkingAccount, TransactionAccount cardinality,
			Double balence) {
		cardinality.vendor_client = Owner;
		cardinality.debited = getAccountByName("Owner's Draw");
		cardinality.amount = amount;
		cardinality.setCredited(checkingAccount);
		cardinality.credited_balence = balence;
	}

	/**
	 * Date | Account Name | Debit | Credit
	 * ---------------------------------------------------------- 01/02/2023 | Loss
	 * on Asset Write-off | $23 | 01/02/2023 | Electronic Equipment | | $23
	 * ----------------------------------------------------------
	 */
	private void handleLostOfAssetWriteOff(TransactionAccount acc, double amount) {
		acc.vendor_client = "Self";
		acc.debited = getAccountByName("Loss on Asset Write-off");
		acc.setCredited(getAccountByName("Office Equipment"));
		acc.amount = amount;
	}

	private String getCorrespondingVendorOrClient(String message, String message_o) {

		return message;
	}

	public void printAccounts() {
		for (Account account : accountManager.getAccounts()) {
			System.out.println(account);
		}
	}

	public void printLedger() {
		for (Transaction transaction : this.transactions) {
			System.out.println(transaction.toString());
		}
	}

	public Set<Transaction> getTransactions() {

		return transactions;
	}

	public void addTransaction(TransactionDTO dto) {
		addTransaction(dto.getDate(), dto.getOriginalDescription(), dto.getDescription(), "" + dto.getAmount(),
				dto.getTransactionType(), dto.getCategory(), dto.getAccountName(), 0);

	}

	/**
	 * 
	 * @param dateString
	 * @param message
	 * @param message_o
	 * @param amountStr
	 * @param type
	 * @param category
	 * @param account_name
	 * @param balence
	 */
	public void addTransaction(String dateString, String message, String note, String amountStr, String type,
			String category, String account_name, double balence) {

		Date date;

		// Create a DateTimeFormatter with the correct pattern
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");

		Account transaction_account = getAccountByName(account_name);
		// Parse the dateString using the formatter
		LocalDate ldate = LocalDate.parse(dateString, formatter);

		Double balenced = 0.0;
		date = Date.from(ldate.atStartOfDay(ZoneId.systemDefault()).toInstant());
		TransactionType c = TransactionType.valueOf(category);
		TransactionNature n = TransactionNature.valueOf(type);
		addTransaction(date, message, note, amountStr, n, c, transaction_account.getAccountNumber(), balenced);

	}

	public void recalculateLedger() {
		postedTransactionIds.clear();

		List<Account> accounts = this.accountManager.getAccounts();
		for (Account account : accounts) {
			account.clear();
		}

		List<Transaction> sorted_transactions = new ArrayList<>(this.transactions);
		Comparator<Transaction> comparator = new Comparator<Transaction>() {

			@Override
			public int compare(Transaction o1, Transaction o2) {
				int comp = o1.getDate().compareTo(o2.getDate());
				if (comp != 0)
					return comp;
				return o1.getSequence().compareTo(o2.getSequence());
			}
		};

		Collections.sort(sorted_transactions, comparator);
		for (Transaction t : sorted_transactions) {
			rePostTransaction(t);
		}
	}

}
