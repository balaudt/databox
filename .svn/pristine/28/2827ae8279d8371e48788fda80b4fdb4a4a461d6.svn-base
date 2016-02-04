package com.sagarius.goddess.server.resources.mapreduce;

import java.net.URL;
import java.text.SimpleDateFormat;
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
import com.google.gdata.data.Link;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.CustomElementCollection;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.sagarius.goddess.server.Utils;

public class NewMonthAttendanceGen extends ServerResource {
	public NewMonthAttendanceGen() {
		getVariants().add(new Variant(MediaType.ALL));
	}

	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		try {
			String jsonString = entity.getText();
			JSONObject jsonObject = (JSONObject) JSONValue.parse(jsonString);

			SpreadsheetService service = new SpreadsheetService("Goddess");
			service.setProtocolVersion(SpreadsheetService.Versions.V1);
			String token = (String) jsonObject.get("token");
			service.setAuthSubToken(token);

			URL docWorkUrl = new URL((String) jsonObject.get("docWorkUrl"));
			URL studentListUrl = new URL(
					(String) jsonObject.get("studentListUrl"));
			String clazz = (String) jsonObject.get("clazz");

			String[] stdSec = clazz.split("-");
			String standard = stdSec[0];
			String section = stdSec[1];
			ListQuery listQuery = new ListQuery(studentListUrl);
			listQuery.setSpreadsheetQuery("standard=\"" + standard
					+ "\" and section=\"" + section + "\"");
			ListFeed studFeed = service.getFeed(listQuery, ListFeed.class);
			List<ListEntry> studEntries = studFeed.getEntries();
			int strength = studEntries.size();

			int year = ((Long) jsonObject.get("year")).intValue();
			int month = ((Long) jsonObject.get("month")).intValue();
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, month);
			int maxDate = calendar.getActualMaximum(Calendar.DATE);
			int noOfRows = strength + 6;
			int noOfCol = maxDate + 4;

			WorksheetEntry newEntry = new WorksheetEntry();
			String title = new SimpleDateFormat("MMMM").format(calendar
					.getTime());
			newEntry.setTitle(new PlainTextConstruct(title));
			newEntry.setRowCount(noOfRows);
			newEntry.setColCount(noOfCol);
			newEntry = service.insert(docWorkUrl, newEntry);
			URL inFeedUrl = newEntry.getCellFeedUrl();
			CellQuery inQuery = new CellQuery(inFeedUrl);

			CellFeed outFeed = new CellFeed();
			final String lastDateCol = Utils.getColumn(noOfCol - 2, 0);
			final String presentCol = Utils.getColumn(noOfCol - 1, 0);
			final String absentCol = Utils.getColumn(noOfCol, 0);
			final int presentRow = noOfRows - 2;
			final int absentRow = noOfRows - 1;
			final int lastStudRow = noOfRows - 3;

			// Headers change
			inQuery.setMinimumCol(1);
			inQuery.setMaximumCol(noOfCol);
			inQuery.setMinimumRow(1);
			inQuery.setMaximumRow(3);
			inQuery.setReturnEmpty(true);
			CellFeed inFeed = service.getFeed(inQuery, CellFeed.class);
			List<CellEntry> inEntries = inFeed.getEntries();
			Utils.helpBatch(inEntries.get(0), outFeed, "Student id");
			Utils.helpBatch(inEntries.get(1), outFeed, "Name");
			Utils.helpBatch(inEntries.get(noOfCol - 2), outFeed,
					"No of days present");
			Utils.helpBatch(inEntries.get(noOfCol - 1), outFeed,
					"No of days absent");
			Utils.helpBatch(inEntries.get(noOfCol), outFeed, "Day");
			Utils.helpBatch(inEntries.get(noOfCol * 2), outFeed, "Status");
			// Total number of working days
			Utils.helpBatch(
					inEntries.get(noOfCol * 3 - 2),
					outFeed,
					new StrBuilder("=COUNTIF(C3:").append(lastDateCol)
							.append("3,\"=HW\")*.5+COUNTIF(C3:")
							.append(lastDateCol).append("3,\"=W\")").toString());
			// Total number of holidays
			Utils.helpBatch(
					inEntries.get(noOfCol * 3 - 1),
					outFeed,
					new StrBuilder("=").append(maxDate).append("-")
							.append(presentCol).append("3").toString());
			SimpleDateFormat dayFormatter = new SimpleDateFormat("EE");
			int i = 0;
			for (i = 0; i < maxDate; i++) {
				Utils.helpBatch(inEntries.get(i + 2), outFeed, (i + 1) + "");
				calendar.set(Calendar.DATE, i + 1);
				Utils.helpBatch(inEntries.get(noOfCol + i + 2), outFeed,
						dayFormatter.format(calendar.getTime()));
			}
			Utils.logBatchStatus(service.batch(
					new URL(inFeed.getLink(Link.Rel.FEED_BATCH, Link.Type.ATOM)
							.getHref()), outFeed));

			// Student ids and names
			inQuery.setMinimumCol(1);
			inQuery.setMaximumCol(2);
			inQuery.setMinimumRow(4);
			inQuery.setMaximumRow(4 + strength);
			inFeed = service.getFeed(inQuery, CellFeed.class);
			inEntries = inFeed.getEntries();
			outFeed = new CellFeed();
			for (i = 0; i < strength; i++) {
				CustomElementCollection elements = studEntries.get(i)
						.getCustomElements();
				Utils.helpBatch(inEntries.get(2 * i), outFeed,
						"=\"" + elements.getValue("studentid") + "\"");
				Utils.helpBatch(inEntries.get(2 * i + 1), outFeed, "=\""
						+ elements.getValue("name") + "\"");
			}
			Utils.logBatchStatus(service.batch(
					new URL(inFeed.getLink(Link.Rel.FEED_BATCH, Link.Type.ATOM)
							.getHref()), outFeed));

			// Student consolidations
			inQuery.setMinimumCol(noOfCol - 1);
			inQuery.setMaximumCol(noOfCol);
			inQuery.setMinimumRow(4);
			inQuery.setMaximumRow(4 + strength);
			inFeed = service.getFeed(inQuery, CellFeed.class);
			inEntries = inFeed.getEntries();
			outFeed = new CellFeed();
			String absentString = new StrBuilder("=IF(").append(presentCol)
					.append("3>0,COUNTIF(FILTER(C@:").append(lastDateCol)
					.append("@").append(",C3:").append(lastDateCol)
					.append("3=\"W\"),\"=A\")+").append("COUNTIF(FILTER(C@:")
					.append(lastDateCol).append("@").append(",C3:")
					.append(lastDateCol).append("3=\"W\"),\"=H\")*.5+")
					.append("COUNTIF(FILTER(C@:").append(lastDateCol)
					.append("@").append(",C3:").append(lastDateCol)
					.append("3=\"HW\"),\"=H\")*.5,\"\")").toString();
			String presentString = new StrBuilder("=IF(").append(presentCol)
					.append("3>0,").append(presentCol).append("3-")
					.append(absentCol).append("@,\"\")").toString();
			for (i = 0; i < strength; i++) {
				// No of days absent
				Utils.helpBatch(inEntries.get(2 * i + 1), outFeed,
						absentString.replaceAll("\\@", (i + 4) + ""));
				// No of days present
				Utils.helpBatch(inEntries.get(2 * i), outFeed,
						presentString.replaceAll("\\@", (i + 4) + ""));
			}
			Utils.logBatchStatus(service.batch(
					new URL(inFeed.getLink(Link.Rel.FEED_BATCH, Link.Type.ATOM)
							.getHref()), outFeed));

			// Footers
			inQuery.setMinimumCol(1);
			inQuery.setMaximumCol(noOfCol);
			inQuery.setMinimumRow(noOfRows - 2);
			inQuery.setMaximumRow(noOfRows);
			inFeed = service.getFeed(inQuery, CellFeed.class);
			inEntries = inFeed.getEntries();
			outFeed = new CellFeed();
			Utils.helpBatch(inEntries.get(0), outFeed, "No present");
			Utils.helpBatch(inEntries.get(noOfCol), outFeed, "No absent");
			Utils.helpBatch(inEntries.get(2 * noOfCol), outFeed, "Present %");
			String absentRowString = new StrBuilder(
					"=IF(OR(@3=\"W\",@3=\"HW\"),COUNTIF(@4:@")
					.append(lastStudRow).append(",\"=A\")+")
					.append("COUNTIF(@4:@").append(lastStudRow)
					.append(",\"=H\")*.5,\"\")").toString();
			String presentRowString = new StrBuilder("=IF(@").append(absentRow)
					.append("=\"\",\"\",").append(strength).append("-@")
					.append(absentRow).append(")").toString();
			String percentageRowString = new StrBuilder("=IF(@")
					.append(presentRow).append("=\"\",\"\",").append("@")
					.append(presentRow).append("/").append(strength)
					.append(")").toString();
			for (i = 0; i < maxDate; i++) {
				Utils.helpBatch(
						inEntries.get(noOfCol + i + 2),
						outFeed,
						absentRowString.replaceAll("\\@",
								Utils.getColumn(i + 3, 0)));
				Utils.helpBatch(
						inEntries.get(i + 2),
						outFeed,
						presentRowString.replaceAll("\\@",
								Utils.getColumn(i + 3, 0)));
				Utils.helpBatch(
						inEntries.get(noOfCol * 2 + i + 2),
						outFeed,
						percentageRowString.replaceAll("\\@",
								Utils.getColumn(i + 3, 0)));
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
