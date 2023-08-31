package com.example.springboot.accounting.model.entities.qb;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class TransactionEntry {

	private Account account;
	private double amount;
	private Double actualBalence;
	private EntryType type; // Debit or Credit
	private Double balance;
	private String vendor_client;
	private Date date;
	private UUID id;

	/**
	 * 
	 * @param account
	 * @param vendor_client
	 * @param date
	 * @param amount
	 * @param type
	 */
	public TransactionEntry(Account account, String vendor_client, Date date, double amount, EntryType type) {
		this();
		this.account = account;
		this.amount = Math.abs(amount);
		this.type = type;
		this.date = date;
		this.vendor_client = vendor_client;
	}

	public TransactionEntry(TransactionEntry entry, Account account) {
		this();
		this.account = account;
		this.actualBalence = entry.getActualBalence();
		this.balance = account.getBalance();
		this.vendor_client = entry.getVendor_client();
		this.date = entry.getDate();
		this.amount = entry.getAmount();
		this.type = entry.getType();
		this.id=entry.id;

	}

	public TransactionEntry() {
		this.id = UUID.randomUUID();
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public double post() {
		if (!account.hasExecuted(id)) {
			if (EntryType.DEBIT.equals(type)) {
				account.debit(amount, id);
			} else if (EntryType.CREDIT.equals(type)) {
				account.credit(amount, id);
			}
		} else {
			System.err.println(id);
		}
		balance = account.getBalance();
		return account.getBalance();
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setType(EntryType type) {
		this.type = type;
	}

	public void setVendor_client(String vendor_client) {
		this.vendor_client = vendor_client;
	}

	public void setAccount(Account acc) {
		this.account = acc;

	}

	public void setAmount(double amount) {
		this.amount = amount;

	}

	@Override
	public String toString() {
		double credit = 0;
		double debit = 0;
		// Double credit = " ";
		// Double debit = " ";
		switch (type) {
		case CREDIT:
			credit = amount;
			break;
		case DEBIT:
			debit = amount;

		}

		return String.format("%-10s\t%-10s\t%-25s\t%s\t%-20s\t%.2f\t%.2f", date, account.getAccountType().name(),
				account.getName(), account.getAccountNumber(), vendor_client, debit, credit);

//	        return account.getAccountType() + "\t ," + 
//	               account.getName() + "\t ," + 
//	               account.getAccountNumber() + "\t ," + 
//	               vendor_client + "\t ," + 
//	               debit + "\t ," + 
//	               credit;
	}

	public EntryType getType() {

		return type;
	}

	public Double getAmount() {
		return amount;
	}

	public String getVendor_client() {

		return vendor_client;
	}

	public Account getAccount() {

		return account;
	}

	@Override
	public int hashCode() {
		return Objects.hash(account.getAccountNumber(), amount, date, type, vendor_client);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransactionEntry other = (TransactionEntry) obj;
		return Objects.equals(account.getAccountNumber(), other.account.getAccountNumber())
				&& Double.doubleToLongBits(amount) == Double.doubleToLongBits(other.amount)
				&& Objects.equals(date, other.date) && type == other.type
				&& Objects.equals(vendor_client, other.vendor_client);
	}

	public Double getActualBalence() {
		return actualBalence;
	}

	public void setActualBalence(Double actualBalence) {
		this.actualBalence = actualBalence;
	}

	public Double getBalance() {
		return balance;
	}

//	public void setBalance(Double balance) {
//		this.balance = balance;
//	}

}
