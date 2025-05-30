package com.sam.accounting.model;

/**
 * Class 50 (55%) Include in Class 50 with a CCA rate of 55% property acquired
 * after March 18, 2007, that is general-purpose electronic data processing
 * equipment and systems software for that equipment, including ancillary data
 * processing equipment. Do not include property that is included in Class 29 or
 * Class 52 or that is mainly or is used mainly as:
 * 
 * electronic process control or monitor equipment electronic communications
 * control equipment systems software for equipment referred to in 1. or 2. data
 * handling equipment (other than data handling equipment that is ancillary to
 * general-purpose electronic data processing equipment)
 * https://www.canada.ca/en/revenue-agency/services/tax/businesses/topics/sole-proprietorships-partnerships/report-business-income-expenses/claiming-capital-cost-allowance/classes-depreciable-property.html
 */
public enum AssetType {
	ELECTRONICS(50, 55), VEHICLES(10, 30), FURNITURE(8, 20), BUILDINGS(1, 4);

	private final int classNumber;
	private final double ccaRate;

	AssetType(int classNumber, double ccaRate) {
		this.classNumber = classNumber;
		this.ccaRate = ccaRate;
	}

	public int getClassNumber() {
		return this.classNumber;
	}

	public double getCcaRate() {
		return this.ccaRate;
	}
}
