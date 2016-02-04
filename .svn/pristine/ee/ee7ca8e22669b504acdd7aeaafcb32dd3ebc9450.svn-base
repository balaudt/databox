package com.sagarius.goddess.server.resources.mapreduce;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.lang.text.StrBuilder;
import org.json.simple.JSONArray;
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

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Method;
import com.google.gdata.client.batch.BatchInterruptedException;
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
import com.google.gdata.util.ServiceException;
import com.sagarius.goddess.server.Utils;

public class NewQueuedExamResource extends ServerResource {
	private SpreadsheetService service;
	private SubjectGroup rootGroup = new SubjectGroup();
	private int categorizationLevel;
	private String[][] subjectNames;
	private int noOfConsolidateRows;
	private JSONObject summary = new JSONObject();
	private int lastStudentRow;
	private int currentRow;
	private static Map<SubjectGroup, Integer> aTypeConsolidations = new HashMap<SubjectGroup, Integer>();
	private Queue queue;

	public NewQueuedExamResource() {
		getVariants().add(new Variant(MediaType.ALL));
	}

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
			String[] clazzConsolidations = new String[] { "Student Id",
					"Minimum Marks", "Maximum Marks", "Highest", "Average",
					"No of Pass", "No of Fail", "No of abs", "Above 90",
					"Attendants" };
			String[] studentConsolidations = new String[] { "Student Id",
					"Name", "Total", "Percentage", "Rank", "Result", "Passed?" };
			int strength = studFeed.getTotalResults();
			int noOfRows = strength + clazzConsolidations.length;
			int studConLen = studentConsolidations.length;
			WorksheetEntry workEntry = new WorksheetEntry(noOfRows, studConLen);
			workEntry.setTitle(new PlainTextConstruct(clazz));
			workEntry = service.insert(examWorkUrl, workEntry);
			URL cellFeedUrl = workEntry.getCellFeedUrl();
			CellQuery cellQuery = new CellQuery(cellFeedUrl);
			cellQuery.setReturnEmpty(true);
			CellFeed inFeed = service.getFeed(cellQuery, CellFeed.class);
			List<CellEntry> inEntries = inFeed.getEntries();
			CellFeed outFeed = new CellFeed();
			// Headers put
			for (int i = 0; i < studConLen; i++) {
				Utils.helpBatch(inEntries.get(i), outFeed,
						studentConsolidations[i]);
			}
			Utils.helpBatch(inEntries.get(studConLen), outFeed,
					clazzConsolidations[1]);
			Utils.helpBatch(inEntries.get(2 * studConLen), outFeed,
					clazzConsolidations[2]);
			for (int i = 3; i < clazzConsolidations.length; i++) {
				int index = (strength + i) * studConLen;
				Utils.helpBatch(inEntries.get(index), outFeed,
						clazzConsolidations[i]);
			}
			// Student details put
			int i = 3;
			for (ListEntry studEntry : studEntries) {
				CustomElementCollection elements = studEntry
						.getCustomElements();
				Utils.helpBatch(inEntries.get(i * studConLen), outFeed,
						elements.getValue("studentid"));
				Utils.helpBatch(inEntries.get(i * studConLen + 1), outFeed,
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

	@SuppressWarnings("unchecked")
	@Override
	protected Representation put(Representation representation, Variant variant)
			throws ResourceException {
		try {
			String repText = representation.getText();
			System.out.println(repText);
			JSONObject mainMap = (JSONObject) new JSONParser().parse(repText);
			String token = (String) mainMap.get("token");
			String workUrl = (String) mainMap.get("workEntry");
			String consolidatedUrl = (String) mainMap.get("consolidatedEntry");
			service = new SpreadsheetService("Goddess");
			service.setProtocolVersion(SpreadsheetService.Versions.V1);
			service.setAuthSubToken(token);

			WorksheetEntry workEntry = service.getEntry(new URL(workUrl),
					WorksheetEntry.class);
			String clazz = workEntry.getTitle().getPlainText();
			WorksheetEntry consolidatedEntry = service.getEntry(new URL(
					consolidatedUrl), WorksheetEntry.class);
			setupGroups(workEntry);

			queue = QueueFactory.getQueue("examqueue");
			JSONObject totalMap = (JSONObject) summary.get("totals");
			totalMap.put("workEntry", workUrl);
			totalMap.put("token", token);
			queue.add(url("/qe_btotal")
					.payload(JSONObject.toJSONString(totalMap))
					.method(Method.PUT).countdownMillis(2000));

			JSONObject studentMap = (JSONObject) summary.get("student");
			studentMap.put("workEntry", workUrl);
			studentMap.put("token", token);
			queue.add(url("/qe_student")
					.payload(JSONObject.toJSONString(studentMap))
					.method(Method.PUT).countdownMillis(2000));

			JSONObject clazzMap = (JSONObject) summary.get("clazz");
			clazzMap.put("workEntry", workUrl);
			clazzMap.put("token", token);
			queue.add(url("/qe_class")
					.payload(JSONObject.toJSONString(clazzMap))
					.method(Method.PUT).countdownMillis(2000));

			consolidate(consolidatedEntry, clazz,
					(String) mainMap.get("cssEntry"), token);
		} catch (Exception e) {
			e.printStackTrace();
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage());
		}
		getResponse().setStatus(Status.SUCCESS_OK);
		return new EmptyRepresentation();
	}

	@SuppressWarnings("unchecked")
	public void setupGroups(WorksheetEntry workEntry)
			throws MalformedURLException, IOException, ServiceException {
		int colCount = workEntry.getColCount();
		int rowCount = workEntry.getRowCount();

		int startSubject = 3;
		int endSubject = colCount - 5;
		categorizationLevel = 1;
		int noOfSubj = endSubject - startSubject + 1;
		CellQuery cellQuery = new CellQuery(workEntry.getCellFeedUrl());
		cellQuery.setMinimumCol(1);
		cellQuery.setMaximumCol(1);
		cellQuery.setMinimumRow(2);
		cellQuery.setMaximumRow(5);
		cellQuery.setReturnEmpty(true);
		CellFeed inFeed = service.getFeed(cellQuery, CellFeed.class);
		List<CellEntry> inEntries = inFeed.getEntries();
		for (int i = 0; i < 4; i++) {
			String generic = inEntries.get(i).getCell().getValue()
					.toLowerCase();
			if (generic.startsWith("cl")) {
				categorizationLevel++;
			}
		}

		subjectNames = new String[categorizationLevel][noOfSubj];
		cellQuery.setMinimumCol(startSubject);
		cellQuery.setMaximumCol(endSubject);
		cellQuery.setMinimumRow(1);
		cellQuery.setMaximumRow(categorizationLevel);
		inFeed = service.getFeed(cellQuery, CellFeed.class);
		inEntries = inFeed.getEntries();
		for (int i = 0; i < categorizationLevel; i++) {
			for (int j = 0; j < noOfSubj; j++) {
				int index = noOfSubj * i + j;
				subjectNames[i][j] = inEntries.get(index).getCell().getValue();
			}
		}

		rootGroup.setStartColumn(startSubject - 1);
		rootGroup.setEndColumn(endSubject);
		rootGroup.setLevel(0);
		analyzeSubjects(startSubject - 1, endSubject - 1, 0, rootGroup);
		rootGroup.setType(SubjectType.B);
		rootGroup.setup(categorizationLevel + 1);

		int minimumRow = categorizationLevel + 1;
		int maximumRow = categorizationLevel + 2;
		int firstStudentRow = categorizationLevel + 3;
		lastStudentRow = rowCount - 7;

		JSONObject bTotals = new JSONObject();
		bTotals.put("startIndex", firstStudentRow);
		bTotals.put("endIndex", lastStudentRow);
		JSONObject subBTotals = new JSONObject();
		bTotals.put("columns", subBTotals);
		addBTotals(subBTotals, rootGroup);

		String finalPassString = new StrBuilder("IF(")
				.append(rootGroup.getAbsentCondition()).append(",FALSE,IF(")
				.append(rootGroup.getMissingMarksCondition()).append(",'',IF(")
				.append(rootGroup.getPassCondition()).append(",TRUE,FALSE)))")
				.toString();
		String passCol = Utils.getColString(endSubject + 4);
		String totalCol = Utils.getColString(endSubject);
		String finalStatusString = new StrBuilder("IF(")
				.append(rootGroup.getAbsentCondition()).append(",'ABSENT',IF(")
				.append(passCol).append("i='','',IF(").append(passCol)
				.append("i,'PASS','FAIL')))").toString();
		String finalTotalString = new StrBuilder("IF(")
				.append(rootGroup.getAbsentCondition()).append(",'A',IF(")
				.append(passCol).append("i='','',IF(").append(passCol)
				.append("i,").append(rootGroup.getTotalString())
				.append(",'-')))").toString();
		String finalPercentageString = new StrBuilder("IF(")
				.append(rootGroup.getAbsentCondition()).append(",'A',IF(")
				.append(passCol).append("i='','',IF(").append(passCol)
				.append("i,").append(totalCol).append("i/").append(totalCol)
				.append(maximumRow).append("*100,'-')))").toString();
		// =IF(K4,RANK(H4,FILTER(H4:H53,K4:K53),0),"-")
		String finalRankString = new StrBuilder("IF(")
				.append(rootGroup.getAbsentCondition()).append(",'A',IF(")
				.append(passCol).append("i='','',IF(").append(passCol)
				.append("i,RANK(").append(totalCol).append("i,FILTER(")
				.append(totalCol).append(firstStudentRow).append(":")
				.append(totalCol).append(lastStudentRow).append(",")
				.append(passCol).append(firstStudentRow).append(":")
				.append(passCol).append(lastStudentRow).append("),0),'-')))")
				.toString();
		JSONObject studentConsolidations = new JSONObject();
		studentConsolidations.put("pass", finalPassString);
		studentConsolidations.put("status", finalStatusString);
		studentConsolidations.put("total", finalTotalString);
		studentConsolidations.put("percentage", finalPercentageString);
		studentConsolidations.put("rank", finalRankString);
		studentConsolidations.put("startIndex", firstStudentRow);
		studentConsolidations.put("endIndex", lastStudentRow);
		studentConsolidations.put("startCol", endSubject + 2);

		String highestString = new StrBuilder("MAX(i").append(firstStudentRow)
				.append(":i").append(lastStudentRow).append(")").toString();
		String averageString = new StrBuilder("AVERAGE(i")
				.append(firstStudentRow).append(":i").append(lastStudentRow)
				.append(")").toString();
		String passString = new StrBuilder("COUNTIF(i").append(firstStudentRow)
				.append(":i").append(lastStudentRow).append(",'>='")
				.append("&i").append(minimumRow).append(")").toString();
		String failString = new StrBuilder("COUNTIF(i").append(firstStudentRow)
				.append(":i").append(lastStudentRow).append(",'<'")
				.append("&i").append(minimumRow).append(")").toString();
		String absentString = new StrBuilder("COUNTIF(i")
				.append(firstStudentRow).append(":i").append(lastStudentRow)
				.append(",'=A')").toString();
		String ninetyString = new StrBuilder("COUNTIF(i")
				.append(firstStudentRow).append(":i").append(lastStudentRow)
				.append(",'>='").append("&i").append(maximumRow)
				.append("*90/100)").toString();
		String attendeesString = new StrBuilder("COUNTA(i")
				.append(firstStudentRow).append(":i").append(lastStudentRow)
				.append(")").toString();
		String bPassString = new StrBuilder("COUNT(i").append(firstStudentRow)
				.append(":i").append(lastStudentRow).append(")").toString();
		String bFailString = new StrBuilder("COUNTIF(i")
				.append(firstStudentRow).append(":i").append(lastStudentRow)
				.append(",'=-')").toString();
		JSONObject clazzConsolidations = new JSONObject();
		clazzConsolidations.put("highest", highestString);
		clazzConsolidations.put("average", averageString);
		clazzConsolidations.put("pass", passString);
		clazzConsolidations.put("fail", failString);
		clazzConsolidations.put("absent", absentString);
		clazzConsolidations.put("ninety", ninetyString);
		clazzConsolidations.put("attend", attendeesString);
		clazzConsolidations.put("bpass", bPassString);
		clazzConsolidations.put("bfail", bFailString);
		Set<String> bKeySet = subBTotals.keySet();
		JSONArray forBColsInClazz = new JSONArray();
		for (String key : bKeySet) {
			forBColsInClazz.add(key);
		}
		clazzConsolidations.put("bcols", forBColsInClazz);
		clazzConsolidations.put("startIndex", startSubject);
		clazzConsolidations.put("endIndex", endSubject + 1);
		clazzConsolidations.put("startRow", lastStudentRow + 1);

		summary.put("subjectTree", rootGroup);
		summary.put("student", studentConsolidations);
		summary.put("totals", bTotals);
		summary.put("clazz", clazzConsolidations);
	}

	public void analyzeSubjects(int startColumn, int endColumn, int onRow,
			SubjectGroup inGroup) {

		boolean isAllEmpty = true;
		int startZBase = rootGroup.getStartColumn();
		for (int i = startColumn; i <= endColumn; i++) {
			if (subjectNames[onRow][i - startZBase] != null) {
				isAllEmpty = false;
				break;
			}
		}
		if (isAllEmpty) {
			return;
		}

		String lastSubjStr = subjectNames[onRow][endColumn - startZBase];
		if (lastSubjStr == null) {
			inGroup.setType(SubjectType.A);
		} else if (lastSubjStr.equals("Total")) {
			inGroup.setType(SubjectType.B);
			endColumn--;
		} else {
			inGroup.setType(SubjectType.A);
		}
		int startCol = startColumn;
		for (int i = startColumn; i <= endColumn; i++) {
			if (subjectNames[onRow][i - startZBase] == null) {
				// Start of a subject group
				if (i - startCol - 1 > 0) {
					// Presence of a Z group before the start of some other
					// group
					ZGroup zGroup = new ZGroup();
					zGroup.setStartColumn(startCol);
					// i-2-startCol+1
					noOfConsolidateRows += i - 1 - startCol;
					zGroup.setEndColumn(i - 2);
					zGroup.setLevel(1);
					List<String> subjects = zGroup.getSubjects();
					for (int j = startCol; j < i - 1; j++) {
						subjects.add(subjectNames[onRow][j - startZBase]);
					}
					startCol = i - 1;
					zGroup.setParent(inGroup);
					inGroup.getChildren().add(zGroup);
				}
				for (; i <= endColumn; i++) {
					// Looping to find end of group
					if (subjectNames[onRow][i - startZBase] != null) {
						// Start of the next group
						SubjectGroup newGroup = new SubjectGroup();
						newGroup.setStartColumn(startCol);
						newGroup.setEndColumn(i - 1);
						newGroup.setLevel(onRow + 1);
						newGroup.setName(subjectNames[onRow][startCol
								- startZBase]);
						newGroup.setParent(inGroup);
						inGroup.getChildren().add(newGroup);
						analyzeSubjects(startCol, i - 1, onRow + 1, newGroup);
						startCol = i;
						noOfConsolidateRows++;
						break;
					}
				}
				if (i == endColumn + 1) {
					// The group is the last one
					SubjectGroup newGroup = new SubjectGroup();
					newGroup.setStartColumn(startCol);
					newGroup.setEndColumn(i - 1);
					newGroup.setLevel(onRow + 1);
					newGroup.setName(subjectNames[onRow][startCol - startZBase]);
					newGroup.setParent(inGroup);
					inGroup.getChildren().add(newGroup);
					analyzeSubjects(startCol, i - 1, onRow + 1, newGroup);
					noOfConsolidateRows++;
				}
				if (i == endColumn) {
					// The group ended just before last column leaving an orphan
					// subject at the last
					// The condition is placed here as the for loop exits with
					// yet another i++
					ZGroup zGroup = new ZGroup();
					zGroup.setStartColumn(startCol);
					zGroup.setEndColumn(endColumn);
					noOfConsolidateRows += endColumn - startCol + 1;
					zGroup.setLevel(onRow + 1);
					zGroup.getSubjects().add(
							subjectNames[onRow][startCol - startZBase]);
					zGroup.setParent(inGroup);
					inGroup.getChildren().add(zGroup);
				}
			} else if (i == endColumn) {
				// Z-Group at the last
				ZGroup zGroup = new ZGroup();
				zGroup.setStartColumn(startCol);
				zGroup.setEndColumn(endColumn);
				List<String> subjects = zGroup.getSubjects();
				for (int j = startCol; j <= endColumn; j++) {
					subjects.add(subjectNames[onRow][j - startZBase]);
				}
				noOfConsolidateRows += endColumn - startCol + 1;
				zGroup.setLevel(onRow + 1);
				zGroup.setParent(inGroup);
				inGroup.getChildren().add(zGroup);
			}
		}
	}

	// TODO Also check the minimum mark condition for total too
	// It may be tempting to add the B-Type checking in children loop instead of
	// recursive calling addBTotals. But it will not work in certain cases eg.
	// AB [beta]
	@SuppressWarnings("unchecked")
	public void addBTotals(JSONObject bTotals, SubjectGroup group) {
		if (group.type == SubjectType.B) {
			String finalTotalString = new StrBuilder("IF(")
					.append(group.getAbsentCondition()).append(",'A',IF(")
					.append(group.getMissingMarksCondition()).append(",'',IF(")
					.append(group.getPassCondition()).append(",")
					.append(group.getTotalString()).append(",'-')))")
					.toString();
			bTotals.put(group.getEndColString(), finalTotalString);
		}
		List<SubjectGroup> children = group.getChildren();
		for (SubjectGroup child : children) {
			addBTotals(bTotals, child);
		}
	}

	@SuppressWarnings("unchecked")
	public void consolidate(WorksheetEntry consolidatedEntry, String clazzName,
			String cssEntry, String token) throws BatchInterruptedException,
			MalformedURLException, IOException, ServiceException {
		int rowCount = consolidatedEntry.getRowCount();
		// 1 for the root group itself
		consolidatedEntry.setRowCount(noOfConsolidateRows + 1 + rowCount);
		consolidatedEntry.update();

		CellQuery cellQuery = new CellQuery(consolidatedEntry.getCellFeedUrl());
		cellQuery.setReturnEmpty(true);
		cellQuery.setMinimumCol(1);
		int noOfConsolidationFields = 16;
		cellQuery.setMaximumCol(noOfConsolidationFields);
		cellQuery.setMinimumRow(rowCount + 1);
		cellQuery.setMaximumRow(rowCount + 1);

		CellFeed inFeed = service.getFeed(cellQuery, CellFeed.class);
		List<CellEntry> inEntries = inFeed.getEntries();
		CellFeed outFeed = new CellFeed();
		Utils.helpBatch(inEntries.get(2), outFeed,
				new StrBuilder("=TRANSPOSE('").append(clazzName)
						.append("'!C1:").append(rootGroup.getEndColString())
						.append(categorizationLevel).append(")").toString());
		Utils.helpBatch(
				inEntries.get(7),
				outFeed,
				new StrBuilder("=TRANSPOSE('").append(clazzName).append("'!C")
						.append(categorizationLevel + 1).append(":")
						.append(rootGroup.getEndColString())
						.append(categorizationLevel + 2).append(")").toString());
		int startRow = lastStudentRow + 1;
		Utils.helpBatch(
				inEntries.get(9),
				outFeed,
				new StrBuilder("=TRANSPOSE('").append(clazzName).append("'!C")
						.append(startRow).append(":")
						.append(rootGroup.getEndColString())
						.append(startRow + 6).append(")").toString());
		service.batch(
				new URL(inFeed.getLink(Link.Rel.FEED_BATCH, Link.Type.ATOM)
						.getHref()), outFeed);

		int noOfRows = rootGroup.getEndColumn() - rootGroup.getStartColumn()
				+ 1;
		cellQuery.setMinimumRow(noOfRows + 1 + rowCount);
		cellQuery.setMaximumRow(noOfConsolidateRows + 1 + rowCount);
		inFeed = service.getFeed(cellQuery, CellFeed.class);
		inEntries = inFeed.getEntries();
		outFeed = new CellFeed();
		currentRow = 0;
		consolidateTraverse(rootGroup, inEntries, outFeed, noOfRows);
		service.batch(
				new URL(inFeed.getLink(Link.Rel.FEED_BATCH, Link.Type.ATOM)
						.getHref()), outFeed);

		JSONObject mainMap = new JSONObject();
		mainMap.put("consolidateUrl", consolidatedEntry.getSelfLink().getHref());
		mainMap.put("cssUrl", cssEntry);
		mainMap.put("startIndex", rowCount + 1);
		mainMap.put("endIndex", noOfConsolidateRows + 1 + rowCount);
		mainMap.put("token", token);
		mainMap.put("clazz", clazzName);
		queue.add(url("/qe_consolidate")
				.payload(JSONObject.toJSONString(mainMap)).method(Method.PUT)
				.countdownMillis(2000));
	}

	public void consolidateTraverse(SubjectGroup group,
			List<CellEntry> inEntries, CellFeed outFeed, int noOfRows) {
		List<SubjectGroup> children = group.getChildren();
		for (SubjectGroup child : children) {
			consolidateTraverse(child, inEntries, outFeed, noOfRows);
		}
		if (group.getType() != SubjectType.A) {
			return;
		}
		Stack<String> nameTree = new Stack<String>();
		SubjectGroup temp = group;
		while (temp != null && temp.getName() != null) {
			nameTree.push(temp.getName());
			temp = temp.getParent();
		}
		Integer index = 0;
		int noOfConsolidationFields = 16;
		while (true) {
			try {
				Utils.helpBatch(
						inEntries.get(noOfConsolidationFields * currentRow + 2
								+ index), outFeed, nameTree.pop());
				index++;
			} catch (EmptyStackException e) {
				break;
			}
		}
		StrBuilder numeratorBuilder = new StrBuilder("=SUM(");
		StrBuilder denominatorBuilder = new StrBuilder("SUM(");
		StrBuilder minimumBuilder = new StrBuilder("=MIN(");
		StrBuilder maxBuilder = new StrBuilder("=MAX(");
		StrBuilder highestBuilder = new StrBuilder("=MAX(");
		StrBuilder passBuilder = new StrBuilder("=SUM(");
		StrBuilder failBuilder = new StrBuilder("=SUM(");
		StrBuilder absBuilder = new StrBuilder("=SUM(");
		StrBuilder ninetyBuilder = new StrBuilder("=SUM(");
		StrBuilder attendBuilder = new StrBuilder("=SUM(");
		for (SubjectGroup child : children) {
			int endIndex = child.getEndColumn();
			int startIndex = child.getStartColumn();
			if (child.getType() == SubjectType.ZERO) {
				numeratorBuilder.append("SUMPRODUCT(K").append(startIndex)
						.append(":K").append(endIndex).append(",P")
						.append(startIndex).append(":P").append(endIndex)
						.append("),");
				denominatorBuilder.append("SUM(P").append(startIndex)
						.append(":P").append(endIndex).append("),");
				minimumBuilder.append("MIN(H").append(startIndex).append(":H")
						.append(endIndex).append("),");
				maxBuilder.append("MAX(I").append(startIndex).append(":I")
						.append(endIndex).append("),");
				highestBuilder.append("MAX(J").append(startIndex).append(":J")
						.append(endIndex).append("),");
				passBuilder.append("SUM(L").append(startIndex).append(":L")
						.append(endIndex).append("),");
				failBuilder.append("SUM(M").append(startIndex).append(":M")
						.append(endIndex).append("),");
				absBuilder.append("SUM(N").append(startIndex).append(":N")
						.append(endIndex).append("),");
				ninetyBuilder.append("SUM(O").append(startIndex).append(":O")
						.append(endIndex).append("),");
				attendBuilder.append("SUM(P").append(startIndex).append(":P")
						.append(endIndex).append("),");
			} else {
				index = null;
				if (child.getType() == SubjectType.A) {
					index = aTypeConsolidations.get(child);
				} else if (child.getType() == SubjectType.B) {
					index = endIndex;
				}
				numeratorBuilder.append("K").append(index).append("*P")
						.append(index).append(",");
				denominatorBuilder.append("P").append(index).append(",");
				minimumBuilder.append("H").append(index).append(",");
				maxBuilder.append("I").append(index).append(",");
				highestBuilder.append("J").append(index).append(",");
				passBuilder.append("L").append(index).append(",");
				failBuilder.append("M").append(index).append(",");
				absBuilder.append("N").append(index).append(",");
				ninetyBuilder.append("O").append(index).append(",");
				attendBuilder.append("P").append(index).append(",");
			}
		}
		numeratorBuilder.deleteCharAt(numeratorBuilder.length() - 1);
		numeratorBuilder.append(")");
		denominatorBuilder.deleteCharAt(denominatorBuilder.length() - 1);
		denominatorBuilder.append(")");
		minimumBuilder.deleteCharAt(minimumBuilder.length() - 1);
		minimumBuilder.append(")");
		maxBuilder.deleteCharAt(maxBuilder.length() - 1);
		maxBuilder.append(")");
		highestBuilder.deleteCharAt(highestBuilder.length() - 1);
		highestBuilder.append(")");
		passBuilder.deleteCharAt(passBuilder.length() - 1);
		passBuilder.append(")");
		failBuilder.deleteCharAt(failBuilder.length() - 1);
		failBuilder.append(")");
		absBuilder.deleteCharAt(absBuilder.length() - 1);
		absBuilder.append(")");
		ninetyBuilder.deleteCharAt(ninetyBuilder.length() - 1);
		ninetyBuilder.append(")");
		attendBuilder.deleteCharAt(attendBuilder.length() - 1);
		attendBuilder.append(")");

		Utils.helpBatch(
				inEntries.get(noOfConsolidationFields * currentRow + 10),
				outFeed,
				numeratorBuilder.toString().concat("/")
						.concat(denominatorBuilder.toString()));
		Utils.helpBatch(
				inEntries.get(noOfConsolidationFields * currentRow + 7),
				outFeed, minimumBuilder.toString());
		Utils.helpBatch(
				inEntries.get(noOfConsolidationFields * currentRow + 8),
				outFeed, maxBuilder.toString());
		Utils.helpBatch(
				inEntries.get(noOfConsolidationFields * currentRow + 9),
				outFeed, highestBuilder.toString());
		Utils.helpBatch(
				inEntries.get(noOfConsolidationFields * currentRow + 11),
				outFeed, passBuilder.toString());
		Utils.helpBatch(
				inEntries.get(noOfConsolidationFields * currentRow + 12),
				outFeed, failBuilder.toString());
		Utils.helpBatch(
				inEntries.get(noOfConsolidationFields * currentRow + 13),
				outFeed, absBuilder.toString());
		Utils.helpBatch(
				inEntries.get(noOfConsolidationFields * currentRow + 14),
				outFeed, ninetyBuilder.toString());
		Utils.helpBatch(
				inEntries.get(noOfConsolidationFields * currentRow + 15),
				outFeed, attendBuilder.toString());

		aTypeConsolidations.put(group, noOfRows + 1 + currentRow);
		currentRow++;
	}

}
