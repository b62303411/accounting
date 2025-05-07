package com.sam.accounting.model;

import java.util.Date;

public class NOLRecord {
	double value;
	String status; // Could be "AVAILABLE", "PARTIALLY_USED", or "FULLY_USED"
	public int year;
	public Date endOfYear;
	public NOLRecord(double value)
	{
		this(value,"AVAILABLE");
	}
	public NOLRecord(double value, String status) {
		this.value = value;
		this.status = status;
	}
	public double getValue() {
		return value;
	}
	protected String getStatus() {
		return status;
	}
	protected void setStatus(String status) {
		this.status = status;
	}
	public void setValue(double value) {
		this.value = value;
	}
}
