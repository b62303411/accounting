package com.sam.accounting.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultUser {
	@Value("${default_user}")
	private String default_user;
	@Value("${default_password}")
	private String default_password;
	public String getDefault_user() {
		return default_user;
	}
	public String getDefault_password() {
		return default_password;
	}
	
	
}
