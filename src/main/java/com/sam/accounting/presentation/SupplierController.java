package com.sam.accounting.presentation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sam.accounting.model.entities.Supplier;
import com.sam.accounting.service.SupplierService;

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