package com.example.springboot.accounting.service.util;

public class SuffixCount {
	public String suffix;
	public int id=0;
	public SuffixCount(String string) {
		this.suffix=string;
	}
	public String getNext() 
	{
		id++;
		return String.format("%s%03d", suffix, id);
	}
}
