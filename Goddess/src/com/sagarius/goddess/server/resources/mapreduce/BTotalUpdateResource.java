package com.sagarius.goddess.server.resources.mapreduce;

import java.net.URL;
import java.util.List;
import java.util.Set;

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

import com.google.gdata.client.spreadsheet.CellQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.Link;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.sagarius.goddess.server.Utils;

public class BTotalUpdateResource extends ServerResource {
	public BTotalUpdateResource() {
		getVariants().add(new Variant(MediaType.ALL));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Representation put(Representation representation, Variant variant)
			throws ResourceException {
		try {
			String repText = representation.getText();
			System.out.println(repText);
			JSONObject input = (JSONObject) new JSONParser().parse(repText);
			String token = (String) input.get("token");
			String workUrl = (String) input.get("workEntry");
			SpreadsheetService service = new SpreadsheetService("Goddess");
			service.setProtocolVersion(SpreadsheetService.Versions.V1);
			service.setAuthSubToken(token);

			WorksheetEntry workEntry = service.getEntry(new URL(workUrl),
					WorksheetEntry.class);
			int startIndex = ((Number) input.get("startIndex")).intValue() - 1;
			int endIndex = ((Number) input.get("endIndex")).intValue();
			CellQuery cellQuery = new CellQuery(workEntry.getCellFeedUrl());
			cellQuery.setReturnEmpty(true);
			cellQuery.setMinimumRow(startIndex);
			cellQuery.setMaximumRow(endIndex);
			int noOfStuds = endIndex - startIndex + 1;

			JSONObject colInputs = (JSONObject) input.get("columns");
			Set<String> columns = colInputs.keySet();
			CellFeed inFeed = null;
			CellFeed outFeed = null;
			List<CellEntry> inEntries = null;
			for (String column : columns) {
				// System.out.println("Manipulating column ".concat(column));
				int colIndex = Utils.getCol(column);
				cellQuery.setMinimumCol(colIndex);
				cellQuery.setMaximumCol(colIndex);
				inFeed = service.getFeed(cellQuery, CellFeed.class);
				inEntries = inFeed.getEntries();
				outFeed = new CellFeed();
				String formula = "=".concat((String) colInputs.get(column))
						.replaceAll("'", "\"");
				for (int i = 0; i < noOfStuds; i++) {
					Utils.helpBatch(inEntries.get(i), outFeed,
							formula.replaceAll("i", (i + startIndex) + ""));
				}
				service.batch(
						new URL(inFeed.getLink(Link.Rel.FEED_BATCH,
								Link.Type.ATOM).getHref()), outFeed);
			}
		} catch (Exception e) {
			e.printStackTrace();
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage());
		}
		getResponse().setStatus(Status.SUCCESS_OK);
		return new EmptyRepresentation();
	}
}
