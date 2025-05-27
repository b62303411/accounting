package com.example.springboot.accounting.model.entities;

import com.example.springboot.accounting.model.FiscalYearEnd;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class CompanyProfile {
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	private String name;
	@Embedded
	private FiscalYearEnd fiscalYearEnd;
	
	
	public CompanyProfile() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * 
	 * @param id
	 * @param name
	 * @param fiscalYearEnd
	 */
	public CompanyProfile(Long id, String name, FiscalYearEnd fiscalYearEnd) {
		super();
		this.id = id;
		this.name = name;
		this.fiscalYearEnd = fiscalYearEnd;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public FiscalYearEnd getFiscalYearEnd() {
		return fiscalYearEnd;
	}
	public void setFiscalYearEnd(FiscalYearEnd fiscalYearEnd) {
		this.fiscalYearEnd = fiscalYearEnd;
	}

	

}
