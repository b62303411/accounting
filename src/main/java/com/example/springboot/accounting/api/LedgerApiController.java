package com.example.springboot.accounting.api;

import java.util.List;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.springboot.accounting.model.dto.LedgerEntryDTO;
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
	
	
	@GetMapping("/ledger/stream")
    public SseEmitter streamLedgerDtos() {
        SseEmitter emitter = new SseEmitter();

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
            	
            	List<LedgerEntryDTO> cashedLedger = generalLedgerService.getLedgerDtos();
              
                
                for (LedgerEntryDTO entry : cashedLedger) {
                    emitter.send(entry);
                    Thread.sleep(100); // Optional: introduce a delay between sends
                }
                
                emitter.complete();
                
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }
}
