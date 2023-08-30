package com.example.springboot.accounting.model.dto;

import java.util.Date;

public class LedgerEntryDTO {
	    private Date date;
	    private String accountType; // could be an Enum depending on your predefined account types
	    private String glAccountName;
	    private String glAccountNumber; // using String in case account numbers have non-numeric characters, otherwise can be an int or long
	    private String vendorOrClient;
	    private Double debit;  // using Double for currency values, but BigDecimal is more accurate for financial calculations
	    private Double credit;
	    private Double balence;
	    private Double abalence;
	    private String message;
	    // Standard getters and setters for each field

	    public Date getDate() {
	        return date;
	    }

	    public long getUnixTime() 
	    {
	    	return date.getTime();
	    }
	    
	    public void setDate(Date date) {
	        this.date = date;
	    }

	    public String getAccountType() {
	        return accountType;
	    }

	    public void setAccountType(String accountType) {
	        this.accountType = accountType;
	    }

	    public String getGlAccountName() {
	        return glAccountName;
	    }

	    public void setGlAccountName(String glAccountName) {
	        this.glAccountName = glAccountName;
	    }

	    public String getGlAccountNumber() {
	        return glAccountNumber;
	    }

	    public void setGlAccountNumber(String glAccountNumber) {
	        this.glAccountNumber = glAccountNumber;
	    }

	    public String getVendorOrClient() {
	        return vendorOrClient;
	    }

	    public void setVendorOrClient(String vendorOrClient) {
	        this.vendorOrClient = vendorOrClient;
	    }

	    public Double getDebit() {
	        return debit;
	    }

	    public void setDebit(Double debit) {
	        this.debit = debit;
	    }

	    public Double getCredit() {
	        return credit;
	    }

	    public void setCredit(Double credit) {
	        this.credit = credit;
	    }

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public Double getBalence() {
			return balence;
		}

		public void setBalence(Double balence) {
			this.balence = balence;
		}

		public Double getAbalence() {
			return abalence;
		}

		public void setAbalence(Double abalence) {
			this.abalence = abalence;
		}
	    
}
