package com.example.springboot.accounting.model.dto;

import com.example.springboot.accounting.model.ExploitationExpenseType;

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
