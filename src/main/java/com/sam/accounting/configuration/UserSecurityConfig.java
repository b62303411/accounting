package com.sam.accounting.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sam.accounting.service.CustomUserDetailsService;

@Configuration
public class UserSecurityConfig {

	final SecurityConfig sec;
	PasswordEncoder ec;
	
	@Autowired
	public UserSecurityConfig(SecurityConfig sec,PasswordEncoder ec) 
	{
		this.sec=sec;
		this.ec=ec;
		
	}
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth,CustomUserDetailsService userDetailsService) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(ec);
	}
	


}
