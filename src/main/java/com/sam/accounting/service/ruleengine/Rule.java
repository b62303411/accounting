package com.sam.accounting.service.ruleengine;

import java.util.List;

public class Rule {
	private String ruleName;
	private List<Condition> conditions;
	private Classification classification;
	private String vendor;

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	protected List<Condition> getConditions() {
		return conditions;
	}

	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions;
	}

	public Classification getClassification() {
		return classification;
	}

	public void setClassification(Classification classification) {
		this.classification = classification;
	}

	public boolean evaluate(String message, String message_o, String type, String category, String account) {

		for (Condition condition : conditions) {
			if (condition.getAccount() != null) {
				if (!account.equals(condition.getAccount())) {
					return false;
				}
			}
			boolean keyword_found = false;
			if (condition.getDescriptionContains() != null) {
				for (String keywords : condition.getDescriptionContains()) {
					if (message_o != null) {
						if (message_o.contains(keywords)) {

							return true;
						}
					}
					if (message.contains(keywords)) {
						return true;
					}
				}
			}

		}
		return false;
	}

	public String getVendor() {
	
		return vendor;
	}


}
