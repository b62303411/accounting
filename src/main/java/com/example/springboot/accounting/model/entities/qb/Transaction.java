package com.example.springboot.accounting.model.entities.qb;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.example.springboot.accounting.model.TransactionNature;

public class Transaction {
	
	
	private LocalDate date;
	private String description;
	private double amount;
	private TransactionNature nature;
	private String id;
	private TransactionStatus status;  // Enum: Unposted, Posted
	public Transaction()
	{
		this.id = UUID.randomUUID().toString();
		this.status = TransactionStatus.UNPOSTED;
	}
	
	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public TransactionNature getNature() {
		return nature;
	}

	public void setNature(TransactionNature nature) {
		this.nature = nature;
	}

	private List<TransactionEntry> entries = new ArrayList<>();
	
	private String message;

	public void addEntry(TransactionEntry entry) {
		entries.add(entry);
	}

	public void post() {
		for (TransactionEntry entry : entries) {
			entry.post();
		}
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public TransactionStatus getStatus() {
		return status;
	}

	public void setStatus(TransactionStatus status) {
		this.status = status;
	}

	public List<TransactionEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<TransactionEntry> entries) {
		this.entries = entries;
	}

	 @Override
	    public String toString() {
	     StringBuilder str = new StringBuilder(); 
	     str.append(String.format("%-10s\t%-10s\t%-25s\t%s\t%-20s\t%-5s\t%-5s", 
		 			"Date",
		 			"Type", 
		 			"Name" , 
		 			"No", 
		 			"Vendor/Client", 
		            "Debit", 
		            "Credit")).append("\n");
		 for (TransactionEntry transactionEntry : entries) {
			str.append(transactionEntry.toString()).append("\n");
		}
		 return str.toString();
	    }

	public void setMessage(String message) {
		this.description=message;
		
	}

	@Override
	public int hashCode() {
	    int result = 17; // arbitrary prime number
	    for (TransactionEntry obj : entries) {
	        result = 31 * result + (obj == null ? 0 : obj.hashCode());
	    }
	    return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transaction other = (Transaction) obj;
		return Objects.equals(entries, other.entries);
	}


	
	
}
