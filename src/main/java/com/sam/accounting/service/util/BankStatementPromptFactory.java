package com.sam.accounting.service.util;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sam.accounting.model.AiFileResult;
import com.sam.accounting.model.AssistantAnswer;
import com.sam.accounting.model.dto.InvoiceDto;
import com.sam.accounting.service.ai.PromptTemplateService;

import freemarker.template.TemplateException;
@Service
public class BankStatementPromptFactory {
	
	@Autowired
	public OpenAiRequestService service;
	@Autowired
	public PromptTemplateService promptTemplate;
	
	public AiFileResult submitBankStatementQuery(String fileContent, String extraContext) {

		AiFileResult result = new AiFileResult();
		ObjectMapper objectMapper = new ObjectMapper();
		InvoiceDto invoiceTemplate = new InvoiceDto();

		String invoiceJsonTemplate;
		try {
			
			String  prompt= promptTemplate.renderBankStatementPrompt(fileContent);
			

					
			AssistantAnswer answer =service.reallyRun(prompt,null);
			
			result.firstMetaData=answer;
			
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TemplateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
}
