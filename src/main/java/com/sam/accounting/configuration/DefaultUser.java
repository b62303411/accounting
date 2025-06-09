package com.sam.accounting.configuration;

import org.springframework.beans.factory.annotation.Value;

public class DefaultUser {
	@Value("${default_user}")
	private String default_user;
	@Value("${default_password}")
	private String default_password;
}
