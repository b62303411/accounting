package com.example.springboot.accounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.springboot.accounting.model.entities.BankReccord;

public interface BankReccordRepository extends JpaRepository<BankReccord, Long>{

}
