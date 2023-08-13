package com.example.springboot.accounting.model.dto;

import java.time.LocalDate;

public class InvoiceDto {

	private String description;
	private int noFacture;
	private Double amount;
	private Double tps;
	private Double tvq;
	private LocalDate date;
	private String recipient;

	public InvoiceDto() {
		super();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public Double getTps() {
		return tps;
	}

	public void setTps(Double tps) {
		this.tps = tps;
	}

	public Double getTvq() {
		return tvq;
	}

	public void setTvq(Double tvq) {
		this.tvq = tvq;
	}

	public int getNoFacture() {
		return noFacture;
	}

	public void setNoFacture(int noFacture) {
		this.noFacture = noFacture;
	}

}