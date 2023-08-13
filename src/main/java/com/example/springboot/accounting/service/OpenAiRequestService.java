package com.example.springboot.accounting.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

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
import com.example.springboot.accounting.model.entities.Invoice;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class OpenAiRequestService {

	
	private ApiKeyConfig config;

	@Autowired
	public OpenAiRequestService(ApiKeyConfig config)
	{
		this.config = config;
	}
	
	public String submitInvoiceQuery(String fileContent) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Invoice invoiceTemplate = new Invoice();
			String invoiceJsonTemplate = objectMapper.writeValueAsString(invoiceTemplate );

			String prompt = "I have a string representing an invoice with the following content: " + fileContent
					+ " Can you please extract the relevant information and present it in the following JSON format?:\n"+
					invoiceJsonTemplate;
			prompt+="/n Take as context that my name is Samuel Audet-Arsenault, and im doing my accounting for my incorporation named:9321-0474 Québec Inc. Also recipient and origin canot be the same";
			
			String apiKey =config.getApiKey();

			CloseableHttpClient httpClient = HttpClients.createDefault();///v1/chat/completions
			HttpPost httpPost = new HttpPost("https://api.openai.com/v1/chat/completions");
			httpPost.setHeader("content-type", "application/json");
			httpPost.setHeader("authorization", "Bearer "+apiKey);
			ObjectMapper mapper = new ObjectMapper();
		    ObjectNode payload = mapper.createObjectNode();
		    //payload.put("prompt", prompt);
		    payload.put("model", "gpt-3.5-turbo");
	        payload.put("max_tokens", 1000);
	        // Creating messages array
	        ArrayNode messagesArray = mapper.createArrayNode();
	        ObjectNode userMessage = mapper.createObjectNode();
	        userMessage.put("role", "user");
	        userMessage.put("content", prompt);
	        messagesArray.add(userMessage);
	        payload.set("messages", messagesArray);
	        
			//String jsonPayload = "{ \"prompt\": \"" + prompt + "\", \"max_tokens\": 100 }";
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
}
