package com.sagarius.goddess.server.resources;

import java.net.URL;
import java.util.List;

import org.json.simple.JSONArray;
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

public class ClassUpdateResource extends ServerResource {
	public ClassUpdateResource() {
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
			URL cellUrl = workEntry.getCellFeedUrl();
			int startIndex = ((Number) input.get("startIndex")).intValue();
			int endIndex = ((Number) input.get("endIndex")).intValue();
			int startRow = ((Number) input.get("startRow")).intValue();
			int noOfSubjs = endIndex - startIndex + 1;
			JSONArray colInputs = (JSONArray) input.get("bcols");
			String highestFormula = "=".concat((String) input.get("highest"))
					.replaceAll("'", "\"");
			String averageFormula = "=".concat((String) input.get("average"))
					.replaceAll("'", "\"");
			String passFormula = "=".concat((String) input.get("pass"))
					.replaceAll("'", "\"");
			String failFormula = "=".concat((String) input.get("fail"))
					.replaceAll("'", "\"");
			String absentFormula = "=".concat((String) input.get("absent"))
					.replaceAll("'", "\"");
			String ninetyFormula = "=".concat((String) input.get("ninety"))
					.replaceAll("'", "\"");
			String attendFormula = "=".concat((String) input.get("attend"))
					.replaceAll("'", "\"");
			String bpassFormula = "=".concat((String) input.get("bpass"))
					.replaceAll("'", "\"");
			String bfailFormula = "=".concat((String) input.get("bfail"))
					.replaceAll("'", "\"");
			CellQuery cellQuery = new CellQuery(cellUrl);
			cellQuery.setReturnEmpty(true);
			cellQuery.setMinimumCol(startIndex);
			cellQuery.setMaximumCol(endIndex);
			cellQuery.setMinimumRow(startRow);
			cellQuery.setMaximumRow(startRow + 6);
			CellFeed inFeed = service.getFeed(cellQuery, CellFeed.class);
			CellFeed outFeed = new CellFeed();
			List<CellEntry> inEntries = inFeed.getEntries();

			for (int i = 0; i < noOfSubjs; i++) {
				String colString = Utils.getColString(i + startIndex - 1);
				Utils.helpBatch(inEntries.get(i), outFeed,
						highestFormula.replaceAll("i", colString));
				Utils.helpBatch(inEntries.get(i + noOfSubjs), outFeed,
						averageFormula.replaceAll("i", colString));
				if (colInputs.contains(colString)) {
					Utils.helpBatch(inEntries.get(i + (noOfSubjs * 2)),
							outFeed, bpassFormula.replaceAll("i", colString));
					Utils.helpBatch(inEntries.get(i + (noOfSubjs * 3)),
							outFeed, bfailFormula.replaceAll("i", colString));
				} else {
					Utils.helpBatch(inEntries.get(i + (noOfSubjs * 2)),
							outFeed, passFormula.replaceAll("i", colString));
					Utils.helpBatch(inEntries.get(i + (noOfSubjs * 3)),
							outFeed, failFormula.replaceAll("i", colString));
				}
				Utils.helpBatch(inEntries.get(i + (noOfSubjs * 4)), outFeed,
						absentFormula.replaceAll("i", colString));
				Utils.helpBatch(inEntries.get(i + (noOfSubjs * 5)), outFeed,
						ninetyFormula.replaceAll("i", colString));
				Utils.helpBatch(inEntries.get(i + (noOfSubjs * 6)), outFeed,
						attendFormula.replaceAll("i", colString));
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
