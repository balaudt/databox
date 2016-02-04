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

import com.google.gdata.client.spreadsheet.CellQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.Link;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.sagarius.goddess.server.Utils;

public class StudentUpdateResource extends ServerResource {
	public StudentUpdateResource() {
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
			String workUrl = (String) input.get("workEntry");
			SpreadsheetService service = new SpreadsheetService("Goddess");
			service.setProtocolVersion(SpreadsheetService.Versions.V1);
			service.setAuthSubToken(token);

			WorksheetEntry workEntry = service.getEntry(new URL(workUrl),
					WorksheetEntry.class);
			int startIndex = ((Number) input.get("startIndex")).intValue();
			int startCol = ((Number) input.get("startCol")).intValue();
			int endIndex = ((Number) input.get("endIndex")).intValue();
			int noOfStuds = endIndex - startIndex + 1;

			CellQuery cellQuery = new CellQuery(workEntry.getCellFeedUrl());
			cellQuery.setReturnEmpty(true);
			cellQuery.setMinimumRow(startIndex);
			cellQuery.setMaximumRow(endIndex);
			cellQuery.setMinimumCol(startCol);
			cellQuery.setMaximumCol(startCol + 3);
			String passFormula = "=".concat((String) input.get("pass"))
					.replaceAll("'", "\"");
			String statusFormula = "=".concat((String) input.get("status"))
					.replaceAll("'", "\"");
			String percentageFormula = "=".concat(
					(String) input.get("percentage")).replaceAll("'", "\"");
			String rankFormula = "=".concat((String) input.get("rank"))
					.replaceAll("'", "\"");
			CellFeed inFeed = service.getFeed(cellQuery, CellFeed.class);
			CellFeed outFeed = new CellFeed();
			List<CellEntry> inEntries = inFeed.getEntries();

			int noOfCons = 4;
			for (int i = 0; i < noOfStuds; i++) {
				Utils.helpBatch(inEntries.get(i * noOfCons), outFeed,
						percentageFormula
								.replaceAll("i", (i + startIndex) + ""));
				Utils.helpBatch(inEntries.get(i * noOfCons + 1), outFeed,
						rankFormula.replaceAll("i", (i + startIndex) + ""));
				Utils.helpBatch(inEntries.get(i * noOfCons + 2), outFeed,
						statusFormula.replaceAll("i", (i + startIndex) + ""));
				Utils.helpBatch(inEntries.get(i * noOfCons + 3), outFeed,
						passFormula.replaceAll("i", (i + startIndex) + ""));
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
