package com.example.springboot.accounting.model.dto;

import java.util.List;

public class MergeRequest {
    private List<Long> ids;

	public List<Long> getIds() {
		return ids;
	}

	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

}
