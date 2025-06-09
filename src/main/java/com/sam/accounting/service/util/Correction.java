package com.sam.accounting.service.util;

import com.fasterxml.jackson.databind.JsonNode;

public interface Correction {
	public JsonNode correct(String assistantContent) ;
}
