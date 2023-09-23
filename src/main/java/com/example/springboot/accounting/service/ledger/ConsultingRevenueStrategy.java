package com.example.springboot.accounting.service.ledger;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springboot.accounting.model.entities.qb.Account;
import com.example.springboot.accounting.model.entities.qb.AccountManager;
import com.example.springboot.accounting.model.entities.qb.TransactionAccount;
import com.example.springboot.accounting.service.TaxService;

@Service
public class ConsultingRevenueStrategy implements TransactionStrategy{
	
	@Autowired
	private AccountManager accountManager;
	@Autowired
	private TaxService taxService;
//	+------------+----------+------------------------+-----+--------------+-----------+-----------+
//	| Date       | Type     | Name                   | No  | Vendor/Client|  Debit    | Credit    |
//	+------------+----------+------------------------+-----+--------------+-----------+-----------+
//	| 04/01/2023 | ASSET    | Accounts Receivable    | 007 | Client XYZ   | $1,500.00 |   -       |
//	| 04/01/2023 | REVENUE  | Consulting Services    | 008 | Client XYZ   |    -      | $1,500.00 |
//	+------------+----------+------------------------+-----+--------------+-----------+-----------+
	/**
	 * 
	 * @param receivable
	 * @param amount
	 * @param qc_inc_invoice
	 */
	private void populateClientInvoice(Account receivable, double amount, TransactionAccount qc_inc_invoice) {
		// +------------+-----------+-------------------------+-----+--------------+----------+----------+
		// | Date | Type | Name | No | Vendor/Client| Debit | Credit |
		// +------------+-----------+-------------------------+-----+--------------+----------+----------+
		// | 01/06/2023 | ASSET | Accounts Receivable | 060 | Client XYZ | $5,000.00| -
		// |
		// | 01/06/2023 | REVENUE | Consulting Revenue | 061 | Client XYZ | - |
		// $5,000.00|
		// +------------+-----------+-------------------------+-----+--------------+----------+----------+
		TransactionAccount without_sales_tax_revenue = new TransactionAccount();
		TransactionAccount tax_collected = new TransactionAccount();
		double amount_without_tax = taxService.getBeforeTaxesValue(Math.abs(amount));
		double tax = amount - amount_without_tax;

		without_sales_tax_revenue.amount = amount_without_tax;
		without_sales_tax_revenue.setCredited(getAccountByName("Consulting Revenue"));
		without_sales_tax_revenue.debited = receivable;

		tax_collected.amount = tax;
		tax_collected.setCredited(getAccountByName("Sales Tax Payable"));
		tax_collected.setCredited(getAccountByName("Sales Tax Collected"));
		tax_collected.debited = receivable;

		qc_inc_invoice.split = new ArrayList<TransactionAccount>();
		qc_inc_invoice.split.add(tax_collected);
		qc_inc_invoice.split.add(without_sales_tax_revenue);

	}
	
	private Account getAccountByName(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	private TransactionAccount handleSalesRevenue(String message, String message_o, Account receivable, double amount,
			Account checkingAccount, TransactionAccount c) {

		TransactionAccount qc_inc_invoice = new TransactionAccount();
		c.vendor_client = getCorrespondingVendorOrClient(message, message_o);
		qc_inc_invoice.vendor_client = c.vendor_client;

		populateClientInvoice(receivable, amount, qc_inc_invoice);

//		+------------+-----------+-------------------------+-----+--------------+----------+----------+
//		| Date       | Type      | Name                    | No  | Vendor/Client|  Debit   | Credit   |
//		+------------+-----------+-------------------------+-----+--------------+----------+----------+
//		| 10/06/2023 | ASSET     | Checking Account        | 100 | Client XYZ   | $5,000.00|   -      |
//		| 10/06/2023 | ASSET     | Accounts Receivable     | 060 | Client XYZ   |   -      | $5,000.00|
//		+------------+-----------+-------------------------+-----+--------------+----------+----------+
		TransactionAccount invoice_payment = new TransactionAccount();
		invoice_payment.vendor_client = c.vendor_client;
		invoice_payment.amount = amount;
		invoice_payment.debited = checkingAccount;
		invoice_payment.setCredited(receivable);

		// c.from = checkingAccount;
		// c.to = receivable;
		// c.amount = -amount;
		c.split = new ArrayList<TransactionAccount>();
		c.split.addAll(qc_inc_invoice.split);
		c.split.add(invoice_payment);
		return c;
	}

	private String getCorrespondingVendorOrClient(String message, String message_o) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void populate(double amount, Account transactionAccount, TransactionAccount cardinality, String type,
			Double balence) {
		// TODO Auto-generated method stub
		
	}
}
