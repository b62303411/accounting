package com.sam.accounting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sam.accounting.model.entities.Consolidation;

public interface ConsolidationRepository extends JpaRepository<Consolidation, Long>{

	Consolidation findByTransactionId(Long id);

	Consolidation findByInvoiceId(Long id);

	List<Consolidation> findAllBySupplier(String name);

	Consolidation findByEpenseId(Long id);

}
