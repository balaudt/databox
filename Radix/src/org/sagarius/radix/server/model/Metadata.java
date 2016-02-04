package org.sagarius.radix.server.model;

import java.util.Date;

import javax.jdo.annotations.EmbeddedOnly;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.sagarius.radix.client.model.enumerations.Status;

import com.google.appengine.api.datastore.Key;

/**
 * Server model
 * <ul>
 * <li>Persistence</li>
 * <li>Direct JSON evaluation</li>
 * <ul>
 * 
 * @author bala
 * 
 */
@PersistenceCapable
@EmbeddedOnly
public class Metadata extends BaseEntity {
	public Metadata() {
		this(null);
	}

	public Metadata(String json) {
		super(org.sagarius.radix.client.model.Metadata.FIELD_LIST);
		if (json != null) {
			parse(json, this.getClass());
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Persistent
	private Key creator;
	@Persistent
	private Date published;
	@Persistent
	private Key editor;
	@Persistent
	private Date timestamp;
	@Persistent
	private String version;
	@Persistent
	private Key schoolId;
	@Persistent
	private Status status;

	public Key getCreator() {
		return creator;
	}

	public void setCreator(Key creator) {
		this.creator = creator;
	}

	public Date getPublished() {
		return published;
	}

	public void setPublished(Date published) {
		this.published = published;
	}

	public Key getEditor() {
		return editor;
	}

	public void setEditor(Key editor) {
		this.editor = editor;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Key getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(Key schoolId) {
		this.schoolId = schoolId;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Metadata [creator=" + creator + ", published=" + published
				+ ", editor=" + editor + ", timestamp=" + timestamp
				+ ", version=" + version + ", schoolId=" + schoolId
				+ ", status=" + status + "]";
	}

	public String render() {
		return render(this.getClass());
	}

	@Override
	public Metadata getMeta() {
		return null;
	}

	@Override
	public void setMeta(Metadata metadata) {

	}
}
