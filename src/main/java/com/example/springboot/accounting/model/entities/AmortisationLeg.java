package com.example.springboot.accounting.model.entities;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class AmortisationLeg {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	boolean realized;
	private int fiscalYear;
	private Date date;
	private Double amount;
	//@JoinColumn(@name = "amortisation_id")
	@OneToOne
	private Amortisation amortisation;
	private double remainingValue;
	public Long getId() {
		return id;
	}	
	public void setId(Long id) {
		this.id = id;
	}
	public boolean isRealized() {
		return realized;
	}
	public void setRealized(boolean realized) {
		this.realized = realized;
	}
	public int getFiscalYear() {
		return fiscalYear;
	}
	public void setFiscalYear(int fiscalYear) {
		this.fiscalYear = fiscalYear;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public Amortisation getAmortisation() {
		return amortisation;
	}
	public void setAmortisation(Amortisation amortisation) {
		this.amortisation = amortisation;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public void setPredictedValue(double remainingValue) {
		this.remainingValue=remainingValue;
		
	}
	public double getRemainingValue() {
		return remainingValue;
	}
	public void setRemainingValue(double remainingValue) {
		this.remainingValue = remainingValue;
	}
	
	
	
}
