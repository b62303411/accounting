package com.sam.accounting.service;


import java.security.MessageDigest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sam.accounting.model.FileType;
import com.sam.accounting.model.entities.Attachment;
import com.sam.accounting.repository.AttachmentRepository;

@Service
public class AttachmentService {
    @Autowired
    private AttachmentRepository attachmentRepository;

    public Attachment findFile(byte[] fileBytes) 
    {  	
    	String hash;
		try {
			hash = getSHA256Hash(fileBytes);
			return attachmentRepository.findBySha256Hash(hash);
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return null;
		
    }
    
    public void saveAttachment(Attachment attachment) {
        String hash = attachment.getSha256Hash();
        if (attachmentRepository.findBySha256Hash(hash) != null) {
            // Duplicate found, handle accordingly
            return;
        }
        // No duplicate found, proceed with saving
        attachmentRepository.save(attachment);
    }
    
    public static String getSHA256Hash(byte[] fileBytes) throws Exception {    
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(fileBytes);
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

	public Attachment createAttachment(byte[] fileBytes, String contentType) {
		Attachment att = new Attachment();
		String hash;
		try {
			hash = getSHA256Hash(fileBytes);		
		att.setSha256Hash(hash);
		Attachment existingAtt =  attachmentRepository.findBySha256Hash(hash);
//		if(null != existingAtt)
//			return existingAtt;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		att.setFile(fileBytes);
		String type = contentType;
		if (type.equals("application/pdf"))
			att.setType(FileType.PDF);
	
		return att;
		
	}
}