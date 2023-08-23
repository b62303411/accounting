package com.example.springboot.accounting.model.entities.qb;

public class TransactionEntry {
	private Account account;
	private double amount;
	private EntryType type; // Debit or Credit
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setType(EntryType type) {
		this.type = type;
	}

	public void setVendor_client(String vendor_client) {
		this.vendor_client = vendor_client;
	}

	private String vendor_client;
	private String date;
	/**
	 * 
	 * @param account
	 * @param vendor_client
	 * @param date
	 * @param amount
	 * @param type
	 */
	public TransactionEntry(Account account,String vendor_client, String date, double amount, EntryType type) {
		this.account = account;
		this.amount = amount;
		this.type = type;
		this.date = date;
		this.vendor_client=vendor_client;
	}

	public TransactionEntry() {

	}

	public void post() {
		if (EntryType.DEBIT.equals(type)) {
			account.debit(amount);
		} else if (EntryType.CREDIT.equals(type)) {
			account.credit(amount);
		}
	}

	public void setAccount(Account acc) {
		this.account=acc;
		
	}

	public void setAmount(double amount) {
		this.amount=amount;
		
	}
	
	 @Override
	    public String toString() {
		    double credit=0;
			double debit=0;
			//Double credit = "	";
		    //Double debit = "	";
		 	switch(type) 
		 	{ 
		 	case CREDIT:
		 		credit = amount;
		 		break;
		 	case DEBIT:
		 		debit = amount;
		 		
		 	}
		 	
		 	return String.format("%-10s\t%-10s\t%-25s\t%s\t%-20s\t%.2f\t%.2f", 
		 			date,
		 			account.getAccountType().name(), 
		 			account.getName() , 
		 			account.getAccountNumber(), 
		 			vendor_client, 
		            debit, 
		            credit);
		 	
//	        return account.getAccountType() + "\t ," + 
//	               account.getName() + "\t ," + 
//	               account.getAccountNumber() + "\t ," + 
//	               vendor_client + "\t ," + 
//	               debit + "\t ," + 
//	               credit;
	    }

	public EntryType getType() {
	
		return type;
	}

	public Double getAmount() {
		return amount;
	}

	public String getVendor_client() {

		return vendor_client;
	}

	public Account getAccount() {
	
		return account;
	}
}
