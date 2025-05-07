package com.sam.accounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sam.accounting.model.entities.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier, Long>{

}
