package com.sam.accounting.service.util;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sam.accounting.ApiKeyConfig;
import com.sam.accounting.model.AssistantAnswer;

@Service
public class OpenAiRequestService {
	List<DialogLine> dialogues;
	private ApiKeyConfig config;
	private JsonBlockExtractor extractor;
	
	@Autowired
	public OpenAiRequestService(ApiKeyConfig config,JsonBlockExtractor extractor) {
		this.config = config;
		this.extractor = extractor;
		dialogues = new ArrayList();
	}

	
	public AssistantAnswer reallyRun(String prompt, Correction correction)
			throws JsonMappingException, JsonProcessingException {

		AssistantAnswer answer = new AssistantAnswer();
		
		String value = exec_prompt(prompt);
		
		JsonNode data = extractData(value, correction);
		if( null == data) 
		{
			String not_great =""
					+ "Make sure that your json response respect the required formatting."
					+ "We agreed to have our json response within delimiter ```json  ```"
					+ "Remember that we have code that need to parse your answer and the delimiter is very important.";
			
			 ObjectMapper objectMapper = new ObjectMapper();
			 objectMapper.writeValueAsString(this.dialogues);
			value = exec_prompt(not_great);
			data = extractData(value, correction);
		}
		answer.result = value;
		answer.answer = data;

		return answer;

	}

	public AssistantAnswer runPrompts__(String message, Correction correction)
			throws JsonMappingException, JsonProcessingException {

		AssistantAnswer answer = new AssistantAnswer();
		String value = exec_prompt(message);
		JsonNode data = extractData(value, correction);
		answer.result = value;
		answer.answer = data;

		return answer;

	}
	public HttpPost getPost()
	{
		String apiKey = config.getApiKey();
		String host = config.getUrl();

		HttpPost httpPost = new HttpPost(String.format("http://%s/v1/chat/completions", host));
		httpPost.setHeader("content-type", "application/json");
		httpPost.setHeader("authorization", "Bearer " + apiKey);
		return httpPost;

	}
	
	public String exec_prompt(String message) 
	{
		try {
			DialogLine user_line = new DialogLine();
			user_line.content=message;
			user_line.role="user";
			this.dialogues.add(user_line);
			
			CloseableHttpClient httpClient = HttpClients.createDefault();/// v1/chat/completions
			HttpPost httpPost = getPost();
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode payload = mapper.createObjectNode();
			// payload.put("prompt", prompt);
			payload.put("model", "gpt-3.5-turbo");
			payload.put("max_tokens", 2000);
			ArrayNode messagesArray = mapper.createArrayNode();
		
			// Creating messages array
			for (DialogLine line : this.dialogues) {
				ObjectNode userMessage = mapper.createObjectNode();
				userMessage.put("role", line.role);
				userMessage.put("content", line.content);
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
	
	public String runPromptList(List<String> prompts) {
		try {
		
			CloseableHttpClient httpClient = HttpClients.createDefault();/// v1/chat/completions
			HttpPost httpPost = getPost();
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode payload = mapper.createObjectNode();
			// payload.put("prompt", prompt);
			payload.put("model", "gpt-3.5-turbo");
			payload.put("max_tokens", 2000);
			ArrayNode messagesArray = mapper.createArrayNode();
		
			// Creating messages array
			for (String message : prompts) {
				DialogLine line = new DialogLine();
				line.content=message;
				line.role="user";
				this.dialogues.add(line);
				
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

	public JsonNode extractJsonFromDelimitedBlock(String rawResponse) throws Exception {
	    return this.extractor.extractJsonFromFencedBlock(rawResponse);
	}

	public JsonNode extractData(String responseString, Correction corr)
			throws JsonMappingException, JsonProcessingException {
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
		DialogLine line = new DialogLine();
		line.content=assistantContent;
		line.role = "assistant";
		this.dialogues.add(line);
		
		// Process the extracted text as needed
		// For example, you may want to convert it to a JSON object if it's in JSON
		// format
		try {
			JsonNode extractedData = extractJsonFromDelimitedBlock(assistantContent);

			return extractedData;
		} catch (Exception e) {

			if (null != corr) {
				return corr.correct(assistantContent);
			}

			return null;
		}

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
					formattedSuppliers, name);

			query += "\n please provide your answer as a json format with the field answer.";

			List<String> messages = new ArrayList<String>();
			messages.add(query);
			return null; //runPrompts(messages);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

}
