package com.sam.accounting.service.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PdfService {
	public String extractTextFromPDF(MultipartFile file) throws IOException {
		// Convert the MultipartFile to an InputStream
		try (InputStream inputStream = file.getInputStream()) {
			// Load the PDF document
			PDDocument document = PDDocument.load(inputStream);

			// Create a PDFTextStripper to extract the text
			PDFTextStripper pdfStripper = new PDFTextStripper();

			// Get the text from the document
			String text = pdfStripper.getText(document);

			// Close the document
			document.close();

			// Do something with the text, e.g., parse the invoice details
			// ...

			return text;
		}
	}
}
