package com.sam.accounting.model.entities.qb;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LedgerRuleFactory {
	@Autowired
	public AccountManager accountManager;
	
	public LedgerRuleFactory() {

	}

	/**
	 * 
	 * @param am
	 */
	public LedgerRuleFactory(AccountManager am) {
		this.accountManager = am;
	}

	
	/**
	 * 
	 * @param keywords
	 * @param client_vendor
	 * @param accountNo
	 * @return
	 */
	public ClassificationRule makeTravelAndMealRule(List<String> keywords, String client_vendor, String accountNo) {
		return makeExpence(keywords, client_vendor, "Travel & Meals",accountNo);
	}
	
	/**
	 * 
	 * @param keywords
	 * @param client_vendor
	 * @param accountNo
	 * @return
	 */
	public ClassificationRule makeBankFeeRule(List<String> keywords, String client_vendor, String accountNo) {
		return makeExpence(keywords, client_vendor, "Bank Fees",accountNo);
	}
	
	/**
	 * 
	 * @param keywords
	 * @param client_vendor
	 * @return
	 */
	public ClassificationRule makeOfficeSuppliesRule(List<String> keywords, String client_vendor,String accountNo) {
		return makeExpence(keywords, client_vendor, "Office Supplies",accountNo);
	}

	/**
	 * 
	 * @param keywords
	 * @param client_vendor
	 * @return
	 */
	public ClassificationRule makeSasRule(List<String> keywords, String client_vendor,String accountNo) {
		return makeExpence(keywords, client_vendor, "Software SAS",accountNo);
	}

	/**
	 * 	+------------+---------+---------------------+-----+--------------+----------+----------+
     *	| Date       | Type    | Name                | No  | Vendor       |  Debit   | Credit   |
     *	+------------+---------+---------------------+-----+--------------+----------+----------+
     *	| 01/02/2023 | EXPENSE | Professional Fees   | 028 | Accountant   | $2,500.00|     -    |
     *	| 01/02/2023 | ASSET   | Checking Account    | 029 | Accountant   |   -      | $2,500.00|
     *	+------------+---------+---------------------+-----+--------------+----------+----------+
	 * @param keywords
	 * @param client_vendor
	 * @return
	 */
	public ClassificationRule makeProfessionalFeesRule(List<String> keywords, String client_vendor,String accountNo) 
	{
		ClassificationRule rule = new ClassificationRule();
		rule.setKeyWords(keywords);
		rule.client_vendor = client_vendor;
		rule.debited = "Professional Fees";
		rule.credited = accountManager.getAccountByAccountNo(accountNo).getName();
		rule.addAccountNumber(accountNo);
		return rule;
	}
	
	/**
	 * 	+------------+----------+---------------------+-----+--------------+---------+--------+
     *	| Date       | Type     | Name                | No  | Vendor/Client|  Debit  | Credit |
     *	+------------+----------+---------------------+-----+--------------+---------+--------+
     *	| 02/01/2023 | EXPENSE  | Office Supplies     | 003 | Amazon       | $500.00 |    -   |
     *	| 02/01/2023 | LIABILITY| Credit Card Payable | 004 | Amazon       |    -    |$500.00 |
     *	+------------+----------+---------------------+-----+--------------+---------+--------+
	 * @param keywords
	 * @param client_vendor
	 * @param debited
	 * @return
	 */
	public ClassificationRule makeExpence(List<String> keywords, String client_vendor, String debited,String accountNo) {
		ClassificationRule rule = new ClassificationRule();
		rule.setKeyWords(keywords);
		rule.client_vendor = client_vendor;
		rule.addAccountNumber(accountNo);
		rule.debited = debited;
		rule.credited = accountManager.getAccountByAccountNo(accountNo).getName();
	
		return rule;
	}

/**
 * 
 * @param keywords
 * @param vendor
 * @param debited
 * @param accountNo
 * @return
 */
	public ClassificationRule makeOfficeEquipementPurchaceRule(List<String> keywords, String vendor,  String debited,String credited) {
		ClassificationRule rule = new ClassificationRule();
		rule.setKeyWords(keywords);
		rule.client_vendor = vendor;
		Account d = accountManager.getAccountByName(debited);
		rule.addAccountNumber(d.getAccountNumber());
		rule.debited = debited;
		rule.credited = credited;
	
		return rule;
	}

public ClassificationRule makeTrainingRule(List<String> keywords, String vendor, String accountNo) {
	ClassificationRule rule = new ClassificationRule();
	rule.setKeyWords(keywords);
	rule.client_vendor = vendor;
	rule.debited = "Training";
	if(  accountManager.getAccountByAccountNo(accountNo) == null)
	{
		accountManager.addAccount(vendor, accountNo, AccountType.ASSET, false);
	}
	rule.credited = accountManager.getAccountByAccountNo(accountNo).getName();
	rule.addAccountNumber(accountNo);
	return rule;
}



	
}
