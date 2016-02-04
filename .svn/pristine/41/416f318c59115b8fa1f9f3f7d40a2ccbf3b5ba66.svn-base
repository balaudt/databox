package com.sagarius.goddess.server.model;

import javax.jdo.annotations.Embedded;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.sagarius.radix.server.model.BaseEntity;
import org.sagarius.radix.server.model.Metadata;

import com.google.appengine.api.datastore.Key;
import com.sagarius.goddess.client.model.enumerations.DocumentType;

@PersistenceCapable
public class Document extends BaseEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Persistent
	@PrimaryKey
	private Key documentKey;
	@Persistent
	private DocumentType type;
	@Persistent
	private String param1;
	@Persistent
	private Integer param2;
	@Persistent
	private String param3;
	@Persistent
	private Key academicYear;
	@Persistent
	private String docTitle;
	@Persistent
	private String docName;
	@Persistent
	private String cccKey;
	@Persistent
	@Embedded
	private Metadata metadata;

	public String getDocTitle() {
		return docTitle;
	}

	public void setDocTitle(String docTitle) {
		this.docTitle = docTitle;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public Key getDocumentKey() {
		return documentKey;
	}

	public void setDocumentKey(Key documentKey) {
		this.documentKey = documentKey;
	}

	public DocumentType getType() {
		return type;
	}

	public void setType(DocumentType type) {
		this.type = type;
	}

	public String getParam1() {
		return param1;
	}

	public void setParam1(String param1) {
		this.param1 = param1;
	}

	public Integer getParam2() {
		return param2;
	}

	public void setParam2(Integer param2) {
		this.param2 = param2;
	}

	public String getParam3() {
		return param3;
	}

	public void setParam3(String param3) {
		this.param3 = param3;
	}

	public Key getAcademicYear() {
		return academicYear;
	}

	public void setAcademicYear(Key academicYear) {
		this.academicYear = academicYear;
	}

	public Document() {
		super(com.sagarius.goddess.client.model.Document.FIELD_LIST);
	}

	public Document(Key documentKey) {
		super(com.sagarius.goddess.client.model.Document.FIELD_LIST);
		this.documentKey = documentKey;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((documentKey == null) ? 0 : documentKey.hashCode());
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
		Document other = (Document) obj;
		if (documentKey == null) {
			if (other.documentKey != null)
				return false;
		} else if (!documentKey.equals(other.documentKey))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Document [documentKey=" + documentKey + ", type=" + type
				+ ", param1=" + param1 + ", param2=" + param2 + ", param3="
				+ param3 + ", academicYear=" + academicYear + ", docTitle="
				+ docTitle + ", docName=" + docName + ", cccKey=" + cccKey
				+ ", metadata=" + metadata + "]";
	}

	public void setCccKey(String cccKey) {
		this.cccKey = cccKey;
	}

	public String getCccKey() {
		return cccKey;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	public Metadata getMetadata() {
		return metadata;
	}

	@Override
	public String render() {
		return render(Document.class);
	}

	@Override
	public Metadata getMeta() {
		return metadata;
	}

	@Override
	public void setMeta(Metadata metadata) {
		this.metadata=metadata;
	}
}
