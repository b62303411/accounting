package com.sam.accounting.model.dto;

import com.sam.accounting.model.ExploitationExpenseType;

public class BulkUpdateExpenseDTO {

	public String description;
	public ExploitationExpenseType newType;
	public String getDescription() {
		return description;
	}
	public ExploitationExpenseType getNewType() {
	
		return newType;
	}

}
