package com.example.springboot.accounting.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.springboot.accounting.model.entities.Attachment;
import com.example.springboot.accounting.model.entities.Invoice;
import com.example.springboot.accounting.repository.InvoiceRepository;

@Service
public class InvoiceService {

	private InvoiceRepository invoiceRepository;

	@Autowired
	public InvoiceService(InvoiceRepository invoiceRepository) {
		this.invoiceRepository = invoiceRepository;
	}
	
	/**
	 * 
	 * @param att
	 * @return
	 * @throws IOException 
	 */
	public byte[] createPreviewImageAsBytesForAttachment(Attachment att) throws IOException 
	{
		byte[] file = att.getFile();
		BufferedImage preview = createPreviewImage(file);
		byte[] imageBytes = createPreviewImageAsBytes(preview);
		return imageBytes;		
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws IOException 
	 */
	public byte[] createPreviewImageAsBytesForInvoice(Long id) throws IOException {
		Optional<Invoice> invoice = invoiceRepository.findById(id);
		for (Attachment iterable_element : invoice.get().getAttachments()) {
			return createPreviewImageAsBytesForAttachment(iterable_element);
			//String base64Image = Base64.getEncoder().encodeToString(imageBytes);
		}
		return null;
		

	}

	/**
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private BufferedImage createPreviewImage(byte[] file) throws IOException {
		PDDocument document = PDDocument.load(file);
		PDFRenderer pdfRenderer = new PDFRenderer(document);
		BufferedImage image = pdfRenderer.renderImageWithDPI(0, 300);
		document.close();
		return image;
	}
	
	/**
	 * 
	 * @param preview
	 * @return
	 * @throws IOException
	 */
	private byte[] createPreviewImageAsBytes(BufferedImage preview) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(preview, "png", baos);
		return baos.toByteArray();

	}
	public BufferedImage createPreviewImage(MultipartFile file) throws IOException {
		PDDocument document = PDDocument.load(file.getInputStream());
		PDFRenderer pdfRenderer = new PDFRenderer(document);
		// Render the first page to an image
		BufferedImage image = pdfRenderer.renderImageWithDPI(0, 300);
		document.close();
		return image;
	}

	public byte[] createPreviewImageAsBytes(MultipartFile file) throws IOException {
		BufferedImage image = createPreviewImage(file);
		return createPreviewImageAsBytes(image);
	}
	/*
	 * private byte[] createPreviewImageAsBytes(Long id) {
	 * 
	 * 
	 * for (Attachment iterable_element : invoice.get().getAttachments()) { byte[]
	 * file = iterable_element.getFile(); try { BufferedImage preview =
	 * createPreviewImage(file); byte[] imageBytes =
	 * createPreviewImageAsBytes(preview); String base64Image =
	 * Base64.getEncoder().encodeToString(imageBytes); return null; }
	 */

}
