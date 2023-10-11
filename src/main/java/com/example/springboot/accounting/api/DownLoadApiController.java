package com.example.springboot.accounting.api;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.accounting.service.LatexGenerator;

@RestController
public class DownLoadApiController {

	@Autowired
	LatexGenerator gen;

	@GetMapping("/download-tex/{year}")
	public ResponseEntity<ByteArrayResource> downloadTeX(Model model, @PathVariable("year") Integer year)
			throws IOException {
		
		
		String latexContent = gen.incomeStatement(model, year);

		ByteArrayResource resource = new ByteArrayResource(latexContent.getBytes());

		return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN)
				.header("Content-Disposition", "attachment; filename=finantialSummary_"+year+".tex").body(resource);
	}
	
	@GetMapping("/download-pdf/{year}")
    public ResponseEntity<ByteArrayResource> downloadPDF(Model model, @PathVariable("year") Integer year) throws IOException, InterruptedException {
        String latexContent = gen.incomeStatement(model, year);
        String latexJsonContent = "{ \"latex\": \"" + escapeJson(latexContent) + "\" }";

        ProcessBuilder processBuilder = new ProcessBuilder("pdflatex", "-");
        processBuilder.redirectErrorStream(true);
        try {
            // Define the API endpoint
            URL url = new URL("http://127.0.0.1:5000/generate_pdf");
            
            // Open a connection to the endpoint
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/pdf");
            conn.setDoOutput(true);

            // Define your LaTeX content
           

            // Send the LaTeX content in the request body
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = latexJsonContent.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the PDF response
            InputStream in = conn.getInputStream();

    		ByteArrayResource resource = new ByteArrayResource(in.readAllBytes());
    		 in.close();
    		return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN)
    				.header("Content-Disposition", "attachment; filename=finantialSummary_"+year+".pdf").body(resource);
           

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().build();
        }

	private static String escapeJson(String s) {
	    return s.replace("\\", "\\\\")
	            .replace("\"", "\\\"")
	            .replace("\n", "\\n")
	            .replace("\r", "\\r");
	}
}
