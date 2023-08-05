package com.example.springboot.accounting.model.dto;

public class TransactionRequest {

	public String suc;
	public String acc;
	public String date_from;
	public String date_to;
	public String year;
	public String DESCRIPTION;
	public String RETRAITS;
	public String DEPOTS;
	public String DATE;
	public String SOLDE;
	
	public TransactionRequest() {
		super();
	}
	/**
	 * 
	 * @param suc
	 * @param acc
	 * @param date_from
	 * @param date_to
	 * @param year
	 * @param dESCRIPTION
	 * @param rETRAITS
	 * @param dEPOTS
	 * @param dATE
	 * @param sOLDE
	 */
	public TransactionRequest(String suc, String acc, String date_from, String date_to, String year, String dESCRIPTION,
			String rETRAITS, String dEPOTS, String dATE, String sOLDE) {
		super();
		this.suc = suc;
		this.acc = acc;
		this.date_from = date_from;
		this.date_to = date_to;
		this.year = year;
		DESCRIPTION = dESCRIPTION;
		RETRAITS = rETRAITS;
		DEPOTS = dEPOTS;
		DATE = dATE;
		SOLDE = sOLDE;
	}
	public String getSuc() {
		return suc;
	}
	public void setSuc(String suc) {
		this.suc = suc;
	}
	public String getAcc() {
		return acc;
	}
	public void setAcc(String acc) {
		this.acc = acc;
	}
	public String getDate_from() {
		return date_from;
	}
	public void setDate_from(String date_from) {
		this.date_from = date_from;
	}
	public String getDate_to() {
		return date_to;
	}
	public void setDate_to(String date_to) {
		this.date_to = date_to;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getDESCRIPTION() {
		return DESCRIPTION;
	}
	public void setDESCRIPTION(String dESCRIPTION) {
		DESCRIPTION = dESCRIPTION;
	}
	public String getRETRAITS() {
		return RETRAITS;
	}
	public void setRETRAITS(String rETRAITS) {
		RETRAITS = rETRAITS;
	}
	public String getDEPOTS() {
		return DEPOTS;
	}
	public void setDEPOTS(String dEPOTS) {
		DEPOTS = dEPOTS;
	}
	public String getDATE() {
		return DATE;
	}
	public void setDATE(String dATE) {
		DATE = dATE;
	}
	public String getSOLDE() {
		return SOLDE;
	}
	public void setSOLDE(String sOLDE) {
		SOLDE = sOLDE;
	}
	
	
}
