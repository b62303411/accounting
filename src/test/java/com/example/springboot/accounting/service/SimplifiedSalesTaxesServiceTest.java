package com.example.springboot.accounting.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.example.springboot.accounting.TestFixture;
import com.sam.accounting.model.Sequence;
import com.sam.accounting.model.entities.qb.Ledger;
import com.sam.accounting.model.entities.qb.Transaction;
import com.sam.accounting.service.CompanyProfileService;
import com.sam.accounting.service.FiscalYearService;
import com.sam.accounting.service.IncomeStatementWhiteBoardFactory;
import com.sam.accounting.service.SimplifiedSalesTaxesService;
import com.sam.accounting.service.SimplifiedSalesTaxesStrategy;

class SimplifiedSalesTaxesServiceTest {
	SimplifiedSalesTaxesService s = new SimplifiedSalesTaxesService();
	
	
	
	
	@Test
	void test() {
		TestFixture tf = new TestFixture();
		s.fys = new FiscalYearService();
		s.fys.profile = new CompanyProfileService();
	    Sequence seq =new Sequence();
	    
	    
		// Initial balances
		assertEquals(0, tf.getCheckingBalance());
		assertEquals(0, tf.getBalance("Bank Fees"));
		
		String checking_account_name = tf.getCheckingAccountName();
	    String credit_account_name = tf.getCreditAccountName();
		//  This paying the bank fees. s
		tf.ledger.addTransaction("5/31/2018", 
				"Frais Mens Plan", 
				"FRAIS MENS PLAN SERV", "19.00", "debit", "Bank Fee",
				checking_account_name, 0.0);

		// After bank fee is charged
		assertEquals(-19, tf.getCheckingBalance());
		assertEquals(19, tf.getBalance("Bank Fees"));

		// After bank fee is refunded
		tf.ledger.addTransaction("5/31/2018", "Red Solde", "Cpte	RED SOLDE CPTE", "19.00", "credit", "Income",
				checking_account_name, 0.0);

		assertEquals(0,tf.getCheckingBalance());
		assertEquals(0,tf.getBalance("Bank Fees"));

		tf.ledger.printAccounts();

		assertEquals(0, tf.getCreditBalance());
        String date = tf.getNextDate("5/28/2018");
        
		tf.ledger.addTransaction(date, credit_account_name, "PMT PREAUTOR VISA TD", "276.72", "debit",
				"Credit Card Payment", checking_account_name, 0.0);
		date = tf.getNextDate(date);
//		assertEquals(-276.72, tf.getCheckingBalance());
//		assertEquals(-276.72, tf.getCreditBalance());
//
		assertEquals(0, tf.getBalance("Owner's Draw"));
		tf.ledger.addTransaction(date, "View Cheque Chq", "View Cheque CHQ#00037-3000249272", "4000.00	", "debit",
				"Dividend & Cap Gains", checking_account_name, 0.0);
		assertEquals(-4000.00, tf.getBalance("Owner's Draw"));
		assertEquals(-4000.00, tf.getCheckingBalance());
//
		/// Ok so there we need to divide this is a total of 8 entry.
		// Accounts Receivable
		//
		// Type , Name ,Client , Debit , Credit.
		// Asset , Accounts Receivable,Incloud Solutio Fac, 5472.81, -
		// Revenue , Sales ,Incloud Solutio Fac, - , 5472.81
		date = tf.getNextDate(date);
		
		assertEquals(0, tf.getBalance("Accounts Receivable"), 0.001);
		assertEquals(0, tf.getBalance("Consulting Revenue"), 0.001);
		
		tf.ledger.addTransaction(date, "Incloud Solutio Fac", "INCLOUD SOLUTIO FAC", "5472.81	", "debit",
				"Income", checking_account_name, 0.0);
		tf.ledger.printLedger();
		assertEquals(1472.81, tf.getCheckingBalance(), 0.001);
		assertEquals(0.0, tf.getBalance("Accounts Receivable"), 0.001);
		assertEquals(4739.4407, tf.getBalance("Consulting Revenue"), 0.001);// Part that is revenue.
	
		Set<Transaction> transactions = tf.ledger.getTransactions();
		s.iswbf= new IncomeStatementWhiteBoardFactory();
		s.iswbf.accountManager=tf.accountManager;
		s.simplifiedSalesTaxesStrategy= new SimplifiedSalesTaxesStrategy();
		s.simplifiedSalesTaxesStrategy.accountManager=tf.accountManager;
		
		s.run(tf.ledger.getSeq(), transactions, tf.ledger);
		
		assertEquals(249.946, tf.getBalance("Quick Method Benefit"), 0.001);
//
//		// (Payment Received from Client A)
//		// Type , Name ,Client , Debit , Credit.
//		// Asset , Check ,Incloud Solutio Fac, 5472.81, -
//		// Revenue , Accounts Receivable,Incloud Solutio Fac, - , 5472.81
//		tf.ledger.addTransaction("5/14/2018", "Incloud Solutio Fac", "INCLOUD SOLUTIO FAC", "5472.81	", "debit",
//				"Income", "TD EVERY DAY A BUSINESS PLAN", 0.0);
//
//		tf.ledger.printLedger();
//
//		assertEquals(1196.09, tf.getCheckingBalance(), 0.001);
//		assertEquals(5472.81, tf.getBalance("Consulting Revenue"), 0.001);
//
////		addTransaction(ledger,accountManager, "4/30/2018", "Frais Mens Plan", "FRAIS MENS PLAN SERV", "19.00", "debit",
////				"Bank Fee", "TD EVERY DAY A BUSINESS PLAN");
//
//		// Post transactions
////		postTransaction(ledger, checking, bankFeeExpense, -19.00);
////		postTransaction(ledger, checking, income, 19.00);
////		postTransaction(ledger, checking, creditCardPayable, -276.72);
////		postTransaction(ledger, checking, dividendCapGains, -4000.00);
////		postTransaction(ledger, checking, income, 5472.81);
		
		
	}

}
