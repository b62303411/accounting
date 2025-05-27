package com.sam.accounting.api;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.sam.accounting.model.dto.LedgerEntryDTO;
import com.sam.accounting.service.GeneralLedgerService;

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
	
	
	@GetMapping("/stream")
    public SseEmitter streamLedgerDtos() {
        SseEmitter emitter = new SseEmitter();

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
        
            	List<LedgerEntryDTO> allEntries = generalLedgerService.getLedgerDtos();
            	int batchSize = 50; // You can adjust this value based on your needs
            	for (int i = 0; i < allEntries.size(); i += batchSize) {
            		 List<LedgerEntryDTO> batch = allEntries.subList(i, Math.min(i + batchSize, allEntries.size()));
            		  emitter.send(batch);
            		 Thread.sleep(50); // Optional: introduce a delay between sends
            	}
            	// Once all the ledger entries are sent, send an end-of-stream message
            	emitter.send(SseEmitter.event().name("message").data("{\"endOfStream\":true}"));                
                emitter.complete();
                
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }
}
