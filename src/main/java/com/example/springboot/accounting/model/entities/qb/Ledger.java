package com.example.springboot.accounting.model.entities.qb;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.springboot.accounting.model.Sequence;
import com.example.springboot.accounting.service.TaxService;

public class Ledger {
	private static final String Owner = "Samuel Audet-Arsenault";
	private AccountManager accountManager;
	private Set<String> postedTransactionIds;
	private Set<Transaction> transactions;
	private ClassificationRule taxes_rule;
	private ClassificationRule amazon_rule;
	private LedgerRuleFactory factory;
	private List<ClassificationRule> operatingExpensesRules;
	private List<ClassificationRule> assetPurchaceRules;
	private List<String> vendors;
	private TaxService taxService;
	String creditCardAccountNo = "7053";
	String checkAccountNo = "5235425";
	private Sequence seq;

	public Sequence getSeq() {
		return seq;
	}

	protected void setSeq(Sequence seq) {
		this.seq = seq;
	}

	public Ledger(AccountManager manager, LedgerRuleFactory factory, TaxService taxService) {
		seq = new Sequence();
		this.factory = factory;
		this.accountManager = manager;
		this.postedTransactionIds = new HashSet<>();
		this.transactions = new HashSet<Transaction>();
		this.operatingExpensesRules = new ArrayList<ClassificationRule>();
		this.assetPurchaceRules = new ArrayList<ClassificationRule>();
		this.vendors = new ArrayList<String>();
		this.taxService = taxService;
		vendors.add("Cloutier & Longtin");
		vendors.add("MTA");

		taxes_rule = new ClassificationRule();
		taxes_rule.addKeyWord("QUEBEC GOV'T");
		taxes_rule.addKeyWord("ARC");
		taxes_rule.addKeyWord("Ministere du revenue");
		taxes_rule.addKeyWord("Revenue QC");
		taxes_rule.addKeyWord("MRQ");
		taxes_rule.addKeyWord("Revenue Quebec");
		taxes_rule.addKeyWord("CRA");
		taxes_rule.addAccountNumber(checkAccountNo);
		taxes_rule.addAccountNumber("1");

		amazon_rule = new ClassificationRule();

		amazon_rule.addAccountNumber(creditCardAccountNo);
		amazon_rule.addKeyWord("AMZNMktpCA");
		amazon_rule.addKeyWord("amazon");
		amazon_rule.addKeyWord("Amazon.ca");
		amazon_rule.addKeyWord("Amazon*");
		amazon_rule.addKeyWord("Amazon");

	}

	public AccountManager getAccountManager() {
		return accountManager;
	}

	public void setAccountManager(AccountManager accountManager) {
		this.accountManager = accountManager;
	}

	public void createRules() {
		
		
		addTrainingPurchaceRule(creditCardAccountNo,List.of("SANS"),"SANS Institute");
		
		addOfficeEquipementPurchaceRule(creditCardAccountNo,List.of("Amazon","AMZNMktpCA"),
				"Amazon");
		addOfficeEquipementPurchaceRule(creditCardAccountNo,List.of("BESTBUY"),
				"BESTBUY");
		addOfficeSuppliesRule(creditCardAccountNo, List.of("NEWEGG"), "NEWEGG");
		addOfficeSuppliesRule(creditCardAccountNo, List.of("Microsoft*Store"), "Microsoft");
		addOfficeSuppliesRule(creditCardAccountNo, List.of("Acme"), "Acme");
		addOfficeSuppliesRule(creditCardAccountNo, List.of("Dri Nvidia"), "Dri Nvidia");
		addOfficeSuppliesRule(creditCardAccountNo, List.of("Sears"), "Sears");
		addOfficeSuppliesRule(creditCardAccountNo, List.of("Best Buy"), "Best Buy");
		addOfficeSuppliesRule(creditCardAccountNo, List.of("Sparkfun"), "Sparkfun");
		addOfficeSuppliesRule(creditCardAccountNo, List.of("Master Vox Electronique"), "Master Vox");
		addOfficeSuppliesRule(creditCardAccountNo, List.of("AMZN Mktp ","AMZNMktpCA", "amazon", "Amazon.ca", "Amazon*"),
				List.of("PrimeMemberamazon"), "Amazon");
		addOfficeSuppliesRule(creditCardAccountNo, List.of("MAGASINCDNTIRE"), "MAGASIN CDN TIRE");
		addOfficeSuppliesRule(creditCardAccountNo, List.of("THEHOMEDEPOT"), "THE HOME DEPOT");
		addOfficeSuppliesRule(creditCardAccountNo, List.of("FEDEX-YUDMONTREAL"), "FEDEX");
		addOfficeSuppliesRule(creditCardAccountNo, List.of("DOLLARAMA"), "DOLLARAMA");
		addOfficeSuppliesRule(creditCardAccountNo, List.of("BUREAUENGROS", "STAPLES"), "BUREAU EN GROS");
		addOfficeSuppliesRule(creditCardAccountNo, List.of("COOP", "COOP ETS MONTREAL"), "COOP ETS MONTREAL");

		addTravelAndMealsRule(creditCardAccountNo, List.of("Nettoyeur"), "Nettoyeur");
		addTravelAndMealsRule(creditCardAccountNo, List.of("Subway"), "Subway");
		addTravelAndMealsRule(creditCardAccountNo, List.of("Okane"), "Okane");
		addTravelAndMealsRule(creditCardAccountNo, List.of("Kanda"), "Kanda");
		addTravelAndMealsRule(creditCardAccountNo, List.of("Vinci"), "Vinci");
		addTravelAndMealsRule(creditCardAccountNo, List.of("Zibo","RESTAURANT ZIBO"), "Zibo");
		addTravelAndMealsRule(creditCardAccountNo, List.of("Amir"), "Amir");
		addTravelAndMealsRule(creditCardAccountNo, List.of("Billet Ad Com"), "Billet Ad Com");
		addTravelAndMealsRule(creditCardAccountNo, List.of("Watan"), "Watan Boucherie");
		addTravelAndMealsRule(creditCardAccountNo, List.of("Rest Shaan Tandoori"), "Rest Shaan Tandoori");
		addTravelAndMealsRule(creditCardAccountNo, List.of("Hoang Huong"), "Hoang Huong");
		addTravelAndMealsRule(creditCardAccountNo, List.of("Thai Express"), "Thai Express");
		addTravelAndMealsRule(creditCardAccountNo, List.of("La Cuisine De"), "La Cuisine De");
		addTravelAndMealsRule(creditCardAccountNo, List.of("New York Grill"), "New York Grill");
		addTravelAndMealsRule(creditCardAccountNo, List.of("Basha"), "Basha");
		addTravelAndMealsRule(creditCardAccountNo, List.of("Piazza Pazza"), "Piazza Pazza");
		addTravelAndMealsRule(creditCardAccountNo, List.of("Watan Boucherie"), "Amigos Resto");
		addTravelAndMealsRule(creditCardAccountNo, List.of("Amigos Resto"), "Amigos Resto");
		addTravelAndMealsRule(creditCardAccountNo, List.of("Grillades Torino"), "Grillades Torino");
		addTravelAndMealsRule(creditCardAccountNo, List.of("Les Brasseurs Brossard"), "Les Brasseurs Brossard");
		addTravelAndMealsRule(creditCardAccountNo, List.of("PARKINGOTTAWA"), "PARKING OTTAWA");
		addTravelAndMealsRule(creditCardAccountNo, List.of("INDIGO"), "INDIGO");
		addTravelAndMealsRule(creditCardAccountNo, List.of("VINCIPARK","VINCI PARK"), "VINCI PARK TOUR ALTITU MONTREAL");
		addTravelAndMealsRule(creditCardAccountNo, List.of("ECOLEDETECHNOLOGIQPSES"), "ECOLE DE TECHNOLOGI QPSES");
		addTravelAndMealsRule(creditCardAccountNo, List.of("RESTAURANTZIBOMONTREAL"), "RESTAURANT ZIBO MONTREAL");
		addTravelAndMealsRule(creditCardAccountNo, List.of("IVANHOECAMBRIDGEINC","IVANHOE"), "IVANHOE CAMBRIDGE INC");
		addTravelAndMealsRule(creditCardAccountNo, List.of("LOT39MONTREAL"), "LOT 39 MONTREAL");
		addTravelAndMealsRule(creditCardAccountNo, List.of("OLDDUBLINPUBMONTREAL"), "OLD DUBLIN PUB MONTREAL");
		addTravelAndMealsRule(creditCardAccountNo, List.of("SERVICESDETRANSPORTADORVAL"),
				"SERVICES DE TRANSPORT A DORVAL");

		addSasRule(List.of("PrimeMemberamazon"), "Amazon.ca", creditCardAccountNo);
		addSasRule(List.of("ADOBESEND", "AdobeInc"), "Adobe Inc", creditCardAccountNo);
		addSasRule(List.of("GOOGLE"), "Google", creditCardAccountNo);

		addProfessionalFeeRule(checkAccountNo, List.of("Northon and rose"), "Northon and rose");
		addProfessionalFeeRule(checkAccountNo,
				List.of("Cloutier & Longtin", "Cloutier Longtin Accounting", "Cloutier Longtin"), "Cloutier & Longtin");
		addBankFeeRule(checkAccountNo, List.of("FRAIS MENS PLAN SERV", "FRAIS-COMMANDE CHEQ"), "TD");
	}

	/**
	 * 
	 * @param accountNo
	 * @param keywords
	 * @param vendor
	 */
	private void addTrainingPurchaceRule(String accountNo, List<String> keywords, String vendor) {
		ClassificationRule amp = factory.makeTrainingRule(keywords, vendor, accountNo);
		operatingExpensesRules.add(amp);
		
	}

	private void addBankFeeRule(String accountNo, List<String> keywords, String vendor) {
		ClassificationRule amp = factory.makeBankFeeRule(keywords, vendor, accountNo);
		operatingExpensesRules.add(amp);
	}

	/**
	 * 
	 * @param accountNo
	 * @param keywords
	 * @param vendor
	 */
	private void addTravelAndMealsRule(String accountNo, List<String> keywords, String vendor) {
		ClassificationRule amp = factory.makeTravelAndMealRule(keywords, vendor, accountNo);
		operatingExpensesRules.add(amp);
	}
	
	private void addOfficeEquipementPurchaceRule(String accountNo, List<String> keywords, String vendor) {		
		Account creditCard = getCreditCardAccount();
		Account oe = getAccountByName("Office Equipment");		
		ClassificationRule amp = factory.makeOfficeEquipementPurchaceRule(
				keywords, 
				vendor, 
				"Office Equipment",
				"VISA_TD_REMISES_AFFAIRES"
				);
		this.assetPurchaceRules.add(amp);
	}

	/**
	 * 
	 * @param accountNo
	 * @param keywords
	 * @param vendor
	 */
	private void addProfessionalFeeRule(String accountNo, List<String> keywords, String vendor) {
		ClassificationRule amp = factory.makeProfessionalFeesRule(keywords, vendor, accountNo);
		operatingExpensesRules.add(amp);
	}

	/**
	 * 
	 * @param keywords
	 * @param vendor
	 * @param accountNo
	 */
	private void addSasRule(List<String> keywords, String vendor, String accountNo) {
		ClassificationRule amp = factory.makeSasRule(keywords, vendor, accountNo);
		operatingExpensesRules.add(amp);
	}

	private void addOfficeSuppliesRule(String accountNo, List<String> keywords, String vendor) {
		ClassificationRule amp = factory.makeOfficeSuppliesRule(keywords, vendor, accountNo);
		operatingExpensesRules.add(amp);
	}

	private void addOfficeSuppliesRule(String accountNo, List<String> keywords, List<String> exception, String vendor) {
		ClassificationRule amp = factory.makeOfficeSuppliesRule(keywords, vendor, accountNo);
		for (String string : exception) {
			amp.addExcludeWord(string);
		}
		operatingExpensesRules.add(amp);
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
	public void addTransaction(Date date, String message, String message_o, String amountStr, String type,
			String category, String account, Double balence) {
		Account receivable = accountManager.getAccount("Accounts Receivable");
		double amount = Double.parseDouble(amountStr);
		// Transaction transaction = new Transaction();
		Account relatedAccount;

		Account checkingAccount = accountManager.getAccountByName("TD_EVERY_DAY_A_BUSINESS_PLAN");
		TransactionAccount cardinality = new TransactionAccount();
		String vendor_client = "";
		List<String> words = new ArrayList<String>();
		words.add(message.trim());
		if (message_o != null)
			words.add(message_o);

		switch (category) {
		case "Transfer":
			if (message.equals("TRAITE $CA 01755011")) {
				handleLoanToOwner(amount, checkingAccount, cardinality);
			} else if (message.equals("DEPOT") && checkAccountNo.equals(account)) {
				populateDepot(amount, checkingAccount, cardinality);
			} else if (message.contains("CORRECTION")) {
				cardinality.amount = amount;
				cardinality.vendor_client = "self";
				switch (type) {
				case "Debit":
					cardinality.debited = checkingAccount;
					cardinality.setCredited(getAccountByName("Unknown"));
					break;
				case "Credit":
					cardinality.debited = getAccountByName("Unknown");
					cardinality.setCredited(checkingAccount);
					break;

				}

			} else {
				if (account.contains(checkAccountNo))
					populateFontMutuel(amount, checkingAccount, cardinality, type, balence);
				else {
					System.err.println();
				}
			}
			break;
		case "Bank Fee":
			handleBankFee(amount, checkingAccount, cardinality, balence);
			break;
		case "Income":
			if (account.contains(checkAccountNo)) {
				if (null != message_o && message_o.contains("RED SOLDE CPTE")
						|| null != message_o && message_o.contains("Red Solde")) {

					handleBankFeeRefund(amount, checkingAccount, cardinality);
				} else {
					handleSalesRevenue(message, message_o, receivable, amount, checkingAccount, cardinality);
				}
			} else if (account.contains(creditCardAccountNo)) {
				if (message.contains("REMISEENARGENTTD")||message.contains("ANNUAL")) {
					handleCreditCardCashback(amount, cardinality);
				}
			}
			// postTransaction(ledger, checkingAccount, relatedAccount, amount, type);

			break;
		case "Credit Card Payment":
			handleCreditCardPayment(amount, checkingAccount, cardinality, balence);
			break;
		case "Dividend & Cap Gains":
			handleDividend(amount, checkingAccount, cardinality, balence);
			break;
		case "Invoice":
			handleInvoice(message, message_o, type, receivable, amount, cardinality, balence);
			break;
		case "OperatingExpenses":
			handleOperatingExpense(message, message_o, words, account, amount, checkingAccount, cardinality,
					vendor_client, balence);

			break;
		case "AssetPurchased":
			handleAssetPurchaced(message, words, account, amount, cardinality, balence);
			break;
		case "DeptRepayment":
			if (message.contains("TPS&TVQ") || (null != message_o && message_o.contains("TPS&TVQ"))) {
				populateSalesTaxPayment(amount, checkingAccount, cardinality, balence);
			} else if (this.taxes_rule.keyWordFount(account, words)) {
				populateTax(amount, checkingAccount, cardinality, balence);
			} else if (message.contains("PMT PREAUTOR VISA TD")) {
				handleCreditCardPayment(amount, checkingAccount, cardinality, balence);
			} else if (message.contains("PAIEMENTPRÉAUTORISÉ")) {
				// handleCreditCardPayment(date, amount, checkingAccount, cardinality);
			} else if (message.contains("View Cheque CHQ")) {
				if (amount > 4000)
					populateTax(amount, checkingAccount, cardinality, balence);
				else {
					if (account.contains(checkAccountNo))
						populateToClassify(amount, checkingAccount, cardinality, balence);
				}
			} else {
				if (account.contains(checkAccountNo))
					populateToClassify(amount, checkingAccount, cardinality, balence);
			}

			break;
		case "Dividend":
			if (message.contains("FONDS MUTUELS")) {

				populateFontMutuel(amount, checkingAccount, cardinality, type, balence);
			} else {
				if (message.contains("TRAITE $CA 01755011")) {
					handleLoanToOwner(amount, checkingAccount, cardinality);
				} else
					handleDividend(amount, checkingAccount, cardinality, balence);
			}
			break;
		// ... Handle other categories similarly ...
		case "SalesRevenue":
			handleSalesRevenue(message, message_o, receivable, amount, checkingAccount, cardinality);
			break;
		case "Liability":
			if (this.taxes_rule.keyWordFount(account, words)) {
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

			break;
		case "Credit":
			if (message.contains("FONDS MUTUELS TD")) {
				populateFontMutuel(amount, checkingAccount, cardinality, type, balence);
			} else if (message.contains("DEPOT")) {
				populateDepot(amount, checkingAccount, cardinality);
			} else {
				populateToClassify(amount, checkingAccount, cardinality, balence);
			}

			break;
		case "Debit":
			System.err.println();
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

			} else {
				populateToClassify(amount, checkingAccount, cardinality, balence);
			}
			if (this.taxes_rule.keyWordFount(account, words)) {
				populateTax(amount, checkingAccount, cardinality, balence);

			} else {
				if (account.contains(checkAccountNo))
					populateToClassify(amount, checkingAccount, cardinality, balence);
			}
			break;
		case "LostOfAssetWriteOff":
			this.handleLostOfAssetWriteOff(cardinality, amount);
			break;
		case "Depreciation":
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
		case "Unknown":
			if (this.taxes_rule.keyWordFount(account, words)) {
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
				if (type.contains("Credit")) {
					if (message.contains("ANNUALCASHBACKCREDIT")) {
						handleBankFeeRefund(amount, checkingAccount, cardinality);
					} else {
						if (account.contains(checkAccountNo)) {
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
//						cardinality.credited = accountManager.getAccountByName("Miscellaneous Revenue");
//						cardinality.vendor_client = "VISA";
//						cardinality.debited = getCreditCardAccount();
//						cardinality.amount = amount;
						handleCreditCardCashback(amount, cardinality);
					} else {
						if (account.contains(checkAccountNo)) {
							populateToClassify(amount, checkingAccount, cardinality, balence);

						} else
							System.err.println(message);
					}
				}

			}

			break;
		default:
			throw new IllegalArgumentException("Unsupported category: " + category);
		}
		if (cardinality.debited == null || cardinality.creditedAccounts.isEmpty()) {

			if (cardinality.split != null) {
				for (TransactionAccount ta : cardinality.split) {
					postTransaction(ta, date, message);
				}
			} else {
				System.err.println("not splitted yet null " + message + " " + account);
				if (account.contains(checkAccountNo)) {
					populateToClassify(amount, checkingAccount, cardinality, balence);
					postTransaction(cardinality, date, message);
				}
			}

		} else {
			postTransaction(cardinality, date, message);
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

//TransactionAccount incomeTaxInvoice = new TransactionAccount();
		TransactionAccount incomeTaxPayment = new TransactionAccount();

		String vendor = "Revenue Agency";
		Account payable = getAccountByName("Taxes Payable");

//		incomeTaxInvoice.debited = accountManager.getAccountByName("Income Tax Expense");
//		incomeTaxInvoice.credited = payable;
//		incomeTaxInvoice.amount = Math.abs(amount);
//
//		incomeTaxInvoice.vendor_client = vendor;

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
	private void populateFontMutuel(double amount, Account checkingAccount, TransactionAccount cardinality, String type,
			Double balence) {
		Account font_mutuel = accountManager.getAccountByName("FONDS MUTUELS TD");
		switch (type) {
		case "Credit":
			cardinality.setCredited(checkingAccount);
			cardinality.debited = font_mutuel;
			cardinality.credited_balence = balence;
			break;
		case "Debit":
			cardinality.setCredited(font_mutuel);
			cardinality.debited = checkingAccount;
			break;
		}

		cardinality.vendor_client = "Self";
		cardinality.amount = Math.abs(amount);
	}

	private void handleOperatingExpense(String message, String message_o, List<String> words, String account,
			double amount, Account checkingAccount, TransactionAccount cardinality, String vendor_client,
			Double balence) {

		for (ClassificationRule rule : operatingExpensesRules) {
			if (rule.keyWordFount(account, words)) {
				rule.populate(cardinality, amount, accountManager, balence, account);
				return;
			}
		}
		if (message.contains("RED SOLDE CPTE")) {
			handleBankFeeRefund(amount, checkingAccount, cardinality);
		} else {
			if (account.contains(checkAccountNo)) {
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
		
		for (ClassificationRule rule : assetPurchaceRules) {
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
	private void handleInvoice(String message, String message_o, String type, Account receivable, double amount,
			TransactionAccount cardinality, Double balence) {
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

	public void addTransaction(String dateString, String message, String message_o, String amountStr, String type,
			String category, String account, double balence) {
		Date date;

		// Create a DateTimeFormatter with the correct pattern
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");

		// Parse the dateString using the formatter
		LocalDate ldate = LocalDate.parse(dateString, formatter);

		Double balenced = 0.0;
		date = Date.from(ldate.atStartOfDay(ZoneId.systemDefault()).toInstant());

		addTransaction(date, message, message_o, amountStr, type, category, account, balenced);

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
