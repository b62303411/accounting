package com.sam.accounting;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiKeyConfig {
	@Value("${openai.api.key}")
	private String apiKey;
	@Value("${openai.api.url}")
	private String url;
	public String getApiKey() {
		return apiKey;
	}
	public String getUrl() {
		return url;
	}
}
