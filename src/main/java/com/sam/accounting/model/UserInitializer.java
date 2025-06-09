package com.sam.accounting.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.sam.accounting.configuration.DefaultUser;
import com.sam.accounting.model.entities.AppUser;
import com.sam.accounting.repository.AppUserRepository;

import jakarta.annotation.PostConstruct;



@Component
public class UserInitializer {

    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private DefaultUser defaultUser;
    public UserInitializer(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = new BCryptPasswordEncoder(); // Use @Bean in config if needed
    }

    @PostConstruct
    public void init() {
        if (appUserRepository.findByUsername(defaultUser.getDefault_user()).isEmpty()) {
            AppUser user = new AppUser();
            user.setUsername(defaultUser.getDefault_user());
            user.setPassword(passwordEncoder.encode(defaultUser.getDefault_password())); // hash it
            user.setEnabled(true);
            appUserRepository.save(user);
        }
    }
}
