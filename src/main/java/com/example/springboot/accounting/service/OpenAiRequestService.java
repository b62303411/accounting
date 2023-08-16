package com.example.springboot.accounting.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springboot.accounting.ApiKeyConfig;
import com.example.springboot.accounting.model.AiFileResult;
import com.example.springboot.accounting.model.AssistantAnswer;
import com.example.springboot.accounting.model.dto.InvoiceDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class OpenAiRequestService {

	private ApiKeyConfig config;

	@Autowired
	public OpenAiRequestService(ApiKeyConfig config) {
		this.config = config;
	}

	public String guessSupplier(Map<String, String> suppliersWithServices, String name) {
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			
			// Convert the supplier names list to a formatted string
			// Convert the map to a formatted string
		    String formattedSuppliers = suppliersWithServices.entrySet().stream()
		        .map(entry -> String.format("\"%s\" (Service: %s)", entry.getKey(), entry.getValue()))
		        .collect(Collectors.joining(",\n"));
		    
		    // Create the structured query
		    // Create the structured query
		    String query = String.format(
		        "Given these suppliers and their services: \n%s\n\nWhich one matches closest to: \"%s\"?",
		        formattedSuppliers,
		        name
		    );
		    
		    query+="\n please provide your answer as a json format with the field answer.";

			List<String> messages = new ArrayList<String>();
			messages.add(query);
			return runPrompt(messages);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
	public AssistantAnswer reallyRun(String prompt) throws JsonMappingException, JsonProcessingException 
	{
		List<String> messages = new ArrayList<String>();
		messages.add(prompt);
		AssistantAnswer answer = new AssistantAnswer();
		String value = runPrompt(messages);			
		JsonNode data = extractData(value);			
		answer.result=value;
		answer.answer= data;
		
		return answer;

	}
	
	public String runPrompt(List<String> prompts) {
		try {
			String apiKey = config.getApiKey();

			CloseableHttpClient httpClient = HttpClients.createDefault();/// v1/chat/completions
			HttpPost httpPost = new HttpPost("https://api.openai.com/v1/chat/completions");
			httpPost.setHeader("content-type", "application/json");
			httpPost.setHeader("authorization", "Bearer " + apiKey);
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode payload = mapper.createObjectNode();
			// payload.put("prompt", prompt);
			payload.put("model", "gpt-3.5-turbo");
			payload.put("max_tokens", 2000);
			ArrayNode messagesArray = mapper.createArrayNode();
			// Creating messages array
			for (String message : prompts) {
				ObjectNode userMessage = mapper.createObjectNode();
				userMessage.put("role", "user");
				userMessage.put("content", message);
				messagesArray.add(userMessage);
			}
			payload.set("messages", messagesArray);

			// String jsonPayload = "{ \"prompt\": \"" + prompt + "\", \"max_tokens\": 100
			// }";
			httpPost.setEntity(new StringEntity(payload.toString(), StandardCharsets.UTF_8));

			try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
				HttpEntity entity = response.getEntity();
				return entity != null ? EntityUtils.toString(entity) : null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	public AiFileResult submitInvoiceQuery(String fileContent, String extraContext) {

		AiFileResult result = new AiFileResult();
		ObjectMapper objectMapper = new ObjectMapper();
		InvoiceDto invoiceTemplate = new InvoiceDto();
		String invoiceJsonTemplate;
		try {
			invoiceJsonTemplate = objectMapper.writeValueAsString(invoiceTemplate);

			String message1 = "I have a string representing an invoice with the following content: " + fileContent
					+ " Can you please extract the relevant information and present it in aJSON format?";
			
					
			AssistantAnswer answer =reallyRun(message1);
			
			result.firstMetaData=answer;
			
			
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
			result.finalResult=answer2;
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
	
	public JsonNode extractData(String responseString) throws JsonMappingException, JsonProcessingException 
	{
		ObjectMapper mapper = new ObjectMapper();
		JsonNode responseJson = mapper.readTree(responseString);

		// Check for errors
		if (responseJson.has("error")) {
			String errorMessage = responseJson.get("error").get("message").asText();
			System.out.println("Error from OpenAI: " + errorMessage);
			return null;
		}
		// Get the assistant's message content
		String assistantContent = responseJson.path("choices").get(0).path("message").path("content").asText();
		// Process the extracted text as needed
		// For example, you may want to convert it to a JSON object if it's in JSON
		// format
		JsonNode extractedData = mapper.readTree(assistantContent);
		
		return extractedData;
	}
}
