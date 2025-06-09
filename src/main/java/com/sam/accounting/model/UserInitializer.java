package com.sam.accounting.model;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.sam.accounting.model.entities.AppUser;
import com.sam.accounting.repository.AppUserRepository;

import jakarta.annotation.PostConstruct;



@Component
public class UserInitializer {

    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserInitializer(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = new BCryptPasswordEncoder(); // Use @Bean in config if needed
    }

    @PostConstruct
    public void init() {
        if (appUserRepository.findByUsername("samuel").isEmpty()) {
            AppUser user = new AppUser();
            user.setUsername("samuel");
            user.setPassword(passwordEncoder.encode("25528B24isk30!!")); // hash it
            user.setEnabled(true);
            appUserRepository.save(user);
        }
    }
}
