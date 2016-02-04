package com.sagarius.goddess.server.resources.mapreduce;

import java.net.URL;
import java.util.List;

import org.apache.commons.lang.text.StrBuilder;
import org.apache.commons.lang.text.StrSubstitutor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.LinkedListMultimap;
import com.google.gdata.client.spreadsheet.CellQuery;
import com.google.gdata.client.spreadsheet.ListQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.Link;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.batch.BatchStatus;
import com.google.gdata.data.batch.BatchUtils;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.CustomElementCollection;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.sagarius.goddess.client.utils.Fields.Staff;
import com.sagarius.goddess.server.Utils;
import com.sagarius.goddess.server.resources.ExamResource;

public class QueuedExamResource extends ServerResource {
	public QueuedExamResource() {
		getVariants().add(new Variant(MediaType.ALL));
	}

	public static final int HEADER_ROW = 1;
	public static final int MINIMIM_MARKS_ROW = 2;
	public static final int MAXIMUM_MARKS_ROW = 3;
	public static final int FIRST_STUDENT_ROW = 4;

	public static final String LAST_STUDENT_ROW = "${last_stud}";
	public static final String HIGHEST_ROW = "${highest}";
	public static final String AVERAGE_ROW = "${average_row}";
	public static final String NO_OF_PASS_ROW = "${no_pass}";
	public static final String NO_OF_FAIL_ROW = "${no_fail}";
	public static final String NO_OF_ABS_ROW = "${no_abs}";
	public static final String ABOVE_90_ROW = "${above_90}";
	public static final String CURRENT_ROW = "${row}";

	public static final String LAST_STUDENT_ROW_STRING = "last_stud";
	public static final String HIGHEST_ROW_STRING = "highest";
	public static final String AVERAGE_ROW_STRING = "average_row";
	public static final String NO_OF_PASS_ROW_STRING = "no_pass";
	public static final String NO_OF_FAIL_ROW_STRING = "no_fail";
	public static final String NO_OF_ABS_ROW_STRING = "no_abs";
	public static final String ABOVE_90_ROW_STRING = "above_90";
	public static final String CURRENT_ROW_STRING = "row";

	public static final char ADMISSION_COLUMN = 'A';
	public static final char FIRST_SUBJECT_COLUMN = 'C';

	public static final String LAST_SUBJECT_COLUMN = "${last_subject}";
	public static final String TOTAL_COLUMN = "${total}";
	public static final String AVERAGE_COLUMN = "${average_col}";
	public static final String RANK_COLUMN = "${rank}";
	public static final String PASSED_COLUMN = "${passed}";
	public static final String CURRENT_COLUMN = "${col}";

	public static final String LAST_SUBJECT_COLUMN_STRING = "last_subject";
	public static final String TOTAL_COLUMN_STRING = "total";
	public static final String AVERAGE_COLUMN_STRING = "average_col";
	public static final String RANK_COLUMN_STRING = "rank";
	public static final String PASSED_COLUMN_STRING = "passed";
	public static final String CURRENT_COLUMN_STRING = "col";

	public static final String STRENGTH = "${strength}";
	public static final String STRENGTH_STRING = "strength";

	public static String getPassString(char lastSubj) {
		StrBuilder builder = new StrBuilder("=AND(");
		int subjCount = 0;
		for (char subj = FIRST_SUBJECT_COLUMN; subj <= lastSubj; subj++) {
			builder.append(subj + CURRENT_ROW + ">=" + subj + MINIMIM_MARKS_ROW
					+ ",");
			subjCount++;
		}
		builder.deleteCharAt(builder.length() - 1);
		builder.append(",COUNT(" + FIRST_SUBJECT_COLUMN + CURRENT_ROW + ":"
				+ lastSubj + CURRENT_ROW + ")=" + subjCount + ")");
		return builder.toString();
	}

	/**
	 * Generates report for a single class for an examination<br/>
	 * Thus can be
	 * <ul>
	 * <li>Can be invoked by the
	 * {@link ExamResource#put(Representation, Variant)}</li>
	 * <li>Called by the user directly in case generation is per class</li>
	 * </ul>
	 */
	@Override
	protected Representation put(Representation representation, Variant variant)
			throws ResourceException {
		try {
			String repText = representation.getText();
			System.out.println(repText);
			JSONObject mainMap = (JSONObject) new JSONParser().parse(repText);
			String token = (String) mainMap.get("token");
			init(token, (String) mainMap.get("workEntry"),
					(String) mainMap.get("consolidatedEntry"),
					(String) mainMap.get("cssEntry"));
		} catch (Exception e) {
			e.printStackTrace();
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage());
		}
		getResponse().setStatus(Status.SUCCESS_OK);
		return new EmptyRepresentation();
	}

	private void init(String token, String workEntryHref,
			String consolidateKey, String cssKey) throws Exception {
		SpreadsheetService service = new SpreadsheetService("Goddess");
		service.setAuthSubToken(token);
		service.setProtocolVersion(SpreadsheetService.Versions.V1);

		String passString = null;
		final String totalString = "=IF(" + PASSED_COLUMN + CURRENT_ROW
				+ ",SUM(" + FIRST_SUBJECT_COLUMN + CURRENT_ROW + ":"
				+ LAST_SUBJECT_COLUMN + CURRENT_ROW + "),\"-\")";
		final String averageString = "=IF(" + PASSED_COLUMN + CURRENT_ROW
				+ ",VALUE(FIXED(" + TOTAL_COLUMN + CURRENT_ROW + "/"
				+ TOTAL_COLUMN + MAXIMUM_MARKS_ROW + "*100,2,1)),\"-\")";
		final String rankString = "=IF(" + PASSED_COLUMN + CURRENT_ROW
				+ ",RANK(" + TOTAL_COLUMN + CURRENT_ROW + ",FILTER("
				+ TOTAL_COLUMN + FIRST_STUDENT_ROW + ":" + TOTAL_COLUMN
				+ LAST_STUDENT_ROW + "," + PASSED_COLUMN + FIRST_STUDENT_ROW
				+ ":" + PASSED_COLUMN + LAST_STUDENT_ROW + "),0),\"-\")";

		final String highest = "=MAX(" + CURRENT_COLUMN + FIRST_STUDENT_ROW
				+ ":" + CURRENT_COLUMN + LAST_STUDENT_ROW + ")";
		final String average = "=VALUE(FIXED(AVERAGE(" + CURRENT_COLUMN
				+ FIRST_STUDENT_ROW + ":" + CURRENT_COLUMN + LAST_STUDENT_ROW
				+ "),2,1))";
		final String noOfPass = "=COUNTIF(" + CURRENT_COLUMN
				+ FIRST_STUDENT_ROW + ":" + CURRENT_COLUMN + LAST_STUDENT_ROW
				+ ",\">=\"&" + CURRENT_COLUMN + MINIMIM_MARKS_ROW + ")";
		final String noOfFail = "=COUNTIF(" + CURRENT_COLUMN
				+ FIRST_STUDENT_ROW + ":" + CURRENT_COLUMN + LAST_STUDENT_ROW
				+ ",\"<\"&" + CURRENT_COLUMN + MINIMIM_MARKS_ROW + ")";
		final String noOfAbs = "=" + STRENGTH + "-COUNT(" + CURRENT_COLUMN
				+ FIRST_STUDENT_ROW + ":" + CURRENT_COLUMN + LAST_STUDENT_ROW
				+ ")";
		final String noAbove90 = "=COUNTIF(" + CURRENT_COLUMN
				+ FIRST_STUDENT_ROW + ":" + CURRENT_COLUMN + LAST_STUDENT_ROW
				+ ",\">=\"&" + CURRENT_COLUMN + MAXIMUM_MARKS_ROW + "*90/100)";
		final String totalPass = "=COUNTIF(" + PASSED_COLUMN
				+ FIRST_STUDENT_ROW + ":" + PASSED_COLUMN + LAST_STUDENT_ROW
				+ ",\"=TRUE\")";
		final String totalFail = "=COUNTIF(" + PASSED_COLUMN
				+ FIRST_STUDENT_ROW + ":" + PASSED_COLUMN + LAST_STUDENT_ROW
				+ ",\"=FALSE\")";

		// Initializing update for consolidate sheet
		WorksheetEntry workEntry = service.getEntry(new URL(workEntryHref),
				WorksheetEntry.class);
		String clazz = workEntry.getTitle().getPlainText();
		String standard = clazz.split("-")[0];
		String section = clazz.split("-")[1];
		WorksheetEntry consolidateEntry = service.getEntry(new URL(
				consolidateKey), WorksheetEntry.class);
		int consolidateRows = consolidateEntry.getRowCount();
		int strength = workEntry.getRowCount() - 9;
		int subjects = workEntry.getColCount() - 6;
		// Consolidation row count setting [separated from updation due to entry
		// version conflict because of multiple requests in queue]
		consolidateEntry.setRowCount(consolidateRows + subjects + 1);
		consolidateEntry = consolidateEntry.update();

		// Caching the CSS to populate the subject and staff details in
		// consolidate sheet
		WorksheetEntry cssEntry = service.getEntry(new URL(cssKey),
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
			CustomElementCollection elements = cssListEntry.getCustomElements();
			String subject = elements.getValue("subject");
			String staffId = elements.getValue(Staff.STAFF_ID);
			subjectMap.put(subject, staffId);
		}

		// The main class area to be updated initialization
		URL cellFeedUrl = workEntry.getCellFeedUrl();
		CellQuery query = new CellQuery(cellFeedUrl);
		query.setMinimumRow(1);
		query.setMinimumCol(1);
		int colCount = workEntry.getColCount();
		query.setMaximumCol(colCount);
		query.setMaximumRow(workEntry.getRowCount());
		query.setReturnEmpty(true);
		CellFeed inputFeed = service.getFeed(query, CellFeed.class);
		List<CellEntry> inEntries = inputFeed.getEntries();
		CellFeed outputFeed = new CellFeed();

		// Generating maximum total automatically necessary for use in other
		// formulae
		CellQuery totalUpdateQuery = new CellQuery(workEntry.getCellFeedUrl());
		totalUpdateQuery.setRange("" + (char) (FIRST_SUBJECT_COLUMN + subjects)
				+ MAXIMUM_MARKS_ROW);
		totalUpdateQuery.setReturnEmpty(true);
		CellEntry totalUpdateCell = service
				.getFeed(totalUpdateQuery, CellFeed.class).getEntries().get(0);
		totalUpdateCell.changeInputValueLocal("=SUM(" + FIRST_SUBJECT_COLUMN
				+ MAXIMUM_MARKS_ROW + ":"
				+ (char) (FIRST_SUBJECT_COLUMN + subjects - 1)
				+ MAXIMUM_MARKS_ROW + ")");
		totalUpdateCell.update();

		// Consolidation updation
		int noOfConCol = 12;
		URL consolidateCellUrl = consolidateEntry.getCellFeedUrl();
		CellQuery consolidateCellQuery = new CellQuery(consolidateCellUrl);
		consolidateCellQuery.setMinimumRow(consolidateRows + 1);
		consolidateCellQuery.setMaximumRow(consolidateRows + subjects + 1);
		consolidateCellQuery.setMinimumCol(1);
		consolidateCellQuery.setMaximumCol(noOfConCol);
		consolidateCellQuery.setReturnEmpty(true);
		CellFeed consolidateInFeed = service.getFeed(consolidateCellQuery,
				CellFeed.class);
		List<CellEntry> consolidateEntries = consolidateInFeed.getEntries();
		CellFeed consolidateOutFeed = new CellFeed();
		// noOfConCol - Number of columns in consolidated sheet
		int i;
		for (i = 0; i < subjects; i++) {
			Utils.helpBatch(consolidateEntries.get(i * noOfConCol),
					consolidateOutFeed, standard);
			Utils.helpBatch(consolidateEntries.get(i * noOfConCol + 1),
					consolidateOutFeed, section);
			String subject = inEntries.get(FIRST_SUBJECT_COLUMN - 'A' + i)
					.getCell().getValue();
			List<String> values = subjectMap.get(subject);
			String prefix = "='" + clazz + "'!"
					+ ((char) (FIRST_SUBJECT_COLUMN + i));
			Utils.helpBatch(consolidateEntries.get(i * noOfConCol + 2),
					consolidateOutFeed, prefix + HEADER_ROW);
			if (values != null && !values.isEmpty()) {
				Utils.helpBatch(consolidateEntries.get(i * noOfConCol + 3),
						consolidateOutFeed, values.get(0));
			}
			Utils.helpBatch(consolidateEntries.get(i * noOfConCol + 4),
					consolidateOutFeed, prefix + MINIMIM_MARKS_ROW);
			Utils.helpBatch(consolidateEntries.get(i * noOfConCol + 5),
					consolidateOutFeed, prefix + MAXIMUM_MARKS_ROW);
			Utils.helpBatch(consolidateEntries.get(i * noOfConCol + 6),
					consolidateOutFeed, prefix + (FIRST_STUDENT_ROW + strength));
			Utils.helpBatch(consolidateEntries.get(i * noOfConCol + 7),
					consolidateOutFeed, prefix
							+ (FIRST_STUDENT_ROW + strength + 1));
			Utils.helpBatch(consolidateEntries.get(i * noOfConCol + 8),
					consolidateOutFeed, prefix
							+ (FIRST_STUDENT_ROW + strength + 2));
			Utils.helpBatch(consolidateEntries.get(i * noOfConCol + 9),
					consolidateOutFeed, prefix
							+ (FIRST_STUDENT_ROW + strength + 3));
			Utils.helpBatch(consolidateEntries.get(i * noOfConCol + 10),
					consolidateOutFeed, prefix
							+ (FIRST_STUDENT_ROW + strength + 4));
			Utils.helpBatch(consolidateEntries.get(i * noOfConCol + 11),
					consolidateOutFeed, prefix
							+ (FIRST_STUDENT_ROW + strength + 5));
		}
		Utils.helpBatch(consolidateEntries.get(i * noOfConCol),
				consolidateOutFeed, standard);
		Utils.helpBatch(consolidateEntries.get(i * noOfConCol + 1),
				consolidateOutFeed, section);
		Utils.helpBatch(consolidateEntries.get(i * noOfConCol + 2),
				consolidateOutFeed, "Aggregate");
		char prefixTotalCol = (char) (FIRST_SUBJECT_COLUMN + i);
		String prefix = "='" + clazz + "'!" + prefixTotalCol;
		Utils.helpBatch(consolidateEntries.get(i * noOfConCol + 5),
				consolidateOutFeed, prefix + MAXIMUM_MARKS_ROW);
		Utils.helpBatch(consolidateEntries.get(i * noOfConCol + 6),
				consolidateOutFeed, prefix + (FIRST_STUDENT_ROW + strength));
		Utils.helpBatch(consolidateEntries.get(i * noOfConCol + 7),
				consolidateOutFeed,
				("='" + clazz + "'!" + ((char) (prefixTotalCol + 1)))
						+ (FIRST_STUDENT_ROW + strength));
		Utils.helpBatch(consolidateEntries.get(i * noOfConCol + 11),
				consolidateOutFeed, prefix + (FIRST_STUDENT_ROW + strength + 5));
		prefix = "='" + clazz + "'!" + ((char) (prefixTotalCol + 3));
		Utils.helpBatch(consolidateEntries.get(i * noOfConCol + 8),
				consolidateOutFeed, prefix + (FIRST_STUDENT_ROW + strength + 2));
		Utils.helpBatch(consolidateEntries.get(i * noOfConCol + 9),
				consolidateOutFeed, prefix + (FIRST_STUDENT_ROW + strength + 3));

		service.batch(
				new URL(consolidateInFeed.getLink(Link.Rel.FEED_BATCH,
						Link.Type.ATOM).getHref()), consolidateOutFeed);

		// Main area updation
		ImmutableMap<String, String> mainMap = new ImmutableMap.Builder<String, String>()
				.put(LAST_STUDENT_ROW_STRING,
						(FIRST_STUDENT_ROW + strength - 1) + "")
				.put(HIGHEST_ROW_STRING, "" + (FIRST_STUDENT_ROW + strength))
				.put(AVERAGE_ROW_STRING,
						"" + (FIRST_STUDENT_ROW + strength + 1))
				.put(NO_OF_PASS_ROW_STRING,
						"" + (FIRST_STUDENT_ROW + strength + 2))
				.put(NO_OF_FAIL_ROW_STRING,
						"" + (FIRST_STUDENT_ROW + strength + 3))
				.put(NO_OF_ABS_ROW_STRING,
						"" + (FIRST_STUDENT_ROW + strength + 4))
				.put(ABOVE_90_ROW_STRING,
						"" + (FIRST_STUDENT_ROW + strength + 5))
				.put(LAST_SUBJECT_COLUMN_STRING,
						"" + (char) (FIRST_SUBJECT_COLUMN + subjects - 1))
				.put(TOTAL_COLUMN_STRING,
						"" + (char) (FIRST_SUBJECT_COLUMN + subjects))
				.put(AVERAGE_COLUMN_STRING,
						"" + (char) (FIRST_SUBJECT_COLUMN + subjects + 1))
				.put(RANK_COLUMN_STRING,
						"" + (char) (FIRST_SUBJECT_COLUMN + subjects + 2))
				.put(PASSED_COLUMN_STRING,
						"" + (char) (FIRST_SUBJECT_COLUMN + subjects + 3))
				.put(STRENGTH_STRING, strength + "").build();

		for (int j = 0; j < strength; j++) {
			int rowNo = FIRST_STUDENT_ROW + j - 1;
			ImmutableMap<String, String> currentRowMap = new ImmutableMap.Builder<String, String>()
					.put(CURRENT_ROW_STRING, (rowNo + 1) + "").build();

			passString = getPassString(mainMap.get(LAST_SUBJECT_COLUMN_STRING)
					.charAt(0));
			int passCol = mainMap.get(PASSED_COLUMN_STRING).charAt(0) - 'A';
			CellEntry inEntry = inEntries.get(colCount * rowNo + passCol);
			Utils.helpBatch(inEntry, outputFeed, StrSubstitutor.replace(
					StrSubstitutor.replace(passString, mainMap), currentRowMap));

			int totalCol = mainMap.get(TOTAL_COLUMN_STRING).charAt(0) - 'A';
			inEntry = inEntries.get(colCount * rowNo + totalCol);
			Utils.helpBatch(inEntry, outputFeed,
					StrSubstitutor.replace(
							StrSubstitutor.replace(totalString, mainMap),
							currentRowMap));

			int averageCol = mainMap.get(AVERAGE_COLUMN_STRING).charAt(0) - 'A';
			inEntry = inEntries.get(colCount * rowNo + averageCol);
			Utils.helpBatch(inEntry, outputFeed, StrSubstitutor.replace(
					StrSubstitutor.replace(averageString, mainMap),
					currentRowMap));

			int rankCol = mainMap.get(RANK_COLUMN_STRING).charAt(0) - 'A';
			inEntry = inEntries.get(colCount * rowNo + rankCol);
			Utils.helpBatch(inEntry, outputFeed, StrSubstitutor.replace(
					StrSubstitutor.replace(rankString, mainMap), currentRowMap));
		}
		for (int j = 0; j < subjects + 2; j++) {
			int colNo = FIRST_SUBJECT_COLUMN + j - 'A';
			ImmutableMap<String, String> currentColMap = new ImmutableMap.Builder<String, String>()
					.put(CURRENT_COLUMN_STRING,
							"" + (char) (FIRST_SUBJECT_COLUMN + j)).build();

			int highestRow = Integer.parseInt(mainMap.get(HIGHEST_ROW_STRING)) - 1;
			CellEntry inEntry = inEntries.get(colCount * highestRow + colNo);
			Utils.helpBatch(inEntry, outputFeed, StrSubstitutor.replace(
					StrSubstitutor.replace(highest, mainMap), currentColMap));

			int averageRow = Integer.parseInt(mainMap.get(AVERAGE_ROW_STRING)) - 1;
			inEntry = inEntries.get(colCount * averageRow + colNo);
			Utils.helpBatch(inEntry, outputFeed, StrSubstitutor.replace(
					StrSubstitutor.replace(average, mainMap), currentColMap));

			int above90Row = Integer.parseInt(mainMap.get(ABOVE_90_ROW_STRING)) - 1;
			inEntry = inEntries.get(colCount * above90Row + colNo);
			Utils.helpBatch(inEntry, outputFeed, StrSubstitutor.replace(
					StrSubstitutor.replace(noAbove90, mainMap), currentColMap));

			if (j >= subjects) {
				continue;
			}

			int noPassRow = Integer
					.parseInt(mainMap.get(NO_OF_PASS_ROW_STRING)) - 1;
			inEntry = inEntries.get(colCount * noPassRow + colNo);
			Utils.helpBatch(inEntry, outputFeed, StrSubstitutor.replace(
					StrSubstitutor.replace(noOfPass, mainMap), currentColMap));

			int noFailRow = Integer
					.parseInt(mainMap.get(NO_OF_FAIL_ROW_STRING)) - 1;
			inEntry = inEntries.get(colCount * noFailRow + colNo);
			Utils.helpBatch(inEntry, outputFeed, StrSubstitutor.replace(
					StrSubstitutor.replace(noOfFail, mainMap), currentColMap));

			int noAbsRow = Integer.parseInt(mainMap.get(NO_OF_ABS_ROW_STRING)) - 1;
			inEntry = inEntries.get(colCount * noAbsRow + colNo);
			Utils.helpBatch(inEntry, outputFeed, StrSubstitutor.replace(
					StrSubstitutor.replace(noOfAbs, mainMap), currentColMap));
		}
		int noPassRow = Integer.parseInt(mainMap.get(NO_OF_PASS_ROW_STRING)) - 1;
		int noFailRow = Integer.parseInt(mainMap.get(NO_OF_FAIL_ROW_STRING)) - 1;
		int passCol = mainMap.get(PASSED_COLUMN_STRING).charAt(0) - 'A';

		CellEntry inEntry = inEntries.get(colCount * noPassRow + passCol);
		Utils.helpBatch(inEntry, outputFeed,
				StrSubstitutor.replace(totalPass, mainMap));
		inEntry = inEntries.get(colCount * noFailRow + passCol);
		Utils.helpBatch(inEntry, outputFeed,
				StrSubstitutor.replace(totalFail, mainMap));

		outputFeed = service.batch(
				new URL(inputFeed.getLink(Link.Rel.FEED_BATCH, Link.Type.ATOM)
						.getHref()), outputFeed);
		List<CellEntry> outEntries = outputFeed.getEntries();
		for (CellEntry cellEntry : outEntries) {
			String batchId = BatchUtils.getBatchId(cellEntry);
			if (BatchUtils.isFailure(cellEntry)) {
				BatchStatus status = BatchUtils.getBatchStatus(cellEntry);
				System.err.println("Failed entry");
				System.err.println("\t" + batchId + " failed ("
						+ status.getReason() + ") " + status.getContent());
			}
		}
	}

	/**
	 * Creates a worksheet for a class for an examination and populates with the
	 * necessary fields [will be extended to customized fields]<br/>
	 * Invoked by the system by means of
	 * {@link ExamResource#post(Representation, Variant)}
	 */
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		Form headers = (Form) getRequest().getAttributes().get(
				"org.restlet.http.headers");
		String retryCountStr = headers
				.getFirstValue("X-AppEngine-TaskRetryCount");
		if (retryCountStr != null) {
			int retryCount = Integer.parseInt(retryCountStr);
			if (retryCount > 0) {
				getResponse().setStatus(Status.SUCCESS_OK);
				return new EmptyRepresentation();
			}
		}
		try {
			// Parsing request and initializing
			SpreadsheetService service = new SpreadsheetService("Goddess");
			service.setProtocolVersion(SpreadsheetService.Versions.V1);
			String repText = entity.getText();
			JSONObject mainMap = (JSONObject) new JSONParser().parse(repText);
			String clazz = (String) mainMap.get("clazz");
			String token = (String) mainMap.get("token");
			service.setAuthSubToken(token);
			URL studentListUrl = new URL((String) mainMap.get("studentListUrl"));
			// URL cssListUrl = new URL((String) mainMap.get("cssListUrl"));
			String[] stdSec = clazz.split("-");
			String standard = stdSec[0];
			String section = stdSec[1];

			// Getting the required students' list
			ListQuery listQuery = new ListQuery(studentListUrl);
			listQuery.setSpreadsheetQuery("standard=\"" + standard
					+ "\" and section=\"" + section + "\"");
			ListFeed studFeed = service.getFeed(listQuery, ListFeed.class);
			List<ListEntry> studEntries = studFeed.getEntries();

			// Creating the class sheet and populating
			URL examWorkUrl = new URL((String) mainMap.get("examWorkUrl"));
			// here are the 9 additional rows: header,minimum marks, maximum
			// marks, highest, average, no of pass, no of fail, no of abs, above
			// 90
			// here are the 6 columns: studentid, name, total, average, rank,
			// passed?
			int strength = studFeed.getTotalResults();
			int noOfRows = strength + 9;
			WorksheetEntry workEntry = new WorksheetEntry(noOfRows, 6);
			workEntry.setTitle(new PlainTextConstruct(clazz));
			workEntry = service.insert(examWorkUrl, workEntry);
			URL cellFeedUrl = workEntry.getCellFeedUrl();
			CellQuery cellQuery = new CellQuery(cellFeedUrl);
			cellQuery.setReturnEmpty(true);
			CellFeed inFeed = service.getFeed(cellQuery, CellFeed.class);
			List<CellEntry> inEntries = inFeed.getEntries();
			CellFeed outFeed = new CellFeed();
			// Headers put
			Utils.helpBatch(inEntries.get(0), outFeed, "Student id");
			Utils.helpBatch(inEntries.get(1), outFeed, "Name");
			Utils.helpBatch(inEntries.get(2), outFeed, "Total");
			Utils.helpBatch(inEntries.get(3), outFeed, "Average");
			Utils.helpBatch(inEntries.get(4), outFeed, "Rank");
			Utils.helpBatch(inEntries.get(5), outFeed, "Passed");
			Utils.helpBatch(inEntries.get(6), outFeed, "Minimum marks");
			Utils.helpBatch(inEntries.get(12), outFeed, "Maximum marks");
			Utils.helpBatch(inEntries.get((noOfRows - 6) * 6), outFeed,
					"Highest");
			Utils.helpBatch(inEntries.get((noOfRows - 5) * 6), outFeed,
					"Average");
			Utils.helpBatch(inEntries.get((noOfRows - 4) * 6), outFeed,
					"No of Pass");
			Utils.helpBatch(inEntries.get((noOfRows - 3) * 6), outFeed,
					"No of Fail");
			Utils.helpBatch(inEntries.get((noOfRows - 2) * 6), outFeed,
					"No of abs");
			Utils.helpBatch(inEntries.get((noOfRows - 1) * 6), outFeed,
					"Above 90");
			// Student details put
			int i = 3;
			for (ListEntry studEntry : studEntries) {
				CustomElementCollection elements = studEntry
						.getCustomElements();
				Utils.helpBatch(inEntries.get(i * 6), outFeed,
						elements.getValue("studentid"));
				Utils.helpBatch(inEntries.get(i * 6 + 1), outFeed,
						elements.getValue("name"));
				i++;
			}
			String cellOutUrl = inFeed.getLink(Link.Rel.FEED_BATCH,
					Link.Type.ATOM).getHref();
			outFeed = service.batch(new URL(cellOutUrl), outFeed);
			List<CellEntry> outEntries = outFeed.getEntries();
			for (CellEntry cellEntry : outEntries) {
				String batchId = BatchUtils.getBatchId(cellEntry);
				if (BatchUtils.isFailure(cellEntry)) {
					BatchStatus status = BatchUtils.getBatchStatus(cellEntry);
					System.err.println("Failed entry");
					System.err.println("\t" + batchId + " failed ("
							+ status.getReason() + ") " + status.getContent());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage());
		}
		return new EmptyRepresentation();
	}
}
