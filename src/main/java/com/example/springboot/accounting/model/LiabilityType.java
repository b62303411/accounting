package com.example.springboot.accounting.model;

public enum LiabilityType {
	LOANS_PAYABLE("Loans Payable"), ACCOUNTS_PAYABLE("Accounts Payable"), TAXES_PAYABLE("Taxes Payable"),
	OTHER("Other"); // You can add more types as needed

	private final String displayName;

	LiabilityType(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
}
