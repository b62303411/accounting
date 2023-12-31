package com.example.springboot.accounting;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiKeyConfig {
	@Value("${openai.api.key}")
	private String apiKey;

	public String getApiKey() {
		return apiKey;
	}
}
