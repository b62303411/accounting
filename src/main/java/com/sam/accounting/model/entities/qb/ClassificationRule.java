package com.sam.accounting.model.entities.qb;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassificationRule {
	
	public Set<String> keywords;
	public Set<String> exclustion;
	public String debited;
	public String credited;
	public String client_vendor;
	public HashSet<String> accountNumbers;
    public ClassificationRule()
	{
		keywords= new HashSet<String>();
		exclustion= new HashSet<String>();
		accountNumbers = new HashSet<String>();
	}
    
    public void addKeyWord(String kw) 
    {
    	keywords.add(kw);
    }
    
    public void addExcludeWord(String kw) 
    {
    	exclustion.add(kw);
    }
    
    public boolean exclusionWordFount(List<String> fields) 
	{
		for (String field : fields) {
			for (String word : exclustion) {
				if(field.contains(word))
					return true;
			}				
		}
		return false;
	}
    
   
	public boolean keyWordFount(String accountNo,List<String> fields) 
	{
		
		if(!this.accountNumbers.contains(accountNo))
			return false;
		
		if(exclusionWordFount(fields)) 
		{
			return false;
		}
		
		for (String field : fields) {
			for (String word : keywords) {
				if(field.contains(word))
					return true;
			}				
		}
		return false;
	}
	
	public void setKeyWords(Collection<String> inputKeywords) {
		keywords.addAll(inputKeywords);		
	}
	
	/**
	 * 
	 * @param ta
	 * @param amount
	 * @param manager
	 * @param balence 
	 */
	public void populate(TransactionAccount ta,double amount,AccountManager manager, Double balence,String account) 
	{
		ta.setCredited(manager.getAccountByName(this.credited));
		ta.debited = manager.getAccountByName(this.debited);
		for (Account acc : ta.creditedAccounts) {
			if(acc.getAccountNumber().equals(account)) 
			{
				ta.credited_balence=balence;
				break;
			}		
		}

		ta.amount=Math.abs(amount);		
		ta.vendor_client = this.client_vendor;
	}

	public void addAccountNumber(String no) {
		accountNumbers.add(no);
		
	}

}
