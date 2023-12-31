package com.example.springboot.accounting.presentation;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.springboot.accounting.model.dto.AssetCreationRequest;
import com.example.springboot.accounting.model.entities.AmortisationLeg;
import com.example.springboot.accounting.model.entities.Asset;
import com.example.springboot.accounting.model.entities.Transaction;
import com.example.springboot.accounting.service.AssetService;
import com.example.springboot.accounting.service.TransactionService;

@Controller
@RequestMapping("/view/assets")
public class AssetController {
	private AssetService assetService;
	private TransactionService transactionService;

	@Autowired
	public AssetController(AssetService assetService, TransactionService transactionService) {
		this.assetService = assetService;
		this.transactionService = transactionService;
	}

	@GetMapping
	public String assets(Model model) {
		List<Asset> assets = assetService.findAll();
		double total=0;
		for (Asset asset : assets) {
			total+=asset.getCurrentValue();
		}
		model.addAttribute("total", total);
		model.addAttribute("assets", assets);
		model.addAttribute("year", 2023);
		return "assets";
	}

	@GetMapping("/create/{transactionId}")
	public String transactions(Model model, @PathVariable("transactionId") Long transactionId) {
		AssetCreationRequest request = new AssetCreationRequest();
		request.transactionId = transactionId;
		Transaction transaction = transactionService.findById(transactionId);
		model.addAttribute("transaction", transaction);
		model.addAttribute("request", request);
		model.addAttribute("year", 2023);

		return "assetCreation";
	}

	@GetMapping("/execute-leg/{legId}")
	public String executeLeg(Model model, @PathVariable("legId") Long legId) {
		AmortisationLeg leg = assetService.findLeg(legId);
		if (!leg.isRealized()) {
			Asset asset = leg.getAmortisation().getAsset();
			Double currentValue = asset.getCurrentValue();
			currentValue = currentValue - leg.getAmount(); // Amount of the depreciation.
			asset.setCurrentValue(currentValue);
			leg.setRealized(true);
			assetService.save(asset);
			assetService.save(leg);
		}
		else 
		{
			Logger.getGlobal().log(Level.FINE,"allready realized");

		}
		return "";
	}

}
