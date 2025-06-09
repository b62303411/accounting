package com.sam.accounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sam.accounting.model.entities.Asset;
import com.sam.accounting.model.entities.Transaction;
@Repository
public interface AssetRepository extends JpaRepository<Asset, Long>{


	Asset findByPurchaceTransaction(Transaction transaction);

}
