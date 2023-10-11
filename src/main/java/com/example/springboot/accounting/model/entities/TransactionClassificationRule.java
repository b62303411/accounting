package com.example.springboot.accounting.model.entities;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class TransactionClassificationRule {
	
	@Id
	public Long id;
	public List<String> keywords;
	public String creditedAccount;
	public String debitedAccount;
	public String vendor;
	public String transactionCathegory;
	

}
