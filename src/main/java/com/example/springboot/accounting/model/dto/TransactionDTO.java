package com.example.springboot.accounting.model.dto;

public class TransactionDTO {
	    private String date;
	    private String description;
	    private String originalDescription;
	    private double amount;
	    private String transactionType;
	    private String category;
	    private String accountName;
	    private String labels;
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getOriginalDescription() {
			return originalDescription;
		}
		public void setOriginalDescription(String originalDescription) {
			this.originalDescription = originalDescription;
		}
		public double getAmount() {
			return amount;
		}
		public void setAmount(double amount) {
			this.amount = amount;
		}
		public String getTransactionType() {
			return transactionType;
		}
		public void setTransactionType(String transactionType) {
			this.transactionType = transactionType;
		}
		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
			this.category = category;
		}
		public String getAccountName() {
			return accountName;
		}
		public void setAccountName(String accountName) {
			this.accountName = accountName;
		}
		public String getLabels() {
			return labels;
		}
		public void setLabels(String labels) {
			this.labels = labels;
		}
	    
	    
}
