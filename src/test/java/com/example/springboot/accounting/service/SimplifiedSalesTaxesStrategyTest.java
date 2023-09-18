package com.example.springboot.accounting.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SimplifiedSalesTaxesStrategyTest {

	@BeforeEach
	public void setup() {
		//TaxCalculator.resetReductions();
	}

	/**
	 * A cleaning business used the Quick Method last year. 
	 * It files quarterly returns. 
	 * Since its total annual taxable sales worldwide were not over $400,000 (GST included) and $418,952 (QST included) last year, 
	 * the business can continue to use the Quick Method this year. 
	 * The applicable rates are 3.6% for GST purposes and 6.6% for QST purposes.
	 */
	@Test
	public void testCalculateGSTFirstQuarter() {
		//$21,000.00
		double gstRemittance = SimplifiedSalesTaxesStrategy.calculateGST(21000);
		assertEquals(546.00, gstRemittance, 0.01);
		//assertEquals(9000, SimplifiedSalesTaxesStrategy.getGstReductionRemaining(), 0.01);
	}

	@Test
	public void testCalculateQSTFirstQuarter() {
		double qstRemittance = SimplifiedSalesTaxesStrategy.calculateQST(21995);
		//1,231.72
		assertEquals(1231.72, qstRemittance, 0.01);
		//assertEquals(9426, TaxCalculator.getQstReductionRemaining(), 0.01);
	}

	@Test
	public void testCalculateGSTSecondQuarter() {
		//SimplifiedSalesTaxesStrategy.calculateGST(15000); // First Quarter
		double gstRemittance = SimplifiedSalesTaxesStrategy.calculateGST(15000);
		assertEquals(450.00, gstRemittance, 0.01);
		//assertEquals(0, SimplifiedSalesTaxesStrategy.getGstReductionRemaining(), 0.01);
	}

	@Test
	public void testCalculateQSTSecondQuarter() {
		//SimplifiedSalesTaxesStrategy.calculateQST(21995); // First Quarter
		double qstRemittance = SimplifiedSalesTaxesStrategy.calculateQST(15710.71);
		assertEquals(942.65, qstRemittance, 0.01);
		//assertEquals(715.29, SimplifiedSalesTaxesStrategy.getQstReductionRemaining(), 0.01);
	}
}
