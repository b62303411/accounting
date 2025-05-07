package com.sam.accounting.service;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sam.accounting.model.entities.Attachment;
import com.sam.accounting.model.entities.ExploitationExpense;
import com.sam.accounting.model.entities.Invoice;
import com.sam.accounting.repository.AttachmentRepository;
import com.sam.accounting.repository.ExploitationExpenseRepository;
import com.sam.accounting.repository.InvoiceRepository;

@Service
public class InvoiceService {

	private InvoiceRepository invoiceRepository;
	private ExploitationExpenseRepository expenseRepository;
	private AttachmentRepository attachmentRepository;

	@Autowired
	public InvoiceService(ExploitationExpenseRepository expenseRepository,AttachmentRepository attachmentRepository, InvoiceRepository invoiceRepository) {
		this.invoiceRepository = invoiceRepository;
		this.attachmentRepository = attachmentRepository;
		this.expenseRepository=expenseRepository;
	}

	/**
	 * 
	 * @param id
	 * @return
	 * @throws IOException
	 */
	public byte[] createPreviewImageAsBytesForAttachment(Long id) throws IOException {

		Optional<Attachment> att = attachmentRepository.findById(id);
		return createPreviewImageAsBytesForAttachment(att.get());
	}

	/**
	 * 
	 * @param att
	 * @return
	 * @throws IOException
	 */
	public byte[] createPreviewImageAsBytesForAttachment(Attachment att) throws IOException {
		byte[] file = att.getFile();
		BufferedImage preview = createPreviewImage(file);
	
		int maxSize = 1000; // Example target height
		BufferedImage resizedPreview = resizeImage(preview, maxSize);

		byte[] imageBytes = createPreviewImageAsBytes(resizedPreview);
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
			// String base64Image = Base64.getEncoder().encodeToString(imageBytes);
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

	private BufferedImage resizeImage(BufferedImage originalImage, int maxSize) {
		int originalWidth = originalImage.getWidth();
	    int originalHeight = originalImage.getHeight();
	    float aspectRatio = (float) originalWidth / originalHeight;

	    int targetWidth, targetHeight;

	    if (originalWidth > originalHeight) {
	        targetWidth = maxSize;
	        targetHeight = (int) (targetWidth / aspectRatio);
	    } else {
	        targetHeight = maxSize;
	        targetWidth = (int) (targetHeight * aspectRatio);
	    }
		
		Image tmp = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
		BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = resizedImage.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();
		return resizedImage;
	}

	public List<Invoice> findInvoicesByExpenseId(Long expenseId) {
		Optional<ExploitationExpense> ex = expenseRepository.findById(expenseId);
		List<Invoice> invoices = invoiceRepository.findByAmountWithinTolerance(ex.get().getTransaction().getAmount(),50.0);
		return invoices;
	}

	public void deleteInvoice(Long id) {
		invoiceRepository.deleteById(id);
		
	}

	public List<Invoice> findAllByOrigin(String name) {

		return invoiceRepository.findAllByOrigine(name);
	}

	public Invoice findById(Long id) {
		return invoiceRepository.findById(id).get();
	}

	public Invoice save(Invoice invoice) {
		return invoiceRepository.save(invoice);
		
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
