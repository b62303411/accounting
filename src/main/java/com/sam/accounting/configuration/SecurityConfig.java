package com.sam.accounting.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	

	
	
	public SecurityConfig()
	{
		
	}

	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((authz) -> {
			try {
				authz.requestMatchers("/google8e62e7868a386efb.html").permitAll().anyRequest().authenticated().and()
						.formLogin();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).httpBasic(withDefaults());
		return http.build();
	}



//	@Bean
//	public UserDetailsService userDetailsService(AuthenticationManagerBuilder auth, PasswordEncoder passwordEncoder) {
//		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//		String encodedPassword = passwordEncoder.encode("uHgyqopDYkPBH2f0WydB");
//		manager.createUser(User.withUsername("samuel.ars@gmail.com") // Using withUsername instead of the deprecated
//																		// method
//				.password(encodedPassword) // Use the already encoded password
//				.roles("USER") // or "ADMIN", "MANAGER", etc.
//				.build());
//
//		encodedPassword = passwordEncoder.encode("7VSaiqiRrfFvVYH2xrn3");
//
//		manager.createUser(User.withUsername("trevornelson2011@hotmail.com") // Using withUsername instead of the
//																				// deprecated method
//				.password(encodedPassword) // Use the already encoded password
//				.roles("USER") // or "ADMIN", "MANAGER", etc.
//				.build());
//
//		return manager;
//	}


	private Customizer<HttpBasicConfigurer<HttpSecurity>> withDefaults() {
		return httpBasicConfigurer -> {
			// Here you can customize the HttpBasicConfigurer.
			// For example:
			// httpBasicConfigurer.realmName("MyRealm");
		};
	}

//	@Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        //return (web) -> web.ignoring().antMatchers("/ignore1", "/ignore2");
//    }
}
