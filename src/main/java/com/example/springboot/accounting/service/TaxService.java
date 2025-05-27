package com.example.springboot.accounting.service;

import org.springframework.stereotype.Service;

import com.example.springboot.accounting.model.SalesTaxeRates;
import com.example.springboot.accounting.model.entities.ExploitationExpense;

@Service
public class TaxService {
	/**
	 * 
	 * @param ex
	 * @param total
	 */
	public void invertTpsTvq(ExploitationExpense ex, double total) {
		double TPS_RATE = SalesTaxeRates.TPS_RATE;
		double TVQ_RATE = SalesTaxeRates.TVQ_RATE;
		double beforeTaxes = total / (1 + TPS_RATE + TPS_RATE * TVQ_RATE + TVQ_RATE);
		// Calculate TPS and TVQ
		double tps = beforeTaxes * TPS_RATE;
		double tvq = (beforeTaxes + tps) * TVQ_RATE;
		ex.setTps(tps);
		ex.setTvq(tvq);
		ex.setTotalBeforeSalesTaxes(beforeTaxes);
	}
	
	/**
	 * 
	 * @param total
	 * @return
	 */
	public double getBeforeTaxesValue(double total) 
	{
		double TPS_RATE = SalesTaxeRates.TPS_RATE;
		double TVQ_RATE = SalesTaxeRates.TVQ_RATE;
		double beforeTaxes = total / (1 + TPS_RATE + TPS_RATE * TVQ_RATE + TVQ_RATE);
		return beforeTaxes;
	}
	/**
	 * 
	 * @param changed
	 * @param exploitationExpense
	 * @return
	 */
	public boolean fixTpsTvq(boolean changed, ExploitationExpense exploitationExpense) {
		if (exploitationExpense.getTps() != null) {
			Double value = null;
			exploitationExpense.setTps(value);
			exploitationExpense.setTvq(value);
			exploitationExpense.setTotalBeforeSalesTaxes(exploitationExpense.getTransaction().getAmount());

			changed = true;
		}
		return changed;
	}
}
