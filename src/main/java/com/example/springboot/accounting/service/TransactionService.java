package com.example.springboot.accounting.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springboot.accounting.model.entities.KnownDescription;
import com.example.springboot.accounting.model.entities.Transaction;
import com.example.springboot.accounting.repository.KnownDescriptionRegistry;
import com.example.springboot.accounting.repository.TransactionRepository;



@Service
public class TransactionService {
	private final TransactionRepository transactionRepository;
	private final KnownDescriptionRegistry kdr;

	@Autowired
	public TransactionService(TransactionRepository transactionRepository, KnownDescriptionRegistry kdr) {
		super();
		this.transactionRepository = transactionRepository;
		this.kdr= kdr;
	}

	public Transaction save(Transaction transaction) {
		return transactionRepository.save(transaction);
		
	}

	public Transaction findById(Long id) {
		return transactionRepository.findById(id).get();
	}
	
	public List<KnownDescription> getKnownDescriptions()
	{
		return kdr.findAll();
	}
	
	public void addKnownDescrption(KnownDescription k) 
	{
		kdr.save(k);
	}

	public List<Transaction> findAllByDescription(String description) {
	
		return transactionRepository.findAllByDescription(description);
	}

	public void saveAll(List<Transaction> transactions) {
		transactionRepository.saveAll(transactions);
		
	}
	 
}
