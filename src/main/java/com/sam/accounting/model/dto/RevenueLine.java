package com.sam.accounting.model.dto;

import java.util.Date;

public class RevenueLine {
	private Double amount;
	private Double tpsTvq;
	private Double revenue;
	private String Description;
	private Date date;
	
	/**
	 * 
	 * @param amount
	 * @param tpsTvq
	 * @param revenue
	 * @param description
	 * @param date
	 */
	public RevenueLine(Double amount, Double tpsTvq, Double revenue, String description, Date date) {
		super();
		this.amount = amount;
		this.tpsTvq = tpsTvq;
		this.revenue = revenue;
		Description = description;
		this.date = date;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public Double getTpsTvq() {
		return tpsTvq;
	}
	public void setTpsTvq(Double tpsTvq) {
		this.tpsTvq = tpsTvq;
	}
	public Double getRevenue() {
		return revenue;
	}
	public void setRevenue(Double revenue) {
		this.revenue = revenue;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	

}
