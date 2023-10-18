package com.example.springboot.accounting.service;

import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.springboot.accounting.model.entities.AppUser;
import com.example.springboot.accounting.repository.AppUserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  
    private final AppUserRepository userRepository;
  
    @Autowired
    public CustomUserDetailsService(AppUserRepository userRepository) 
    {
    	this.userRepository=userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with username: " + username));
        Logger.getAnonymousLogger().log(Level.INFO,username);
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("USER")));
    }
}