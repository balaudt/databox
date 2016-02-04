package com.sagarius.goddess.server.resources.base;

import java.net.URL;
import java.util.List;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.acl.AclFeed;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.sagarius.goddess.client.model.enumerations.DocumentType;
import com.sagarius.goddess.server.Utils;
import com.sagarius.goddess.server.model.School;
import com.sagarius.goddess.server.util.PMFactory;

public class ACLResource extends ServerResource {
	public ACLResource() {
		service.setAuthSubToken(Utils.getGoogleToken());
		getVariants().add(new Variant(MediaType.APPLICATION_JSON));
	}

	private SpreadsheetService service = new SpreadsheetService("Goddess");
	private FeedURLFactory factory = FeedURLFactory.getDefault();

	/**
	 * Called in case of enabling access to all in staff list
	 */
	@SuppressWarnings("unused")
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		try {
			String folderId = PMFactory.getManager()
					.getObjectById(School.class, Utils.getSchoolId())
					.getFolderId();
			AclFeed aclFeed = Utils.getAclFeed(folderId);

			String staffKey = Utils.getSheetKeyByType(DocumentType.STAFF);
			URL listFeedUrl = service
					.getFeed(
							factory.getWorksheetFeedUrl(staffKey, "private",
									"values"), WorksheetFeed.class)
					.getEntries().get(0).getListFeedUrl();
			ListFeed staffFeed = service.getFeed(listFeedUrl, ListFeed.class);
			List<ListEntry> entries = staffFeed.getEntries();

			for (ListEntry entry : entries) {

			}
			return super.post(entity, variant);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Called in case of disabling access to all in staff list
	 */
	@Override
	protected Representation delete(Variant variant) throws ResourceException {
		// TODO Auto-generated method stub
		return super.delete(variant);
	}

	/**
	 * Called in case of force refresh of ACL file
	 */
	@Override
	protected Representation put(Representation representation, Variant variant)
			throws ResourceException {
		// TODO Auto-generated method stub
		return super.put(representation, variant);
	}

	/**
	 * Gets the acl status of staff list
	 */
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		// TODO Auto-generated method stub
		return super.get(variant);
	}
}
