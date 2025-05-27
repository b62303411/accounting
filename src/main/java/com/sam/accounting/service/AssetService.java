package com.sam.accounting.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sam.accounting.model.dto.FinancialStatementLine;
import com.sam.accounting.model.entities.AmortisationLeg;
import com.sam.accounting.model.entities.Asset;
import com.sam.accounting.model.entities.Transaction;
import com.sam.accounting.repository.AmortisationLegRepository;
import com.sam.accounting.repository.AssetRepository;

@Service
public class AssetService {
	private final AssetRepository assetRepository;
	private final AmortisationLegRepository amortisationLegRepository;

	/**
	 * 
	 * @param amortisationLegRepository
	 * @param repo
	 */
	@Autowired
	public AssetService(AmortisationLegRepository amortisationLegRepository, AssetRepository repo) {
		assetRepository = repo;
		this.amortisationLegRepository=amortisationLegRepository;
	}

	public List<Asset> findAll() {

		return assetRepository.findAll();
	}

	public List<FinancialStatementLine> getAssetFinantialStatement() {
		List<FinancialStatementLine> lines = new ArrayList<FinancialStatementLine>();
		List<Asset> assets = assetRepository.findAll();
		for (Asset asset : assets) {
			FinancialStatementLineFactory f;

			FinancialStatementLine line = new FinancialStatementLine(asset.getType().name(), asset.getCurrentValue(),
					asset.getPurchaceTransaction().getDescription());
			lines.add(line);
		}
		return lines;
	}

	public AmortisationLeg findLeg(Long legId) {
		return amortisationLegRepository.findById(legId).get();
	}

	public void save(Asset asset) {
		assetRepository.save(asset);

	}

	public void save(AmortisationLeg leg) {
		amortisationLegRepository.save(leg);
	}

	public Asset findByTransaction(Transaction transaction) {
		return assetRepository.findByPurchaceTransaction(transaction);
		
	}

}
