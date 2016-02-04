package com.sagarius.goddess.server.resources.mapreduce;

import java.net.URL;
import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.text.StrBuilder;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.gdata.client.spreadsheet.CellQuery;
import com.google.gdata.client.spreadsheet.ListQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.client.spreadsheet.WorksheetQuery;
import com.google.gdata.data.Link;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.CustomElementCollection;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.sagarius.goddess.server.Utils;

public class ConsolidateAttendGen extends ServerResource {
	public ConsolidateAttendGen() {
		getVariants().add(new Variant(MediaType.ALL));
	}

	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		try {
			String jsonString = entity.getText();
			JSONObject jsonObject = (JSONObject) JSONValue.parse(jsonString);
			String clazz = (String) jsonObject.get("clazz");
			String googleToken = (String) jsonObject.get("token");
			URL studentListUrl = new URL(
					(String) jsonObject.get("studentListUrl"));
			URL classAttendUrl = new URL((String) jsonObject.get("docWorkUrl"));
			int year = new GregorianCalendar().get(Calendar.YEAR);

			SpreadsheetService service = new SpreadsheetService("Goddess");
			service.setProtocolVersion(SpreadsheetService.Versions.V1);
			service.setAuthSubToken(googleToken);

			String[] stdSec = clazz.split("-");
			String standard = stdSec[0];
			String section = stdSec[1];
			ListQuery listQuery = new ListQuery(studentListUrl);
			listQuery.setSpreadsheetQuery("standard=\"" + standard
					+ "\" and section=\"" + section + "\"");
			ListFeed studFeed = service.getFeed(listQuery, ListFeed.class);
			List<ListEntry> studEntries = studFeed.getEntries();
			int strength = studEntries.size();

			WorksheetQuery consolidatedQuery = new WorksheetQuery(
					classAttendUrl);
			consolidatedQuery.setTitleExact(true);
			consolidatedQuery.setTitleQuery("Consolidated");
			WorksheetEntry consolidatedEntry = service
					.getFeed(consolidatedQuery, WorksheetFeed.class)
					.getEntries().get(0);

			String[] inMonths = new DateFormatSymbols().getMonths();
			String[] months = Arrays.copyOf(inMonths, 12);
			final int rowCount = strength + 5;
			consolidatedEntry.setRowCount(rowCount);
			final int colCount = months.length + 3;
			consolidatedEntry.setColCount(colCount);
			consolidatedEntry.update();

			URL mainCellUrl = consolidatedEntry.getCellFeedUrl();
			CellQuery inCellQuery = new CellQuery(mainCellUrl);
			inCellQuery.setReturnEmpty(true);
			CellFeed inFeed = service.getFeed(inCellQuery, CellFeed.class);
			CellFeed outFeed = new CellFeed();
			List<CellEntry> inEntries = inFeed.getEntries();

			Utils.helpBatch(inEntries.get(0), outFeed, "Student id");
			Utils.helpBatch(inEntries.get(1), outFeed, "Name");
			Utils.helpBatch(inEntries.get(colCount - 1), outFeed, "Total");
			Utils.helpBatch(inEntries.get(colCount), outFeed, "Working days");
			Utils.helpBatch(inEntries.get(colCount * 2 - 1), outFeed,
					"=SUM(C2:N2)");
			Utils.helpBatch(inEntries.get(colCount * 2), outFeed, "Holidays");
			Utils.helpBatch(inEntries.get(colCount * 3 - 1), outFeed,
					"=SUM(C3:N3)");
			Utils.helpBatch(inEntries.get(colCount * 3), outFeed, "Total");
			Utils.helpBatch(inEntries.get(colCount * 4 - 1), outFeed,
					"=SUM(C4:N4)");
			Utils.helpBatch(inEntries.get((rowCount - 1) * colCount), outFeed,
					"Average");

			int i;

			final GregorianCalendar calendar = new GregorianCalendar();
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.DATE, 1);

			String workingDays = new StrBuilder("='@'!").append("#")
					.append("3").toString();
			String totalRow = "=@2+@3";
			String averageString = new StrBuilder("=IF(COUNT(@5:@")
					.append(strength + 4).append(")>0,AVERAGE(@5:@")
					.append(strength + 4).append("),\"\")").toString();
			for (i = 0; i < months.length; i++) {
				calendar.set(Calendar.MONTH, i);
				int maxDate = calendar.getActualMaximum(Calendar.DATE);
				int noOfCol = maxDate + 4;
				final String presentCol = Utils.getColumn(noOfCol - 1, 0);
				final String absentCol = Utils.getColumn(noOfCol, 0);
				final String column = Utils.getColumn(i + 3, 0);

				Utils.helpBatch(inEntries.get(i + 2), outFeed, months[i]);
				Utils.helpBatch(
						inEntries.get(colCount + i + 2),
						outFeed,
						workingDays.replaceAll("\\@", months[i]).replaceAll(
								"#", presentCol));
				Utils.helpBatch(
						inEntries.get(colCount * 2 + i + 2),
						outFeed,
						workingDays.replaceAll("\\@", months[i]).replaceAll(
								"#", absentCol));
				Utils.helpBatch(inEntries.get(colCount * 3 + i + 2), outFeed,
						totalRow.replaceAll("\\@", "" + column));
				Utils.helpBatch(
						inEntries.get(colCount * (rowCount - 1) + i + 2),
						outFeed, averageString.replaceAll("\\@", column));
			}
			final String column = Utils.getColumn(i + 3, 0);
			Utils.helpBatch(inEntries.get(colCount * (rowCount - 1) + i + 2),
					outFeed, averageString.replaceAll("\\@", column));

			String studentPresentDays = new StrBuilder("='@'!").append("#")
					.append("|").toString();
			String totalString = "=SUM(C@:N@)";
			for (i = 0; i < strength; i++) {
				CustomElementCollection elements = studEntries.get(i)
						.getCustomElements();
				Utils.helpBatch(inEntries.get(colCount * (i + 4)), outFeed,
						"=\"" + elements.getValue("studentid") + "\"");
				Utils.helpBatch(inEntries.get(colCount * (i + 4) + 1), outFeed,
						elements.getValue("name"));
				for (int j = 0; j < months.length; j++) {
					calendar.set(Calendar.MONTH, j);
					int maxDate = calendar.getActualMaximum(Calendar.DATE);
					int noOfCol = maxDate + 4;
					final String presentCol = Utils.getColumn(noOfCol - 1, 0);

					Utils.helpBatch(
							inEntries.get(colCount * (i + 4) + j + 2),
							outFeed,
							studentPresentDays.replaceAll("\\@", months[j])
									.replace("#", presentCol)
									.replace("|", (i + 4) + ""));
				}
				Utils.helpBatch(inEntries.get(colCount * (i + 5) - 1), outFeed,
						totalString.replaceAll("\\@", (i + 5) + ""));
			}

			Utils.logBatchStatus(service.batch(
					new URL(inFeed.getLink(Link.Rel.FEED_BATCH, Link.Type.ATOM)
							.getHref()), outFeed));
		} catch (Exception e) {
			e.printStackTrace();
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage());
		}
		return new EmptyRepresentation();
	}
}
