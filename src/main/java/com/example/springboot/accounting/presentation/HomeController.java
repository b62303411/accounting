package com.example.springboot.accounting.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class HomeController {
	
	
	public HomeController() {
		super();
		// TODO Auto-generated constructor stub
	}

	@GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "Hello, Thymeleaf!");
        model.addAttribute("year", 2023);
        return "home";
    }
}
