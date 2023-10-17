package com.example.springboot.accounting.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String username;
    private String password;
    private boolean enabled;

    public User() 
    {
    	
    }
    
	protected Long getId() {
		return id;
	}
	protected void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	protected void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	protected void setPassword(String password) {
		this.password = password;
	}
	protected boolean isEnabled() {
		return enabled;
	}
	protected void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

    // Getters, setters, etc.
    
}
