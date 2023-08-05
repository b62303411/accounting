package com.example.springboot.accounting.service;

import com.example.springboot.accounting.model.AssetType;

class AssetCompute {
	/**
	 * 
	 */
	private final AssetDepretiationService AssetCompute;
	private Double purchasePrice;
	private int lifespanInYears;
	private Double annualDepreciation;
	private Double currentBookValue;
	private AssetType assetType;

	/**
	 * 
	 * @param purchasePrice
	 * @param lifespanInYears
	 * @param assetType
	 * @param assetDepretiationService TODO
	 */
	public AssetCompute(AssetDepretiationService assetDepretiationService, Double purchasePrice, int lifespanInYears, AssetType assetType) {
		AssetCompute = assetDepretiationService;
		this.purchasePrice = purchasePrice;
		this.lifespanInYears = lifespanInYears;
		this.assetType = assetType;
		double ccaRate = assetType.getCcaRate() / 100; // convert percentage to a decimal
		this.annualDepreciation = purchasePrice * (Double.valueOf(ccaRate));
		this.currentBookValue = purchasePrice;
	}

	public void depreciateAsset() {
		currentBookValue = currentBookValue - (annualDepreciation);
	}

	public Double getCurrentBookValue() {
		return currentBookValue;
	}

	public Double getAnnualDepreciation() {
		return annualDepreciation;
	}

}