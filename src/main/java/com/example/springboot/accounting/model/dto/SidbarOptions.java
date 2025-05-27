package com.example.springboot.accounting.model.dto;

public class SidbarOptions {

	/**
	 * 
	 * @param display
	 * @param url
	 */
	public SidbarOptions(String display, String url) {
		super();
		this.display = display;
		this.url = url;
	}
	public String display;
	public String url;
}
