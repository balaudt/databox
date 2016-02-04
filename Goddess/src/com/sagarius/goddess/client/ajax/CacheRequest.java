package com.sagarius.goddess.client.ajax;

public class CacheRequest {
	private Object input;
	private CacheType type;

	public CacheRequest setType(CacheType type) {
		this.type = type;
		return this;
	}

	public CacheType getType() {
		return type;
	}

	public CacheRequest setInput(Object input) {
		this.input = input;
		return this;
	}

	public Object getInput() {
		return input;
	}
}
