package com.example.springboot.accounting.service.util;

import java.security.SecureRandom;

import org.springframework.stereotype.Service;

@Service
public class UsernameGenerator {

    private static final SecureRandom random = new SecureRandom();

    private static final String[] ADJECTIVES = {
        "Red", "Blue", "Green", "Yellow", "Brave", "Clever", "Swift", "Silent", "Mighty", "Calm"
    };

    private static final String[] NOUNS = {
        "Lion", "Eagle", "Panther", "Dolphin", "Wolf", "Fox", "Bear", "Shark", "Falcon", "Tiger"
    };

    public String generateRandomUsername() {
        String adjective = ADJECTIVES[random.nextInt(ADJECTIVES.length)];
        String noun = NOUNS[random.nextInt(NOUNS.length)];
        int digits = random.nextInt(100);  // Two random digits
        
        return adjective + noun + digits;
    }


}
