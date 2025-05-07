package com.sam.accounting.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sam.accounting.model.SalesTaxAmounts;
import com.sam.accounting.model.Sequence;
import com.sam.accounting.model.entities.qb.Account;
import com.sam.accounting.model.entities.qb.AccountManager;
import com.sam.accounting.model.entities.qb.EntryType;
import com.sam.accounting.model.entities.qb.Transaction;
import com.sam.accounting.model.entities.qb.TransactionEntry;

@Service
public class SimplifiedSalesTaxesStrategy {
	private static final double GST_RATE = 0.036; // 3.6%
	private static final double QST_RATE = 0.066; // 6.6%

	private static final double GST_REDUCTION_LIMIT = 30000.0;
	private static final double QST_REDUCTION_LIMIT = 31421.0;
	private static final double REDUCTION_RATE = 0.01; // 1%

	@Autowired
	AccountManager accountManager;

	/**
	 * 
	 * @param taxableSales
	 * @return
	 */
	public SalesTaxAmounts calculateAmount(Double taxableSales) {
		double gstReductionRemaining = GST_REDUCTION_LIMIT;
		double qstReductionRemaining = QST_REDUCTION_LIMIT;

		double gstReduction = Math.min(taxableSales, GST_REDUCTION_LIMIT) * REDUCTION_RATE;
		double qstReduction = Math.min(taxableSales, QST_REDUCTION_LIMIT) * REDUCTION_RATE;
		SalesTaxAmounts t = new SalesTaxAmounts();
		t.gstAmount = taxableSales * GST_RATE - gstReduction;
		t.qstAmount = taxableSales * QST_RATE - qstReduction;
		return t;
	}

	public static double calculateGST(double taxableSales) {

		double tax = taxableSales * GST_RATE; // Calculate the total GST
		// Reduction on the first $30,000 of taxable sales
		// ($21,000 × 1%) // the given

		return tax;

	}

	public static double calculateQST(double taxableSales) {
		double tax = taxableSales * QST_RATE; // Calculate the total QST

		return tax;
	}

	public static double calculateGSTR(double taxableSales) {
		double gstReductionRemaining = GST_REDUCTION_LIMIT;
		double tax = taxableSales * GST_RATE; // Calculate the total GST
		// Reduction on the first $30,000 of taxable sales
		// ($21,000 × 1%) // the given
		double applicableReduction = Math.min(gstReductionRemaining, taxableSales) * REDUCTION_RATE; // Calculate the

		// Return the tax after applying the reduction
		double remained_value = tax - applicableReduction;

		return remained_value;

	}

	public static double calculateQSTR(double taxableSales) {
		double tax = taxableSales * QST_RATE; // Calculate the total QST
		double qstReductionRemaining = QST_REDUCTION_LIMIT;
		double applicableReduction = Math.min(qstReductionRemaining, taxableSales) * REDUCTION_RATE; // Calculate the
																										// reduction for
																										// // the give
																										// //
																										// taxable sales
		qstReductionRemaining -= applicableReduction / REDUCTION_RATE; // Update the remaining reduction

		double remained_value = tax - applicableReduction; // Return the tax after applying the reduction

		return remained_value;
	}

	/**
	 * | Account                          | Debit ($) | Credit ($) |
	 * |-----------------------------------------------------------| 
	 * | GST Payable                      | 136.50    |            | 
	 * | QST Payable                      | 308.13    |            |
	 * | Savings from Quick Method (Asset)| 303.12    |            | 
	 * | GST Collected                    |           |     250.00 | 
	 * | QST Collected                    |           |     498.75 |
	 * |-----------------------------------------------------------|
	 */
	/**
	 * 
	 * @param taxableSales
	 * @param collectedTaxes
	 * @param date
	 * @param fy
	 * @param seq 
	 * @return
	 */
	public Transaction recordQuickMethodRemittance(double taxableSales,double collectedTaxes, Date date, int fy, Sequence seq) {
		
		Account quickMethodBenefit = accountManager.getAccountByName("Quick Method Benefit");
		Account salesTaxPayable = accountManager.getAccountByName("Sales Tax Payable");
		Account stc = accountManager.getAccountByName("Sales Tax Collected");
		double gst = calculateGST(taxableSales);
		double qst = calculateQST(taxableSales);

		double salesTaxes = gst + qst;
		Transaction taxAdjustmentTransaction = new Transaction(seq);
		taxAdjustmentTransaction.setDate(date); // Assuming current date
		String description = "Quick Method Benefit:" + date;
		taxAdjustmentTransaction.setDescription(description);
		taxAdjustmentTransaction.setMessage(description);
		double benefit =collectedTaxes- salesTaxes;
		if(benefit < 0) 
		{
			System.err.println();
		}
		else 
		{
					
		

			// Create entries
			TransactionEntry creditEntry = new TransactionEntry();
			creditEntry.setAccount(salesTaxPayable);
			creditEntry.setType(EntryType.DEBIT);
			creditEntry.setAmount(benefit);
			String vendor_client = "Quebec Revenue Agency: QST , GST";
			creditEntry.setVendor_client(vendor_client);
			creditEntry.setDate(date);
		
			
			// Create entries
			TransactionEntry tcreditEntry = new TransactionEntry();
			tcreditEntry.setAccount(stc);
			tcreditEntry.setType(EntryType.DEBIT);
			tcreditEntry.setAmount(collectedTaxes);	
			tcreditEntry.setVendor_client("Self: QST , GST");
			tcreditEntry.setDate(date);
			
			
			TransactionEntry debitEntry = new TransactionEntry();
			debitEntry.setAccount(quickMethodBenefit);
			debitEntry.setType(EntryType.CREDIT);
			debitEntry.setAmount(benefit);
			debitEntry.setDate(date);
			debitEntry.setVendor_client(vendor_client);


			// Add entries to transaction
			taxAdjustmentTransaction.addEntry(creditEntry);
			taxAdjustmentTransaction.addEntry(debitEntry);
			taxAdjustmentTransaction.addEntry(tcreditEntry);
		}
	
		return taxAdjustmentTransaction;

	}
}
