package com.sam.accounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sam.accounting.model.entities.Asset;
import com.sam.accounting.model.entities.Transaction;

public interface AssetRepository extends JpaRepository<Asset, Long>{


	Asset findByPurchaceTransaction(Transaction transaction);

}
