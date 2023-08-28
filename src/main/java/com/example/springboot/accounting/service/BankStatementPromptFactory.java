package com.example.springboot.accounting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springboot.accounting.model.AiFileResult;
import com.example.springboot.accounting.model.AssistantAnswer;
import com.example.springboot.accounting.model.dto.InvoiceDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
@Service
public class BankStatementPromptFactory {
	
	@Autowired
	public OpenAiRequestService service;
	
	public AiFileResult submitBankStatementQuery(String fileContent, String extraContext) {

		AiFileResult result = new AiFileResult();
		ObjectMapper objectMapper = new ObjectMapper();
		InvoiceDto invoiceTemplate = new InvoiceDto();
		String invoiceJsonTemplate;
		try {
			invoiceJsonTemplate = objectMapper.writeValueAsString(invoiceTemplate);

			String message1 = "I have a string exracted from a pdf representing an bank statement  the bank statement contains a table with the follwoing header {Description,Retraits,Depots,Date,Solde} ,the date follow a format like the following examples {31SEP, 31AOU} 31 Septembre, 31 Aout etc.. ,i would expect a list of row in the data for each entry in the bank statement:[  "+fileContent+ "   ],  Can you please extract the relevant information and present it in aJSON format? please make sure you answer is only the json value as i will use dirrectly your answer to be parsed.";
			
			 String prompt =fileContent+"Given the bank statement information provided, please extract and format the data into the following JSON structure:\r\n"
			 		+ "\r\n"
			 		+ "{\r\n"
			 		+ "   \"AccountInfo\": {\r\n"
			 		+ "      \"AccountType\": \"COMPTE CHEQUES D'ENTREPRISES-$CA AFFAIRES DE BASE\",\r\n"
			 		+ "      \"AccountNumber\": \"0511 0511-5235425\",\r\n"
			 		+ "      \"StatementPeriod\": \"DD MMM YY - DD MMM YY\",\r\n"
			 		+ "      \"NextStatementDate\": \"DD MMM YY\",\r\n"
			 		+ "      \"CreditBalance\": \"265,80 $\",\r\n"
			 		+ "      \"MinMonthlyBalance\": \"253,73 $\"\r\n"
			 		+ "   },\r\n"
			 		+ "   \"Transactions\": [\r\n"
			 		+ "      {\r\n"
			 		+ "         \"Description\": \"SOLDE REPORTE 29JUL 267,70\",\r\n"
			 		+ "         \"Retraits\": \"267,70\",\r\n"
			 		+ "         \"Depots\": \" \",\r\n"
			 		+ "         \"Date\": \"29JUL\",\r\n"
			 		+ "         \"Solde\": \"267,70\"\r\n"
			 		+ "      },\r\n"
			 		+ "      {\r\n"
			 		+ "         \"Description\": \"PMT PREAUTOR VISA TD 8,97 26AOU 258,73\",\r\n"
			 		+ "         \"Retraits\": \"8,97\",\r\n"
			 		+ "         \"Depots\": \" \",\r\n"
			 		+ "         \"Date\": \"26AOU\",\r\n"
			 		+ "         \"Solde\": \"258,73\"\r\n"
			 		+ "      },\r\n"
			 		+ "      {\r\n"
			 		+ "         \"Description\": \"FRAIS MENS PLAN SERV 5,00 31AOU 253,73\",\r\n"
			 		+ "         \"Amount\": \"5,00\",\r\n"
			 		+ "         \"Depots\": \" \",\r\n"
			 		+ "         \"Date\": \"31AOU\",\r\n"
			 		+ "         \"Solde\": \"253,73\"\r\n"
			 		+ "      }\r\n"
			 		+ "   ]\r\n"
			 		+ "}\r\n"
			 		+ "\r\n"
			 		+ "Note that a line containing SOLDE REPORTE, is a reported balence line in the account report and is therefore not a transaction, each line in the report contain a Solde witch is the balence of the account following the transaction that value should be formated as this example : [18 071,09]  "
			 		+ ",Please ensure that your response directly matches the JSON structure shown above. Thank you!\r\n";
		

					
			AssistantAnswer answer =service.reallyRun(prompt);
			
			result.firstMetaData=answer;
			return result;
			/*
			String message2 = "I have a string representing an invoice with the following content: " + answer.answer.toPrettyString()
					+ " Can you please extract the relevant information and present it in the following JSON format?:\n"
					+ invoiceJsonTemplate+ " Also make sure the amounts are stripped of any dolar sign they will be parsed as double";
			
			AssistantAnswer answer2 =reallyRun(message2);
			//message2 += "/n Take as context that my name is Samuel Audet-Arsenault, and im doing my accounting for my incorporation named:9321-0474 Québec Inc. Also recipient and origin canot be the same";
			// prompt+="/n Please make sure the Description is not empty";

//			String message2 = "My supplier list:[{\r\n" + "  \"id\": 123,\r\n"
//					+ "  \"name\": \"Cloutier & Longtin\",\r\n" + "  \"address\": \"450, rue du Parc suite 113\r\n"
//					+ "Saint-Eustache, Québec J7R 7G6\",\r\n" + "  \"contactEmail\": \"\",\r\n"
//					+ "  \"contactPhone\": \"\"\r\n" + "}]";

			//List<String> messages = new ArrayList();
			result.finalResult=answer2;*/
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
}
