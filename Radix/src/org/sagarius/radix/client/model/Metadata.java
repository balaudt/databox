package org.sagarius.radix.client.model;

import java.util.Date;
import java.util.Map;

import org.sagarius.radix.client.collect.Maps;
import org.sagarius.radix.client.model.util.BaseEntry;

/**
 * Client model
 * <ul>
 * <li>Model-UI binding</li>
 * <li>Direct JSON evaluation for communication</li>
 * </ul>
 * 
 * @author bala
 * 
 */
public class Metadata extends BaseEntity {

	public Metadata() {
		super(BaseEntry.create());
	}

	public Metadata(BaseEntry entry) {
		super(entry);
		init();
	}

	public Metadata(String json) {
		super(BaseEntry.create(json));
		init();
	}

	public void init() {
		creator = entry.get((Integer) FIELD_LIST.get("creator")[0],
				String.class);
		published = entry.get((Integer) FIELD_LIST.get("published")[0],
				Double.class);
		editor = entry.get((Integer) FIELD_LIST.get("editor")[0], String.class);
		timestamp = entry.get((Integer) FIELD_LIST.get("timestamp")[0],
				Double.class);
		version = entry.get((Integer) FIELD_LIST.get("version")[0],
				String.class);
		schoolId = entry.get((Integer) FIELD_LIST.get("schoolId")[0],
				String.class);
		status = entry.get((Integer) FIELD_LIST.get("status")[0], String.class);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String creator;
	private Double published;
	private String editor;
	private Double timestamp;
	private String version;
	private String schoolId;
	private String status;

	public String getCreator() {
		return creator;
	}

	public Date getPublished() {
		if (published == null) {
			return null;
		}
		return new Date(published.longValue());
	}

	public String getEditor() {
		return editor;
	}

	public Date getTimestamp() {
		if (timestamp == null) {
			return null;
		}
		return new Date(timestamp.longValue());
	}

	public String getVersion() {
		return version;
	}

	public String getSchoolId() {
		return schoolId;
	}

	public String getStatus() {
		return status;
	}

	public static final Map<String, Object[]> FIELD_LIST = new Maps.ImmutableHashMap<String, Object[]>()
			.put("creator", new Object[] { 0, "Created by" })
			.put("published", new Object[] { 1, "Published @" })
			.put("editor", new Object[] { 2, "Last edited by" })
			.put("timestamp", new Object[] { 3, "Last edited @" })
			.put("version", new Object[] { 4, "Version" })
			.put("schoolId", new Object[] { 5, "School" })
			.put("status", new Object[] { 6, "Status" }).build();

	public void setCreator(String creator) {
		String oldCreator = this.creator;
		this.creator = creator;
		support.firePropertyChange("creator", oldCreator, creator);
		entry.set((Integer) FIELD_LIST.get("creator")[0], creator);
	}

	public void setPublished(double published) {
		double oldPublished = this.published;
		this.published = published;
		support.firePropertyChange("published", oldPublished, published);
		entry.set((Integer) FIELD_LIST.get("published")[0], published);
	}

	public void setEditor(String editor) {
		String oldEditor = this.editor;
		this.editor = editor;
		support.firePropertyChange("editor", oldEditor, editor);
		entry.set((Integer) FIELD_LIST.get("editor")[0], editor);
	}

	public void setTimestamp(double timestamp) {
		double oldTimestamp = this.timestamp;
		this.timestamp = timestamp;
		support.firePropertyChange("timestamp", oldTimestamp, timestamp);
		entry.set((Integer) FIELD_LIST.get("timestamp")[0], timestamp);
	}

	public void setVersion(String version) {
		String oldVersion = this.version;
		this.version = version;
		support.firePropertyChange("version", oldVersion, version);
		entry.set((Integer) FIELD_LIST.get("version")[0], version);
	}

	public void setSchoolId(String schoolId) {
		String oldSchoolId = this.schoolId;
		this.schoolId = schoolId;
		support.firePropertyChange("schoolId", oldSchoolId, schoolId);
		entry.set((Integer) FIELD_LIST.get("schoolId")[0], schoolId);
	}

	public void setStatus(String status) {
		String oldStatus = this.status;
		this.status = status;
		support.firePropertyChange("status", oldStatus, status);
		entry.set((Integer) FIELD_LIST.get("status")[0], status);
	}

	@Override
	public String toString() {
		return "Metadata [creator=" + creator + ", published=" + published
				+ ", editor=" + editor + ", timestamp=" + timestamp
				+ ", version=" + version + ", schoolId=" + schoolId
				+ ", status=" + status + "]";
	}

}
