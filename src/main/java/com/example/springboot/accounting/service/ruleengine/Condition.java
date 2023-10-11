package com.example.springboot.accounting.service.ruleengine;

import java.util.List;

public class Condition {
	private String account;
	private List<String> descriptionContains;
	protected String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	protected List<String> getDescriptionContains() {
		return descriptionContains;
	}
	public void setDescriptionContains(List<String> descriptionContains) {
		this.descriptionContains = descriptionContains;
	}
	
}
