package com.google.gwt.gdata.client.spreadsheet;

public class FeedURLFactory {
	public static final String DEFAULT_SPREADSHEETS_URL = "http://spreadsheets.google.com";

	private static final String SPREADSHEETS_PATH = "feeds/spreadsheets/private/full";
	private static final String WORKSHEETS_PATH = "feeds/worksheets/";
	private static final String LIST_PATH = "feeds/list/";
	private static final String CELLS_PATH = "feeds/cells/";
	private static final String TABLE_PATH = "/tables/";
	private static final String RECORD_PATH = "/records/";
	private static final String BASE_PATH = "feeds/";
	private static final FeedURLFactory instance = new FeedURLFactory();

	private String baseUrl;
	private String feedSpreadsheets;
	private String feedWorksheets;
	private String feedList;
	private String feedCells;

	private FeedURLFactory() {
		init(DEFAULT_SPREADSHEETS_URL);
	}

	public static FeedURLFactory getDefault() {
		return instance;
	}

	private void init(String url) {
		if (!url.endsWith("/")) {
			url += "/";
		}
		baseUrl = url;
		feedSpreadsheets = baseUrl + SPREADSHEETS_PATH;
		feedWorksheets = baseUrl + WORKSHEETS_PATH;
		feedList = baseUrl + LIST_PATH;
		feedCells = baseUrl + CELLS_PATH;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public String getSpreadsheetsFeedUrl() {
		return feedSpreadsheets;
	}

	public String getWorksheetFeedUrl(String spreadsheetKey, String visibility,
			String projection) {
		if (spreadsheetKey == null) {
			throw new NullPointerException("spreadsheetKey is null");
		}
		return makeUrl(feedWorksheets, spreadsheetKey, visibility, projection);
	}

	public String getTableFeedUrl(String spreadsheetKey) {
		if (spreadsheetKey == null) {
			throw new NullPointerException("spreadsheetKey is null");
		}
		return baseUrl + BASE_PATH + spreadsheetKey + TABLE_PATH;
	}

	public String getRecordFeedUrl(String spreadsheetKey, String tableId) {
		if (spreadsheetKey == null) {
			throw new NullPointerException("spreadsheetKey is null");
		}
		return baseUrl + BASE_PATH + spreadsheetKey + RECORD_PATH + tableId;
	}

	public String getListFeedUrl(String spreadsheetKey, String worksheetId,
			String visibility, String projection) {
		return makeUrl(feedList, spreadsheetKey, worksheetId, visibility,
				projection);
	}

	public String getCellFeedUrl(String spreadsheetKey, String worksheetId,
			String visibility, String projection) {
		return makeUrl(feedCells, spreadsheetKey, worksheetId, visibility,
				projection);
	}

	private String makeUrl(String url, String spreadsheetKey,
			String parentResourceId, String visibility, String projection) {
		if (spreadsheetKey == null) {
			throw new NullPointerException("spreadsheetKey is null");
		}
		if (parentResourceId == null) {
			throw new NullPointerException("worksheetId is null");
		}
		String path = spreadsheetKey + "/" + parentResourceId;
		return makeUrl(url, path, visibility, projection);
	}

	private String makeUrl(String url, String path, String visibility,
			String projection) {
		if (visibility == null) {
			throw new NullPointerException("visibility is null");
		}
		if (projection == null) {
			throw new NullPointerException("projection is null");
		}
		path = path + "/" + visibility + "/" + projection;
		return url + path;
	}
}
