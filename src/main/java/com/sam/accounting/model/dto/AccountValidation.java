package com.sam.accounting.model.dto;

import java.util.List;

public class AccountValidation {
	public int number;
	public List<ReportAvailable> availabilities;
	
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public List<ReportAvailable> getAvailabilities() {
		return availabilities;
	}
	public void setAvailabilities(List<ReportAvailable> availabilities) {
		this.availabilities = availabilities;
	}
	
	

}
