package com.sam.accounting.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view/invoices")
public class InvoiceController {
	
	@GetMapping("/folder")
	public String folderView(Model model) 
	{
		return "folder-invoice-processing"	;
	}

}
