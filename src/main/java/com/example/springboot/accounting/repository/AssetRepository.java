package com.example.springboot.accounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.springboot.accounting.model.entities.Asset;
import com.example.springboot.accounting.model.entities.Transaction;

public interface AssetRepository extends JpaRepository<Asset, Long>{


	Asset findByPurchaceTransaction(Transaction transaction);

}
