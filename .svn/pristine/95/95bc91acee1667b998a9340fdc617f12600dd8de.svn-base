package com.sagarius.goddess.server.model;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.sagarius.radix.server.model.BaseEntity;
import org.sagarius.radix.server.model.Metadata;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class ACLStatus extends BaseEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Persistent
	@PrimaryKey
	private Key refId;
	@Persistent
	private Metadata metadata;
	@Persistent
	private Key document;

	public Key getRefId() {
		return refId;
	}

	public void setRefId(Key refId) {
		this.refId = refId;
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	public Key getDocument() {
		return document;
	}

	public void setDocument(Key document) {
		this.document = document;
	}

	@Override
	public String toString() {
		return "ACLStatus [refId=" + refId + ", metadata=" + metadata
				+ ", document=" + document + "]";
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
		ACLStatus other = (ACLStatus) obj;
		if (refId == null) {
			if (other.refId != null)
				return false;
		} else if (!refId.equals(other.refId))
			return false;
		return true;
	}

	@Override
	public String render() {
		return render(this.getClass());
	}

	@Override
	public Metadata getMeta() {
		return metadata;
	}

	@Override
	public void setMeta(Metadata metadata) {
		this.metadata = metadata;
	}

	public ACLStatus() {
		super(null);
	}
}
