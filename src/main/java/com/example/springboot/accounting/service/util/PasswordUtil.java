package com.example.springboot.accounting.service.util;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.stereotype.Service;

@Service
public class PasswordUtil {

    private static final SecureRandom random = new SecureRandom();
    
    public String generateRandomPassword(int length) {
        // Ensure the length is at least 1
        length = Math.max(length, 1);

        byte[] values = new byte[length];
        random.nextBytes(values);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(values).substring(0, length);
    }

}
