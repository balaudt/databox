package com.sagarius.goddess.client.model;

import java.util.Map;

import org.sagarius.radix.client.collect.Maps;
import org.sagarius.radix.client.model.BaseEntity;
import org.sagarius.radix.client.model.Metadata;
import org.sagarius.radix.client.model.util.BaseEntry;

public class MessageTemplate extends BaseEntity {
	public MessageTemplate() {
		super(BaseEntry.create());
	}

	public MessageTemplate(BaseEntry entry) {
		super(entry);
		init();
	}

	public MessageTemplate(String json) {
		super(BaseEntry.create(json));
		init();
	}

	private void init() {
		message = entry.get((Integer) FIELD_LIST.get("message")[0],
				String.class);
		name = entry.get((Integer) FIELD_LIST.get("name")[0], String.class);
		refId = entry.get((Integer) FIELD_LIST.get("refId")[0], String.class);
		BaseEntry baseEntry = entry.get((Integer) FIELD_LIST.get("meta")[0],
				BaseEntry.class);
		meta = new Metadata(baseEntry);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String refId;
	private Metadata meta;
	private String name;
	private String message;

	public static final Map<String, Object[]> FIELD_LIST = new Maps.ImmutableHashMap<String, Object[]>()
			.put("refId", new Object[] { 0, "Reference Id" })
			.put("meta", new Object[] { 1, "Meta data" })
			.put("name", new Object[] { 2, "Name" })
			.put("message", new Object[] { 3, "Message" }).build();

	public void setMessage(String message) {
		String oldmessage = this.message;
		this.message = message;
		support.firePropertyChange("message", oldmessage, message);
		entry.set((Integer) FIELD_LIST.get("message")[0], message);
	}

	public void setName(String name) {
		String oldname = this.name;
		this.name = name;
		support.firePropertyChange("name", oldname, name);
		entry.set((Integer) FIELD_LIST.get("name")[0], name);
	}

	public void setRefId(String refId) {
		String oldrefId = this.refId;
		this.refId = refId;
		support.firePropertyChange("refId", oldrefId, refId);
		entry.set((Integer) FIELD_LIST.get("refId")[0], refId);
	}

	public void setMeta(Metadata meta) {
		Metadata oldmeta = this.meta;
		this.meta = meta;
		support.firePropertyChange("meta", oldmeta, meta);
		entry.set((Integer) FIELD_LIST.get("meta")[0], meta);
	}

	public String getRefId() {
		return refId;
	}

	public Metadata getMeta() {
		return meta;
	}

	public String getName() {
		return name;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "MessageTemplate [refId=" + refId + ", meta=" + meta + ", name="
				+ name + ", message=" + message + "]";
	}

}
