package com.example.springboot.accounting.model.entities;

import java.util.Date;
import java.util.List;

import com.example.springboot.accounting.model.AssetType;
import com.example.springboot.accounting.model.FiscalYearEnd;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
@Entity
public class Asset {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private Double originalValue;
	private Double currentValue;
	@Enumerated(EnumType.STRING)
	private AssetType type;
	private Date dateOfPurchace;
	@OneToOne
	private Transaction purchaceTransaction;
	@OneToOne(cascade = CascadeType.ALL)
	private Amortisation amortisation;
	@OneToOne
	private Invoice invoice;
	
	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public int getNumDepreciationLegs() 
	{
		return amortisation.getNumDepreciationLegs();
	}
	
	public List<AmortisationLeg> getDepreciationLegs()
	{
		return amortisation.getDepreciationLegs();
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Double getOriginalValue() {
		return originalValue;
	}
	public void setOriginalValue(Double originalValue) {
		this.originalValue = originalValue;
	}
	public Double getCurrentValue() {
		return currentValue;
	}
	public void setCurrentValue(Double currentValue) {
		this.currentValue = currentValue;
	}
	public AssetType getType() {
		return type;
	}
	public void setType(AssetType type) {
		this.type = type;
	}
	public Amortisation getAmortisation() {
		return amortisation;
	}
	public void setAmortisation(Amortisation amortisation) {
		this.amortisation = amortisation;
	}
	public Date getDateOfPurchace() {
		return dateOfPurchace;
	}
	public void setDateOfPurchace(Date dateOfPurchace) {
		this.dateOfPurchace = dateOfPurchace;
	}
	public Transaction getPurchaceTransaction() {
		return purchaceTransaction;
	}
	public void setPurchaceTransaction(Transaction purchaceTransaction) {
		this.purchaceTransaction = purchaceTransaction;
	}

	/**
	 * In this method, we first check if the Asset type is set, because we can't calculate depreciation without it. Then we calculate the depreciation amount for each year and create an AmortisationLeg for it. The AmortisationLeg is then added to the depreciationLegs list in the Asset class.

Note: Replace this.value with the initial value of your asset. It assumes there's a field value in your Asset class that holds the initial value of the asset.

Also, this example creates straight-line depreciation legs, where the depreciation amount is the same every year. If you need another type of depreciation (like declining balance), you'll need to modify the calculation of the depreciationAmount.
	 * @param service 

	 */
	public void createDepretiation(FiscalYearEnd fye) {
		 double rate = this.type.getCcaRate() / 100.0;
	     double remainingValue = this.originalValue;
	     int fiscalYearStart=fye.getFiscalYear(dateOfPurchace);
	for (int i = 0; i < amortisation.getNumDepreciationLegs(); i++) {
        double depreciationAmount = remainingValue * rate;
        remainingValue -= depreciationAmount;

        AmortisationLeg leg = new AmortisationLeg();
        leg.setFiscalYear(fiscalYearStart + i);
        leg.setAmount(depreciationAmount);
        leg.setPredictedValue(remainingValue);
        //leg.setAmortisation(amortisation);

        this.amortisation.addLeg(leg);
    }
	
	
}
	
	
	

}
