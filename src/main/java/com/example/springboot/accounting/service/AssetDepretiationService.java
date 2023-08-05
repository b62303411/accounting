package com.example.springboot.accounting.service;

import org.springframework.stereotype.Service;

import com.example.springboot.accounting.model.entities.Amortisation;
import com.example.springboot.accounting.model.entities.Asset;

@Service
public class AssetDepretiationService {
	
	private CompanyProfileService companyProfileService;
	public AssetDepretiationService(CompanyProfileService companyProfileService) 
	{
		this.companyProfileService= companyProfileService;
	}
	
	public void generateDepretiation(Asset asset) {
	    companyProfileService.getProfile().getFiscalYearEnd().getFiscalYear(asset.getDateOfPurchace());
		int period = 5;
		AssetCompute computer = new AssetCompute(this, asset.getOriginalValue(), period, asset.getType());
		for (int i = 0; i < period; i++) {
			Amortisation ammortisation = new Amortisation();
			ammortisation.setAmount(computer.getCurrentBookValue());
			ammortisation.setAsset(asset);
			ammortisation.setFiscalYear(0);
			computer.depreciateAsset();
			System.out.println("Year " + (i + 1) + " book value: " + computer.getCurrentBookValue());
		}
	}
}
