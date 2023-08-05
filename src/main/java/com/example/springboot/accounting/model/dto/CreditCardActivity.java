package com.example.springboot.accounting.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreditCardActivity {
	 
    @JsonProperty("DATE_ACTIVITE")
    private String dateActivite;

    @JsonProperty("DATE_OPERATION")
    private String dateOperation;

    @JsonProperty("DESCRIPTION")
    private String description;

    @JsonProperty("MONTANT")
    private String montant;

    @JsonProperty("year")
    private String year;
    
    private String account_name;
    
    private String date_report;
    
    private String acc;

	public String getDateActivite() {
		return dateActivite;
	}

	public String getDateOperation() {
		return dateOperation;
	}

	public String getDescription() {
		return description;
	}

	public String getMontant() {
		return montant;
	}

	public String getYear() {
		return year;
	}

	public String getAccount_name() {
		return account_name;
	}

	public void setAccount_name(String account_name) {
		this.account_name = account_name;
	}

	public String getDate_report() {
		return date_report;
	}

	public void setDate_report(String date_report) {
		this.date_report = date_report;
	}

	public String getAcc() {
		return acc;
	}

	public void setAcc(String acc) {
		this.acc = acc;
	}

	public void setDateActivite(String dateActivite) {
		this.dateActivite = dateActivite;
	}

	public void setDateOperation(String dateOperation) {
		this.dateOperation = dateOperation;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setMontant(String montant) {
		this.montant = montant;
	}

	public void setYear(String year) {
		this.year = year;
	}
    
    
}
