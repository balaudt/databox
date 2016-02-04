package com.sagarius.goddess.server.model;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.sagarius.radix.server.model.BaseEntity;
import org.sagarius.radix.server.model.Metadata;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class MessageTemplate extends BaseEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Key refId;
	@Persistent
	private Metadata meta;
	@Persistent
	private String name;
	@Persistent
	private String message;

	public Key getRefId() {
		return refId;
	}

	public void setRefId(Key refId) {
		this.refId = refId;
	}

	public Metadata getMeta() {
		return meta;
	}

	public void setMeta(Metadata meta) {
		this.meta = meta;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public MessageTemplate(Key refId) {
		super(com.sagarius.goddess.client.model.MessageTemplate.FIELD_LIST);
		this.refId = refId;
	}

	public MessageTemplate() {
		super(com.sagarius.goddess.client.model.MessageTemplate.FIELD_LIST);
	}

	@Override
	public String toString() {
		return "MessageTemplate [refId=" + refId + ", meta=" + meta + ", name="
				+ name + ", message=" + message + "]";
	}

	@Override
	public String render() {
		return render(this.getClass());
	}

}
