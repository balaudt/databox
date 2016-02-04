package com.sagarius.goddess.client.model;

import java.util.Map;

import org.sagarius.radix.client.collect.Maps;
import org.sagarius.radix.client.model.BaseEntity;
import org.sagarius.radix.client.model.Metadata;
import org.sagarius.radix.client.model.util.BaseEntry;

public class ShortMessage extends BaseEntity {
	private String refId;
	private String group;
	private String mobiles;
	private String message;
	private Metadata meta;
	private String parent;

	public ShortMessage() {
		super(BaseEntry.create());
	}

	public ShortMessage(String json) {
		super(BaseEntry.create(json));
		init();
	}

	public ShortMessage(BaseEntry entry) {
		super(entry);
		init();
	}

	private void init() {
		refId = entry.get((Integer) FIELD_LIST.get("refId")[0], String.class);
		group = entry.get((Integer) FIELD_LIST.get("group")[0], String.class);
		mobiles = entry.get((Integer) FIELD_LIST.get("mobiles")[0],
				String.class);
		message = entry.get((Integer) FIELD_LIST.get("message")[0],
				String.class);
		BaseEntry baseEntry = entry.get((Integer) FIELD_LIST.get("meta")[0],
				BaseEntry.class);
		meta = new Metadata(baseEntry);
		parent = entry.get((Integer) FIELD_LIST.get("parent")[0], String.class);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final Map<String, Object[]> FIELD_LIST = new Maps.ImmutableHashMap<String, Object[]>()
			.put("refId", new Object[] { 0, "Reference Id" })
			.put("group", new Object[] { 1, "Group" })
			.put("mobiles", new Object[] { 2, "Mobile numbers" })
			.put("message", new Object[] { 3, "Message" })
			.put("meta", new Object[] { 4, "Meta information" })
			.put("parent", new Object[] { 5, "parent" }).build();

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getMobiles() {
		return mobiles;
	}

	public void setMobiles(String mobiles) {
		this.mobiles = mobiles;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Metadata getMeta() {
		return meta;
	}

	public void setMeta(Metadata meta) {
		this.meta = meta;
	}

	@Override
	public String toString() {
		return "ShortMessage [refId=" + refId + ", group=" + group
				+ ", mobiles=" + mobiles + ", message=" + message + ", meta="
				+ meta + "]";
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getParent() {
		return parent;
	}

}
