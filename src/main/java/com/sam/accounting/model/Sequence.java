package com.sam.accounting.model;

public class Sequence {
	public long value;

	public long getNext() {
		value++;
		return value;
	}
}
