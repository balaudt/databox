package com.sagarius.goddess.server.model;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.sagarius.radix.server.model.BaseEntity;
import org.sagarius.radix.server.model.Metadata;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class ShortMessage extends BaseEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Key refId;
	@Persistent
	private String group;
	@Persistent
	private String mobiles;
	@Persistent
	private String message;
	@Persistent
	private Metadata meta;
	@Persistent
	private String ids;
	@Persistent
	private Key parent;

	public Key getRefId() {
		return refId;
	}

	public void setRefId(Key refId) {
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

	public ShortMessage(Key refId) {
		super(com.sagarius.goddess.client.model.ShortMessage.FIELD_LIST);
		this.refId = refId;
	}

	public ShortMessage() {
		super(com.sagarius.goddess.client.model.ShortMessage.FIELD_LIST);
	}

	@Override
	public String toString() {
		return "ShortMessage [refId=" + refId + ", group=" + group
				+ ", mobiles=" + mobiles + ", message=" + message + ", meta="
				+ meta + ", ids=" + ids + ", parent=" + parent + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((refId == null) ? 0 : refId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ShortMessage other = (ShortMessage) obj;
		if (refId == null) {
			if (other.refId != null)
				return false;
		} else if (!refId.equals(other.refId))
			return false;
		return true;
	}

	@Override
	public String render() {
		return render(ShortMessage.class);
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getIds() {
		return ids;
	}

	public void setParent(Key parent) {
		this.parent = parent;
	}

	public Key getParent() {
		return parent;
	}

}
