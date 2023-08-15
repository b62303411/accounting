package com.example.springboot.accounting.presentation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.springboot.accounting.model.entities.Supplier;
import com.example.springboot.accounting.service.SupplierService;

@Controller
@RequestMapping("/view/suppliers")
public class SupplierController {

	@Autowired
	SupplierService supplierService;
	
	@GetMapping
	public String getall(Model model) {
		
		List<Supplier> suppliers = supplierService.getAllSuppliers();
		model.addAttribute("suppliers", suppliers);
		return "suppliers";
	}
}