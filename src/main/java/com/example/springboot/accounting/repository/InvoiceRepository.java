package com.example.springboot.accounting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.springboot.accounting.model.entities.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Long>{
	
	List<Invoice> findAllInvoiceByRecipient(String recipient);
	
	@Query("SELECT i FROM Invoice i WHERE ABS(ABS(i.amount) - ABS(:amount)) <= :epsilon")
	List<Invoice> findByAmountWithinTolerance(@Param("amount") Double amount, @Param("epsilon") Double epsilon);


}
