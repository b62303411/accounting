package com.example.springboot.accounting.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.springboot.accounting.model.entities.AppUser;
import com.example.springboot.accounting.repository.AppUserRepository;
import com.example.springboot.accounting.service.util.PasswordUtil;
import com.example.springboot.accounting.service.util.UsernameGenerator;

import jakarta.annotation.PostConstruct;

@Component
public class InitAdminUser {
	
	@Autowired
    private AppUserRepository userRepository;
	
	@Autowired
	private PasswordUtil passwordUtil;
	
	
	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	@Autowired
	private UsernameGenerator userGen;
	    @PostConstruct
	    public void init() {
	        if (userRepository.count() == 0) {
	            AppUser admin = new AppUser();
	            
	            admin.setUsername(userGen.generateRandomUsername());
	            String password = generateRandomPassword(16);
	            admin.setPassword(encoder.encode(password));  // ensure you encode/hash this password
	            admin.setEnabled(true);
	            userRepository.save(admin);
	            String msg="Generated "+admin.getUsername()+" Password: " + password;
	            // Optionally, log the password or send it via email, etc.
	            System.out.println(msg);
	            
				Logger.getAnonymousLogger().log(Level.INFO,msg);
	        }
	    }

		private String generateRandomPassword(int i) {
			return passwordUtil.generateRandomPassword(i);
		}
}
