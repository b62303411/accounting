package com.example.springboot.accounting.service.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springboot.accounting.model.AiFileResult;
import com.example.springboot.accounting.model.AssistantAnswer;
import com.example.springboot.accounting.model.dto.InvoiceDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class InvoicePromptFactory {
	
	@Autowired
	public OpenAiRequestService service;
	
	public AiFileResult submitInvoiceQuery(String fileContent, String extraContext) {

		AiFileResult result = new AiFileResult();
		ObjectMapper objectMapper = new ObjectMapper();
		InvoiceDto invoiceTemplate = new InvoiceDto();
		String invoiceJsonTemplate;
		try {
			invoiceJsonTemplate = objectMapper.writeValueAsString(invoiceTemplate);

			String message1 = "I have a string representing an invoice with the following content: " + fileContent
					+ " Can you please extract the relevant information and present it in aJSON format?";
			
					
			AssistantAnswer answer =service.reallyRun(message1,null);
			
			result.firstMetaData=answer;
			
			
			String message2 = "I have a string representing an invoice with the following content: " + answer.answer.toPrettyString()
					+ " Can you please extract the relevant information and present it in the following JSON format?:\n"
					+ invoiceJsonTemplate+ " Also make sure the amounts are stripped of any dolar sign they will be parsed as double";
			
			AssistantAnswer answer2 =service.reallyRun(message2,null);
			//message2 += "/n Take as context that my name is Samuel Audet-Arsenault, and im doing my accounting for my incorporation named:9321-0474 Québec Inc. Also recipient and origin canot be the same";
			// prompt+="/n Please make sure the Description is not empty";

//			String message2 = "My supplier list:[{\r\n" + "  \"id\": 123,\r\n"
//					+ "  \"name\": \"Cloutier & Longtin\",\r\n" + "  \"address\": \"450, rue du Parc suite 113\r\n"
//					+ "Saint-Eustache, Québec J7R 7G6\",\r\n" + "  \"contactEmail\": \"\",\r\n"
//					+ "  \"contactPhone\": \"\"\r\n" + "}]";

			//List<String> messages = new ArrayList();
			result.finalResult=answer2;
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
}
