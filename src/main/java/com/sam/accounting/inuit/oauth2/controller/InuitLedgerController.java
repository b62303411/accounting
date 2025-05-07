package com.sam.accounting.inuit.oauth2.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.intuit.ipp.core.Context;
import com.intuit.ipp.core.ServiceType;
import com.intuit.ipp.data.Account;
import com.intuit.ipp.data.AccountSubTypeEnum;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.security.OAuth2Authorizer;
import com.intuit.ipp.services.DataService;
import com.intuit.ipp.services.QueryResult;
import com.intuit.ipp.util.Config;
import com.sam.accounting.inuit.oauth2.client.OAuth2PlatformClientFactory;
import com.sam.accounting.model.dto.AccountDTO;
import com.sam.accounting.model.entities.FixAccountInfo;
import com.sam.accounting.model.entities.qb.AccountManager;
import com.sam.accounting.model.entities.qb.AccountType;
import com.sam.accounting.repository.AccountRepository;
import com.sam.accounting.service.AccountFactory;
import com.sam.accounting.service.AccountService;
import com.intuit.ipp.data.AccountTypeEnum;
import com.intuit.ipp.data.ReferenceType;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/inuit")
public class InuitLedgerController {

	private static final Logger logger = Logger.getLogger(InuitLedgerController.class.getName());

	// Class-level variables for dependency injection
	private final AccountService accountService;
	private final AccountManager accountManager;
	private final AccountRepository accountRepository;
	private final FixAccountInfo accountInfo;
	private final OAuth2PlatformClientFactory oAuth2Factory;

	// Constructor with dependencies injected via Spring's @Autowired annotation
	@Autowired
	public InuitLedgerController(AccountService accountService, AccountManager accountManager,
			AccountRepository accountRepository, FixAccountInfo accountInfo, OAuth2PlatformClientFactory oAuth2Factory // Rename
																														// this
																														// to
																														// differentiate
																														// from
																														// AccountFactory
	) {
		// Initialize the class variables with the injected dependencies
		this.accountService = accountService;
		this.accountManager = accountManager;
		this.accountRepository = accountRepository;
		this.accountInfo = accountInfo;
		this.oAuth2Factory = oAuth2Factory;

		// Optional: Log the initialization
		logger.info("InuitLedgerController initialized with dependencies.");
	}

	private DataService getDataService(String realmId, String accessToken) throws FMSException {

		// create oauth object
		OAuth2Authorizer oauth = new OAuth2Authorizer(accessToken);
		// create context
		Context context = new Context(oauth, ServiceType.QBO, realmId);

		// create dataservice
		return new DataService(context);
	}

	public String getLedgerEntry() {
		String jsonInputString = "{" + "\"Line\": [{" + "\"Id\": \"1\","
				+ "\"Description\": \"Debit entry for purchase\"," + "\"Amount\": 500.00,"
				+ "\"DetailType\": \"JournalEntryLineDetail\"," + "\"JournalEntryLineDetail\": {"
				+ "\"PostingType\": \"Debit\"," + "\"AccountRef\": {\"value\": \"35\", \"name\": \"Bank\"}" + "}},"
				+ "{" + "\"Id\": \"2\"," + "\"Description\": \"Credit entry for purchase\"," + "\"Amount\": 500.00,"
				+ "\"DetailType\": \"JournalEntryLineDetail\"," + "\"JournalEntryLineDetail\": {"
				+ "\"PostingType\": \"Credit\"," + "\"AccountRef\": {\"value\": \"36\", \"name\": \"Sales\"}" + "}}]"
				+ "}";
		return jsonInputString;
	}

	public DataService getDataService(HttpSession session) {
		DataService service = null;
		// Retrieve the realmId and access token from the session
		String realmId = (String) session.getAttribute("realmId");
		String accessToken = (String) session.getAttribute("access_token");
		// Create the OAuth2 Authorizer and Context
		OAuth2Authorizer oauth = new OAuth2Authorizer(accessToken);
		try {
			Context context = new Context(oauth, ServiceType.QBO, realmId);
			// Create DataService to interact with the QuickBooks API
			service = new DataService(context);

		} catch (FMSException e) {
			logger.log(Level.SEVERE, "Error while creating account", e);
			// return "Error creating account.";
		}
		return service;

	}
	 public static String mapInternalToQuickBooks(String name, String internalType) {
	        switch (internalType) {
	            case "ASSET":
	            	if(name.contains("TD")) 
	            	{
	            		return "Bank";
	            	}
	                return "AccountsReceivable";  // Or map to another appropriate QuickBooks asset type
	            case "LIABILITY":
	                return "AccountsPayable";  // Or another liability account type in QuickBooks
	            case "EQUITY":
	                return "RetainedEarnings";
	            case "REVENUE":
	                return "SalesOfProductIncome";
	            case "EXPENSE":
	                return "AdvertisingPromotional";  // Map to appropriate QuickBooks expense type
	            case "SOMMATION":
	                return "OtherMiscellaneousExpense";  // Define the most fitting type for SOMMATION
	            default:
	                return "Unknown";  // Handle cases where the account type is unknown
	        }
	    }
	 public static String mapNameToQuickBooksSubtype(String accountName, AccountType accountType) {
	        switch (accountType) {
	            case REVENUE:
	                if (accountName.equalsIgnoreCase("Consulting Revenue")) {
	                    return "ServiceFeeIncome";
	                } else if (accountName.equalsIgnoreCase("Quick Method Benefit")) {
	                    return "MiscellaneousIncome";
	                }
	                break;
	            case EXPENSE:
	                if (accountName.equalsIgnoreCase("Salaries and Wages")) {
	                    return "WagesExpense";
	                } else if (accountName.equalsIgnoreCase("Professional Fees")) {
	                    return "ProfessionalServiceExpenses";
	                } else if (accountName.equalsIgnoreCase("Office Supplies")) {
	                    return "OfficeSupplies";
	                } else if (accountName.equalsIgnoreCase("Depreciation Expense")) {
	                    return "DepreciationExpense";
	                }
	                break;
	            case LIABILITY:
	                if (accountName.equalsIgnoreCase("Accounts Payable")) {
	                    return "AccountsPayable";
	                }
	                break;
	            case ASSET:
	            	if(accountName.contains("EVERY_DAY_A_BUSINESS_PLAN")) 
	            	{
	            		return "Checking";
	            	}
	            	else if(accountName.contains(""))
	            	{
	            		return "";
	            	}
	                if (accountName.equalsIgnoreCase("Accounts Receivable")) {
	                    return "AccountsReceivable";
	                } else if (accountName.equalsIgnoreCase("Prepaid Expenses")) {
	                    return "PrepaidExpenses";
	                }
	                break;
	            case EQUITY:
	                if (accountName.equalsIgnoreCase("Owner's Equity")) {
	                    return "RetainedEarnings";
	                }
	                break;
	            default:
	                return "Unknown";
	        }
	        return "Unknown";
	    }
	@RequestMapping("/createAccount")
	public String createAccount(HttpSession session) {

	    // Create DataService to interact with the QuickBooks API
	    DataService service = getDataService(session);
	    
	    if (service != null) {
	        
	        // Retrieve all internal accounts (DTOs)
	        List<AccountDTO> dtos = this.accountService.getAllAccounts();
	        
	        for (AccountDTO dto : dtos) {
	            try {
	                // Create a new QuickBooks account object
	                Account qbAccount = new Account();
	                
	                // Set account name from DTO
	                qbAccount.setName(dto.getAccountName()); // Use internal account name
	                
	                // Convert and set account type from DTO
	                //qbAccount.setAccountType(AccountTypeEnum.valueOf(dto.getAccountingType())); // Convert accounting type
	                
	                // Set account number and alias from DTO
	                qbAccount.setAcctNum(dto.getAccountNo()); // Set the account number
	                qbAccount.setAccountAlias(dto.getAlias()); // Set account alias if available
	                
	                String quickBookType  =mapInternalToQuickBooks(dto.getAccountName(),dto.getAccountingType());
	                String quickBookSubType = mapNameToQuickBooksSubtype(dto.getAccountName(),AccountType.valueOf(dto.getAccountingType()));
	                AccountTypeEnum type = AccountTypeEnum.fromValue(quickBookType);
	                qbAccount.setAccountType(type);
	                qbAccount.setAccountSubType(quickBookSubType);
	                qbAccount.setDescription(dto.getAccountName());
	               
	                
	                // Set the currency reference (assuming CAD)
	                ReferenceType currencyRef = new ReferenceType();
	                currencyRef.setValue("CAD"); // Set the currency to CAD (or other if needed)
	                qbAccount.setCurrencyRef(currencyRef);
	                
	                // Add the new account to QuickBooks using DataService
	                com.intuit.ipp.data.Account createdAccount = service.add(qbAccount);
	                
	                // Log and return success for each created account
	                logger.info("Created Account ID: " + createdAccount.getId());
	                logger.info("Created Account Name: " + createdAccount.getName());

	            } catch (FMSException e) {
	                logger.log(Level.SEVERE, "Error while creating account: " + dto.getAccountName(), e);
	                e.printStackTrace();
	                return "Error creating account: " + dto.getAccountName();
	            }
	        }
	        
	        return "Accounts created successfully.";
	        
	    } else {
	        logger.log(Level.SEVERE, "DataService is null. Unable to connect to QuickBooks.");
	        return "Error: Unable to connect to QuickBooks.";
	    }
	}

	@RequestMapping("/pushLedgerEntry")
	public String pushLedgerEntry(HttpSession session) {
		try {
			// Retrieve the realmId and access token from the session
			String realmId = (String) session.getAttribute("realmId");
			String accessToken = (String) session.getAttribute("access_token");

			// Set up the HTTP connection
			String url = oAuth2Factory.getPropertyValue("IntuitAccountingAPIHost") + "/v3/company/" + realmId
					+ "/journalentry";

			// set custom config
			Config.setProperty(Config.BASE_URL_QBO, url);
			// get DataService
			DataService service = getDataService(realmId, accessToken);
			String jsonInputString = getLedgerEntry();

			// Query accounts
			String sql = "SELECT * FROM Account";
			QueryResult queryResult = service.executeQuery(sql);

			// Process and log account info
			queryResult.getEntities().forEach(entity -> {
				Account account = (Account) entity;
				logger.info("Account: " + account.getName() + " ID: " + account.getId());
			});
		} catch (Exception e) {
		}

		return "";
	}
}
