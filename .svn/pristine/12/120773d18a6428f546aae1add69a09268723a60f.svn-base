package com.sagarius.goddess.server.resources.mapreduce;

import java.net.URL;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.common.collect.LinkedListMultimap;
import com.google.gdata.client.spreadsheet.CellQuery;
import com.google.gdata.client.spreadsheet.ListQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.Link;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.CustomElementCollection;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.sagarius.goddess.client.utils.Fields.Staff;
import com.sagarius.goddess.server.Utils;

public class ConsolidateResource extends ServerResource {
	public ConsolidateResource() {
		getVariants().add(new Variant(MediaType.ALL));
	}

	@Override
	protected Representation put(Representation representation, Variant variant)
			throws ResourceException {
		try {
			String repText = representation.getText();
			System.out.println(repText);
			JSONObject input = (JSONObject) new JSONParser().parse(repText);
			String token = (String) input.get("token");
			String consolidateUrl = (String) input.get("consolidateUrl");
			String cssUrl = (String) input.get("cssUrl");
			String clazzName = (String) input.get("clazz");
			String standard = clazzName.split("-")[0];
			String section = clazzName.split("-")[1];
			int startIndex = ((Long) input.get("startIndex")).intValue();
			int endIndex = ((Long) input.get("endIndex")).intValue();

			SpreadsheetService service = new SpreadsheetService("Goddess");
			service.setProtocolVersion(SpreadsheetService.Versions.V1);
			service.setAuthSubToken(token);

			WorksheetEntry cssEntry = service.getEntry(new URL(cssUrl),
					WorksheetEntry.class);
			URL cssListUrl = cssEntry.getListFeedUrl();
			ListQuery cssListQuery = new ListQuery(cssListUrl);
			cssListQuery.setSpreadsheetQuery("standard=\"" + standard
					+ "\" and section=\"" + section + "\"");
			List<ListEntry> cssEntries = service.getFeed(cssListQuery,
					ListFeed.class).getEntries();
			LinkedListMultimap<String, String> subjectMap = LinkedListMultimap
					.create();
			for (ListEntry cssListEntry : cssEntries) {
				CustomElementCollection elements = cssListEntry
						.getCustomElements();
				String subject = elements.getValue("subject");
				String staffId = elements.getValue(Staff.STAFF_ID);
				subjectMap.put(subject, staffId);
			}

			int noOfRows = endIndex - startIndex + 1;
			WorksheetEntry consolidateEntry = service.getEntry(new URL(
					consolidateUrl), WorksheetEntry.class);
			URL cellUrl = consolidateEntry.getCellFeedUrl();
			CellQuery cellQuery = new CellQuery(cellUrl);
			cellQuery.setMinimumCol(1);
			cellQuery.setMaximumCol(2);
			cellQuery.setMinimumRow(startIndex);
			cellQuery.setMaximumRow(endIndex);
			cellQuery.setReturnEmpty(true);
			CellFeed inFeed = service.getFeed(cellQuery, CellFeed.class);
			List<CellEntry> inEntries = inFeed.getEntries();
			CellFeed outFeed = new CellFeed();
			for (int i = 0; i < noOfRows; i++) {
				Utils.helpBatch(inEntries.get(i * 2), outFeed, standard);
				Utils.helpBatch(inEntries.get(i * 2 + 1), outFeed, section);
			}
			service.batch(
					new URL(inFeed.getLink(Link.Rel.FEED_BATCH, Link.Type.ATOM)
							.getHref()), outFeed);

			cellQuery.setMinimumCol(3);
			cellQuery.setMaximumCol(6);
			inFeed = service.getFeed(cellQuery, CellFeed.class);
			inEntries = inFeed.getEntries();
			String[][] subjects = new String[noOfRows][4];
			for (int i = 0; i < noOfRows; i++) {
				for (int j = 0; j < 4; j++) {
					subjects[i][j] = inEntries.get(i * 4 + j).getCell()
							.getValue();
				}
			}

			cellQuery.setMinimumCol(7);
			cellQuery.setMaximumCol(7);
			inFeed = service.getFeed(cellQuery, CellFeed.class);
			inEntries = inFeed.getEntries();
			outFeed = new CellFeed();
			for (int i = 0; i < noOfRows; i++) {
				String subject = null;
				for (int j = 3; j >= 0; j--) {
					if (subjects[i][j] != null) {
						subject = subjects[i][j];
						break;
					}
				}
				List<String> staffs = subjectMap.get(subject);
				if (staffs != null && staffs.size() > 0) {
					String staffId = staffs.get(0);
					Utils.helpBatch(inEntries.get(i), outFeed, staffId);
				}
			}
			service.batch(
					new URL(inFeed.getLink(Link.Rel.FEED_BATCH, Link.Type.ATOM)
							.getHref()), outFeed);
		} catch (Exception e) {
			e.printStackTrace();
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage());
		}
		getResponse().setStatus(Status.SUCCESS_OK);
		return new EmptyRepresentation();
	}
}
