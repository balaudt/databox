package com.sagarius.goddess.client.model;

import java.util.Map;

import org.sagarius.radix.client.collect.Maps;
import org.sagarius.radix.client.model.BaseEntity;
import org.sagarius.radix.client.model.util.BaseEntry;

import com.sagarius.goddess.client.model.enumerations.DocumentType;

public class Document extends BaseEntity {

	@Override
	public String toString() {
		return "Document [documentKey=" + documentKey + ", type=" + type
				+ ", param1=" + param1 + ", param2=" + param2 + ", param3="
				+ param3 + ", academicYear=" + academicYear + ", docTitle="
				+ docTitle + ", docName=" + docName + ", cccKey=" + cccKey
				+ "]";
	}

	public Document() {
		super(BaseEntry.create());
	}

	public Document(BaseEntry entry) {
		super(entry);
		init();
	}

	public Document(String json) {
		super(BaseEntry.create(json));
		init();
	}

	private void init() {
		docName = entry.get((Integer) FIELD_LIST.get("docName")[0],
				String.class);
		documentKey = entry.get((Integer) FIELD_LIST.get("documentKey")[0],
				String.class);
		param1 = entry.get((Integer) FIELD_LIST.get("param1")[0], String.class);
		// param2 = entry
		// .get((Integer) FIELD_LIST.get("param2")[0], Integer.class);
		param3 = entry.get((Integer) FIELD_LIST.get("param3")[0], String.class);
		docTitle = entry.get((Integer) FIELD_LIST.get("docTitle")[0],
				String.class);
		type = entry.get((Integer) FIELD_LIST.get("type")[0], String.class);
		cccKey = entry.get((Integer) FIELD_LIST.get("cccKey")[0], String.class);
		academicYear = entry.get((Integer) FIELD_LIST.get("academicYear")[0],
				String.class);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String documentKey;
	private String type;
	private String param1;
	private Integer param2;
	private String param3;
	private String academicYear;
	private String docTitle;
	private String docName;
	private String cccKey;
	public static final Map<String, Object[]> FIELD_LIST = new Maps.ImmutableHashMap<String, Object[]>()
			.put("documentKey", new Object[] { 0, "Document Key" })
			.put("type", new Object[] { 1, "Type" })
			.put("param1", new Object[] { 2, "Parameter 1" })
			.put("param2", new Object[] { 3, "Parameter 2" })
			.put("param3", new Object[] { 4, "Parameter 3" })
			.put("academicYear", new Object[] { 5, "Academic Year" })
			.put("docTitle", new Object[] { 6, "Document title" })
			.put("docName", new Object[] { 7, "Document Name" })
			.put("cccKey", new Object[] { 8, "View key" }).build();

	public String getDocumentKey() {
		return documentKey;
	}

	public DocumentType getType() {
		return DocumentType.valueOf(type);
	}

	public String getParam1() {
		return param1;
	}

	public Integer getParam2() {
		return param2;
	}

	public String getParam3() {
		return param3;
	}

	public String getAcademicYear() {
		return academicYear;
	}

	public String getDocTitle() {
		return docTitle;
	}

	public String getDocName() {
		return docName;
	}

	public String getCccKey() {
		return cccKey;
	}

	public void setDocName(String docName) {
		String olddocName = this.docName;
		this.docName = docName;
		support.firePropertyChange("docName", olddocName, docName);
		entry.set((Integer) FIELD_LIST.get("docName")[0], docName);
	}

	public void setDocumentKey(String documentKey) {
		String olddocumentKey = this.documentKey;
		this.documentKey = documentKey;
		support.firePropertyChange("documentKey", olddocumentKey, documentKey);
		entry.set((Integer) FIELD_LIST.get("documentKey")[0], documentKey);
	}

	public void setParam1(String param1) {
		String oldparam1 = this.param1;
		this.param1 = param1;
		support.firePropertyChange("param1", oldparam1, param1);
		entry.set((Integer) FIELD_LIST.get("param1")[0], param1);
	}

	public void setParam2(Integer param2) {
		Integer oldparam2 = this.param2;
		this.param2 = param2;
		support.firePropertyChange("param2", oldparam2, param2);
		entry.set((Integer) FIELD_LIST.get("param2")[0], param2);
	}

	public void setParam3(String param3) {
		String oldparam3 = this.param3;
		this.param3 = param3;
		support.firePropertyChange("param3", oldparam3, param3);
		entry.set((Integer) FIELD_LIST.get("param3")[0], param3);
	}

	public void setDocTitle(String docTitle) {
		String olddocTitle = this.docTitle;
		this.docTitle = docTitle;
		support.firePropertyChange("docTitle", olddocTitle, docTitle);
		entry.set((Integer) FIELD_LIST.get("docTitle")[0], docTitle);
	}

	public void setType(String type) {
		String oldtype = this.type;
		this.type = type;
		support.firePropertyChange("type", oldtype, type);
		entry.set((Integer) FIELD_LIST.get("type")[0], type);
	}

	public void setCccKey(String cccKey) {
		String oldcccKey = this.cccKey;
		this.cccKey = cccKey;
		support.firePropertyChange("cccKey", oldcccKey, cccKey);
		entry.set((Integer) FIELD_LIST.get("cccKey")[0], cccKey);
	}

	public void setAcademicYear(String academicYear) {
		String oldacademicYear = this.academicYear;
		this.academicYear = academicYear;
		support.firePropertyChange("academicYear", oldacademicYear,
				academicYear);
		entry.set((Integer) FIELD_LIST.get("academicYear")[0], academicYear);
	}

}
