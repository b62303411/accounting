package com.example.springboot.accounting.service;

import org.springframework.stereotype.Service;

import com.example.springboot.accounting.model.FiscalYearEnd;
import com.example.springboot.accounting.model.entities.CompanyProfile;

@Service
public class CompanyProfileService {
	private CompanyProfile profile;

	public CompanyProfileService()
	{
		this.profile = new CompanyProfile();
		this.profile.setName("9321-0474 QUEBEC INC");
		this.profile.setFiscalYearEnd(new FiscalYearEnd(31,java.time.Month.MARCH.name()));
	}

	public CompanyProfile getProfile() {
		return profile;
	}

	
}
