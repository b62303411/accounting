package com.example.springboot.accounting.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NormalCorrectionSaveStrategy implements Correction{
	public JsonNode correct(String assistantContent) {

		int index_open = assistantContent.indexOf("{");
		ObjectMapper mapper = new ObjectMapper();
		int index_close = assistantContent.lastIndexOf("}") + 1;
		String content_ = assistantContent.substring(index_open, index_close);
		JsonNode extractedData=null;
		try {
			extractedData = mapper.readTree(content_);

		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return extractedData;
	}
}
