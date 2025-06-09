package com.sam.accounting.service.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class JsonBlockExtractor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Multiline, dot-all regex to extract content between ```json ... ```
    private static final Pattern JSON_BLOCK_PATTERN = Pattern.compile(
        "```json\\s*\\n(.*?)\\n```", Pattern.DOTALL | Pattern.CASE_INSENSITIVE
    );

    public JsonNode extractJsonFromFencedBlock(String llmResponse) {
        Matcher matcher = JSON_BLOCK_PATTERN.matcher(llmResponse);

        if (!matcher.find()) {
            throw new IllegalArgumentException("No valid ```json block found.");
        }

        String jsonBlock = matcher.group(1).trim();

        try {
            return objectMapper.readTree(jsonBlock);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse extracted JSON:\n" + jsonBlock, e);
        }
    }
}
