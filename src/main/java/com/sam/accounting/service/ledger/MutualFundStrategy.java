package com.sam.accounting.service.ledger;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sam.accounting.model.TransactionNature;
import com.sam.accounting.model.entities.FixAccountInfo;
import com.sam.accounting.model.entities.qb.Account;
import com.sam.accounting.model.entities.qb.AccountManager;
import com.sam.accounting.model.entities.qb.TransactionAccount;

@Service
public class MutualFundStrategy implements TransactionStrategy{

	@Autowired
	public AccountManager accountManager;
	
	@Autowired
	public FixAccountInfo accounts;
	
	
	//	+------------+---------+--------------------------+-----+-----------------+-----------+-----------+
	//	| Date       | Type    | Name                     | No  | Vendor/Client   |  Debit    | Credit    |
	//	+------------+---------+--------------------------+-----+-----------------+-----------+-----------+
	//	| 01/03/2023 | ASSET   | Checking Account         | 032 | Mutual Fund Co. | $10,000.00|   -       |
	//	| 01/03/2023 | ASSET   | Investment in Mutual Fund| 033 | Mutual Fund Co. |   -       | $10,000.00|
	//	+------------+---------+--------------------------+-----+-----------------+-----------+-----------+
	/**
	 * 
	 * @param amount
	 * @param transactionAccount
	 * @param cardinality
	 * @param balence
	 */
	public void handleMutualFundPurchace(
			double amount,
			Account transactionAccount,
			TransactionAccount cardinality,
			Double balence)
	{
		Account font_mutuel = getMutualFundAccount();
		cardinality.setCredited(transactionAccount);
		cardinality.debited = font_mutuel;
		cardinality.credited_balence = balence;
		cardinality.amount=amount;
	}
	
	//	+------------+---------+--------------------------+-----+-----------------+-----------+-----------+
	//	| Date       | Type    | Name                     | No  | Vendor/Client   |  Debit    | Credit    |
	//	+------------+---------+--------------------------+-----+-----------------+-----------+-----------+
	//	| 01/03/2023 | ASSET   | Checking Account         | 032 | Mutual Fund Co. | -         | $10,000.00|
	//	| 01/03/2023 | ASSET   | Investment in Mutual Fund| 033 | Mutual Fund Co. | $10,000.00| -         |
    //	| 01/03/2023 | ASSET   | Unrealized Gain          | 034 | Mutual Fund Co. | -         | $1,000.00 |
	//	| 01/03/2023 | ASSET   | Capital Gain             | 035 | Mutual Fund Co. | $1,000.00 | -         |
	//	+------------+---------+--------------------------+-----+-----------------+-----------+-----------+
	public void handleMutualFundSold(
			double amount,
			Account transactionAccount,
			TransactionAccount cardinality, 
			Double balence)
	{
		Account font_mutuel = getMutualFundAccount();
		//cardinality.setCredited(font_mutuel);
		//cardinality.debited = checkingAccount;
		
		TransactionAccount cash_in_cash_out = new TransactionAccount();
		cash_in_cash_out.setCredited(font_mutuel);
		cash_in_cash_out.debited= transactionAccount;
		cash_in_cash_out.amount=amount;
		
		TransactionAccount realized_unrealized = new TransactionAccount();
		Account unrealized= accountManager.getAccountByName("Unearned Revenue");
		Account realized = accountManager.getAccountByName("Realized Gain on Sale of Investments");
		double gain_amount=14872.90;	
		
		double total_value  = 60000+gain_amount;
		
			
		double distributed_gain = amount/total_value * gain_amount;
				
		realized_unrealized.setCredited(realized);
		realized_unrealized.debited=unrealized;
		realized_unrealized.amount=distributed_gain;
		
		cardinality.split = new ArrayList<TransactionAccount>();		
		cardinality.split.add(cash_in_cash_out);
		cardinality.split.add(realized_unrealized);
	}
	
	
	public void populate(
			double amount, 
			Account transactionAccount, 
			TransactionAccount cardinality, 
			TransactionNature type,
			Double balence) {
		if(transactionAccount.getAccountNumber().equals(accounts.checkingAccount.accountNo)) 
		{
			switch (type) {
			case Credit:
				handleMutualFundPurchace(amount,transactionAccount,cardinality,balence);
				break;
			case Debit:
				handleMutualFundSold(amount,transactionAccount,cardinality,balence);
				break;
			}
		}
		else 
		{
			Account font_mutuel = getMutualFundAccount();
			cardinality.setCredited(transactionAccount);
			cardinality.debited = font_mutuel;
			cardinality.credited_balence = balence;
			cardinality.amount=amount;
		}
		Account font_mutuel = getMutualFundAccount();


		cardinality.vendor_client = "Self";
		//cardinality.amount = Math.abs(amount);
	}


	private Account getMutualFundAccount() {
		Account font_mutuel = accountManager.getAccountByName("FONDS MUTUELS TD");
		return font_mutuel;
	}




}
