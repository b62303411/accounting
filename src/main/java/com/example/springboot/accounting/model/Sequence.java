package com.example.springboot.accounting.model;

public class Sequence {
	public long value;

	public long getNext() {
		value++;
		return value;
	}
}
