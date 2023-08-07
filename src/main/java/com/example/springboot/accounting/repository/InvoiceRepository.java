package com.example.springboot.accounting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.springboot.accounting.model.entities.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Long>{
	
	List<Invoice> findAllInvoiceByRecipient(String recipient);

}
