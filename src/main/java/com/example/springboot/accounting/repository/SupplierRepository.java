package com.example.springboot.accounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.springboot.accounting.model.entities.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier, Long>{

}
