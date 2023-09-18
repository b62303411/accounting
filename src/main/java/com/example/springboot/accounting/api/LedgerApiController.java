package com.example.springboot.accounting.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.accounting.service.GeneralLedgerService;

@RestController
@RequestMapping("/api/ledger")
public class LedgerApiController {
	

	@Autowired
	GeneralLedgerService generalLedgerService;
	
	@PostMapping("/populate")
	public void populateLedger() 
	{
		//
		generalLedgerService.populateLedger();
	}
	
	@PostMapping("/recalculate")
	public void recalculateLedger() 
	{
		//
		generalLedgerService.rePopulateLedger();
	}
}
