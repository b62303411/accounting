package com.sam.accounting.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CorrectionSaveStrategy implements Correction{
	public ObjectNode correct(String assistantContent) {

		int index_open = assistantContent.indexOf("{");
		if (index_open != -1) {

		}
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode rootNode = objectMapper.createObjectNode();
		int index_close = assistantContent.lastIndexOf("}") + 1;
		String content_ = assistantContent.substring(index_open, index_close);
		JsonNode extractedData;
		try {
			extractedData = objectMapper.readTree(content_);

			rootNode.put("critique", assistantContent.substring(0, index_open));
			rootNode.put("proposedChange", extractedData);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rootNode;
	}
}
