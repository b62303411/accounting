package com.sam.accounting.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sam.accounting.model.dto.AssetCreationRequest;
import com.sam.accounting.model.entities.Amortisation;
import com.sam.accounting.model.entities.Asset;
import com.sam.accounting.model.entities.Transaction;
import com.sam.accounting.repository.AssetRepository;
import com.sam.accounting.service.CompanyProfileService;
import com.sam.accounting.service.TransactionService;
@RestController
@RequestMapping("/api/assets")
public class AssetApiController {
	private AssetRepository assetRepo;
	private TransactionService transactionService;
	private CompanyProfileService service;
	
	@Autowired
	AssetApiController(CompanyProfileService service,AssetRepository assetRepo,TransactionService transactionService)
	{
		this.assetRepo = assetRepo;
		this.transactionService = transactionService;
		this.service=service;
	}
	@PostMapping("/create")
	public ResponseEntity<Asset> handleAssetCreation(@ModelAttribute AssetCreationRequest assetRequest) {
		Asset asset = new Asset();
		asset.setType(assetRequest.getAssetType());
		
		Amortisation amort = new Amortisation();
		asset.setAmortisation(amort);
		Transaction transaction = transactionService.findById(assetRequest.getTransactionId());
		asset.setPurchaceTransaction(transaction);
		asset.setCurrentValue(transaction.getAmount());// minus tps tvq;
		asset.setDateOfPurchace(transaction.getDate());
		asset.setOriginalValue(transaction.getAmount());
		amort.setAmount(transaction.getAmount());
		amort.setNumDepreciationLegs(assetRequest.numberOfLegs);
		//amort.setFiscalYear(service.getProfile().getFiscalYearEnd().)
		asset.createDepretiation(service.getProfile().getFiscalYearEnd());
		assetRepo.save(asset);
		ResponseEntity<Asset> response = ResponseEntity.ok(asset);
	 
	    return response;  // redirect to another page after handling the form submission
	}
	
	
}
