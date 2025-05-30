package com.sam.accounting.model.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Amortisation {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private int fiscalYear;
	private Double amount;
	@OneToOne(mappedBy = "amortisation")
	private Asset asset;
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "amortisation")
	private List<AmortisationLeg> legs;

	private int numDepreciationLegs;

	public Amortisation() {
		legs = new ArrayList<AmortisationLeg>();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Asset getAsset() {
		return asset;
	}

	public void setAsset(Asset asset) {
		this.asset = asset;
	}

	public int getNumDepreciationLegs() {
		return numDepreciationLegs;
	}

	public List<AmortisationLeg> getDepreciationLegs() {
		return legs;
	}

	public void addLeg(AmortisationLeg leg) {
		legs.add(leg);

	}

	public List<AmortisationLeg> getLegs() {
		return legs;
	}

	public void setLegs(List<AmortisationLeg> legs) {
		this.legs = legs;
	}

	public void setNumDepreciationLegs(int numDepreciationLegs) {
		this.numDepreciationLegs = numDepreciationLegs;
	}

}
