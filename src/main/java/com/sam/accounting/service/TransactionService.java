package com.sam.accounting.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sam.accounting.model.CollisionSet;
import com.sam.accounting.model.entities.BankReccord;
import com.sam.accounting.model.entities.Expense;
import com.sam.accounting.model.entities.KnownDescription;
import com.sam.accounting.model.entities.Transaction;
import com.sam.accounting.repository.KnownDescriptionRegistry;
import com.sam.accounting.repository.TransactionRepository;



@Service
public class TransactionService {
	private final TransactionRepository transactionRepository;
	private final BankReccordService  brr;
	private final KnownDescriptionRegistry kdr;
	private final ExpensesService es;

	@Autowired
	public TransactionService(ExpensesService es,BankReccordService brr,TransactionRepository transactionRepository, KnownDescriptionRegistry kdr) {
		super();
		this.transactionRepository = transactionRepository;
		this.kdr= kdr;
		this.brr = brr;
		this.es = es;
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

	public List<Transaction> findAllNullPayee() {
		return transactionRepository.findByPayeeIsNull();
	}

	public List<Transaction> findAllByPayee(String name) {
		return transactionRepository.findAllByPayee(name);
		
	}

	public List<Transaction> findAllByDateAndAmount(Date date_transaction, Double sold) {
		return transactionRepository.findAllByDateAndAmount(date_transaction,sold);
		
	}

	public  List<Transaction> findAll() {
		return transactionRepository.findAll();
		
	}
	
	public void fixDuplicate() 
	{
		HashMap<CollisionSet,CollisionSet> set = new HashMap();
		
		List<Transaction> transactions = findAll();
		for (Transaction transaction : transactions) {
			CollisionSet cset = new CollisionSet(transaction);
			if(set.containsKey(cset))
			{
				cset = set.get(cset);
			}
			else 
			{
				set.put(cset, cset);
			}
			cset.collitions.add(transaction);
			if(transaction.getSolde()==null) 
			{
				BankReccord br = brr.findByTransaction(transaction);
				if(br !=null)
				{
					transaction.setSolde(br.getSolde());
					//transaction.setAmount(Math.abs(transaction.getAmount()));
					save(transaction);
				}
			}
		}
		
		for (CollisionSet cs : set.values()) {
			int duplicate_count = cs.collitions.size();
			if(duplicate_count >=2) 
			{
				System.out.println();
				Transaction[] array= new Transaction[duplicate_count];
				cs.collitions.toArray(array);
				Transaction keep = array[0];
				int keep_i = 0;
				for(int i = 0; i< duplicate_count;i++) 
				{
				    Expense e= 	es.findByTransaction(array[i]);
					if(!array[i].getAttachments().isEmpty() || e != null) 
					{
						 keep_i=i;
					}
				}
				if(keep_i!=0) 
				{
					keep = array[keep_i];
					delete(array[0]);
					System.err.println();
				}
				else 
				{
					for(int i = 1; i< duplicate_count;i++) 
					{
						Transaction to_be_deleted = array[i];
						
						delete(to_be_deleted);
					}
				}
			
				keep.setAmount(Math.abs(keep.getAmount()));
				save(keep);
			}
			
			
		}
	}

	public void delete(Transaction to_be_deleted) {
		try {
		BankReccord br = this.brr.findByTransaction(to_be_deleted);
		if(null != br)
			brr.delete(br);
		br = this.brr.findByTransaction(to_be_deleted);
		if(null != br) 
		{
			brr.delete(br);
			System.err.println();
		}
		if(to_be_deleted.getAttachments().size()>0) 
		{
			System.err.println();
		}
		this.transactionRepository.delete(to_be_deleted);
		}catch(Exception e) 
		{
			System.err.println(e.getLocalizedMessage());
		}
	}
	 
}
