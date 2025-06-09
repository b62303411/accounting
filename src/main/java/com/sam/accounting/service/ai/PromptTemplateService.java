package com.sam.accounting.service.ai;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Service
public class PromptTemplateService {
	private final Configuration freemarkerConfig;

    public PromptTemplateService() throws IOException {
        freemarkerConfig = new Configuration(Configuration.VERSION_2_3_32);
        freemarkerConfig.setClassForTemplateLoading(getClass(), "/prompts");
        freemarkerConfig.setDefaultEncoding("UTF-8");
    }

    public String renderBankStatementPrompt(String rawText) throws IOException, TemplateException {
        Template template = freemarkerConfig.getTemplate("bank-statement-prompt.ftl");

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("rawText", rawText);

        try (StringWriter out = new StringWriter()) {
            template.process(dataModel, out);
            return out.toString();
        }
    }
}
