package com.sagarius.goddess.client.model.neuf;

import java.util.Map;

import org.sagarius.radix.client.collect.Maps;
import org.sagarius.radix.client.model.BaseEntity;
import org.sagarius.radix.client.model.Metadata;
import org.sagarius.radix.client.model.util.BaseEntry;

public class ShortMessage extends BaseEntity {
	private String refId;
	private String group;
	private Metadata meta;
	private String message;
	private String name;
	private String id;
	private String phone;
	private String parent;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "ShortMessage [refId=" + refId + ", group=" + group + ", meta="
				+ meta + ", message=" + message + ", name=" + name + ", id="
				+ id + ", phone=" + phone + ", parent=" + parent + "]";
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	public Metadata getMeta() {
		return meta;
	}

	public void setMeta(Metadata meta) {
		this.meta = meta;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ShortMessage() {
		super(BaseEntry.create());
	}

	public ShortMessage(String json) {
		super(BaseEntry.create(json));
		init();
	}

	public static final Map<String, Object[]> FIELD_LIST = new Maps.ImmutableHashMap<String, Object[]>()
			.put("refId", new Object[] { 0, "Reference Id" })
			.put("meta", new Object[] { 2, "Meta information" })
			.put("message", new Object[] { 3, "Message" })
			.put("name", new Object[] { 4, "Name" })
			.put("id", new Object[] { 5, "Id" })
			.put("phone", new Object[] { 6, "Id" })
			.put("group", new Object[] { 7, "Id" })
			.put("parent", new Object[] { 8, "Parent key" }).build();

	private void init() {
		refId = entry.get((Integer) FIELD_LIST.get("refId")[0], String.class);
		BaseEntry baseEntry = entry.get((Integer) FIELD_LIST.get("meta")[0],
				BaseEntry.class);
		meta = new Metadata(baseEntry);
		message = entry.get((Integer) FIELD_LIST.get("message")[0],
				String.class);
		name = entry.get((Integer) FIELD_LIST.get("name")[0], String.class);
		id = entry.get((Integer) FIELD_LIST.get("id")[0], String.class);
		parent = entry.get((Integer) FIELD_LIST.get("parent")[0], String.class);
		phone = entry.get((Integer) FIELD_LIST.get("phone")[0], String.class);
		group = entry.get((Integer) FIELD_LIST.get("group")[0], String.class);
	}

	public ShortMessage(BaseEntry entry) {
		super(entry);
		init();
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhone() {
		return phone;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getParent() {
		return parent;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getGroup() {
		return group;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
