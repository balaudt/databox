package com.sagarius.goddess.client.view;

import java.util.List;
import java.util.Map;

import org.sagarius.radix.client.uri.StatefulWidget;
import org.sagarius.radix.client.uri.Template;
import org.sagarius.radix.client.view.Table.SortType;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gwt.gdata.client.spreadsheet.ListEntry;
import com.google.gwt.gdata.client.spreadsheet.ListFeed;
import com.google.gwt.gdata.client.spreadsheet.ListQuery;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.LegendPosition;
import com.google.gwt.visualization.client.QueryResponse;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.LineChart;
import com.sagarius.goddess.client.Utils;
import com.sagarius.goddess.client.ajax.CacheRequest;
import com.sagarius.goddess.client.ajax.CacheType;
import com.sagarius.goddess.client.ajax.GenericRequest;
import com.sagarius.goddess.client.ajax.GenericResponse;
import com.sagarius.goddess.client.ajax.MultiRequestCallback;
import com.sagarius.goddess.client.ajax.RequestType;
import com.sagarius.goddess.client.gadgets.ProfileGadget.ProfileDetails.FormWidget;
import com.sagarius.goddess.client.gadgets.ProfileGadget.ProfileDetails.FormWidget.FormElement;
import com.sagarius.goddess.client.model.Document;
import com.sagarius.goddess.client.model.enumerations.DocumentType;
import com.sagarius.goddess.client.present.CrumbHandler;
import com.sagarius.goddess.client.present.NewSpreadsheetUtils;
import com.sagarius.goddess.client.utils.DocumentMetaData;
import com.sagarius.goddess.client.utils.Fields.Generic;
import com.sagarius.goddess.client.utils.Fields.Staff;
import com.sagarius.goddess.client.utils.Fields.Student;
import com.sagarius.goddess.client.utils.MetaDataRepository;
import com.sagarius.goddess.client.utils.visualization.DataTable;
import com.sagarius.goddess.client.utils.visualization.QueryBuilder;
import com.sagarius.goddess.client.utils.visualization.QueryBuilder.Operator;
import com.sagarius.goddess.client.view.util.Breadcrumb;
import com.sagarius.goddess.client.view.util.HiddenRowButtons;
import com.sagarius.goddess.client.view.util.NewSMSPopup;
import com.sagarius.goddess.client.widgets.BaseWidget;

public class ClassPresenter extends FlowPanel implements StatefulWidget,
		CrumbHandler {
	private String clazz;
	private StaffGadget staffGadget;
	private Label mainLabel;
	private StudentGadget studentsGadget;
	private ExamGadget examGadget;
	private AttendanceGadget attendGadget;

	public ClassPresenter() {
		this(null);
	}

	public ClassPresenter(String clazz) {
		add(mainLabel = new Label());
		mainLabel.setStyleName("h18 strong mb2");
		add(staffGadget = new StaffGadget());
		add(new HTML("<hr class='space'/> <hr />"));
		add(studentsGadget = new StudentGadget());
		add(new HTML("<hr class='space'/> <hr />"));
		add(examGadget = new ExamGadget());
		add(new HTML("<hr class='space'/> <hr />"));
		add(attendGadget = new AttendanceGadget());
		add(new HTML("<hr class='space'/> <hr />"));
		if (clazz != null) {
			setClazz(clazz);
			refresh();
		}
		Event.addNativePreviewHandler(new NativePreviewHandler() {

			@Override
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				if (event.getTypeInt() == Event.ONKEYPRESS) {
					NativeEvent nativeEvent = event.getNativeEvent();
					if (nativeEvent.getAltKey() && nativeEvent.getCtrlKey()) {
						int code = nativeEvent.getKeyCode();
						switch (code) {
						case 49:
							staffGadget.refresh();
							break;
						case 50:
							studentsGadget.refresh();
							break;
						case 51:
							examGadget.refresh();
							break;
						case 52:
							attendGadget.refresh();
						}
					}
				}
			}
		});
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
		mainLabel.setText("Class: " + clazz);
		refresh();
		((Label) Breadcrumb.getCrumbAt(2, false)).setText(clazz);
		if (Utils.currentSchool != null) {
			Window.setTitle("Databox - " + Utils.currentSchool.getName()
					+ " - Class " + clazz);
		}
	}

	public void refresh() {
		staffGadget.refresh();
		studentsGadget.refresh();
		examGadget.refresh();
		attendGadget.refresh();
	}

	public String getClazz() {
		return clazz;
	}

	class StaffGadget extends BaseWidget {
		private final QueryBuilder builder = new QueryBuilder(Utils.sheetVizUrl);
		private final Anchor downloadLink = new Anchor("Download list",
				"javascript:", "_blank");
		private final Anchor messageLink = new Anchor("Send message",
				"javascript:");
		private final Anchor smsLink = new Anchor("Send message", "javascript:");
		private HandlerRegistration smsRegistration;
		private Map<String, String> staffMap;

		public StaffGadget() {
			title.setText("Related teachers");
			title.setStyleName("h13 mb3 strong");
			downloadLink.setStyleName("icon-16-inbox-download mr4");
			messageLink.setStyleName("icon-16-mail--arrow mr4");
			smsLink.setStyleName("icon-16-mobile-phone-cast mr4");
			controlsPanel.add(downloadLink);
			controlsPanel.add(smsLink);
		}

		@Override
		protected void initialize(final AsyncCallback<Void> callback) {
			MultiRequestCallback multiRequest = new MultiRequestCallback() {

				@Override
				public void complete() {
					if (isFailed) {
						return;
					}
					callback.onSuccess(null);
				}

			};
			multiRequest.add(
					new GenericRequest().setType(RequestType.CACHE)
							.setCacheRequest(
									new CacheRequest().setType(
											CacheType.DOCUMENT).setInput(
											DocumentType.CSSREL)),
					new AsyncCallback<GenericResponse>() {

						@Override
						public void onSuccess(GenericResponse result) {
							DocumentMetaData cssMeta = (DocumentMetaData) result
									.getOutput();
							builder.setMetaData(cssMeta).addSelections(
									Staff.STAFF_ID, Generic.SUBJECT);

						}

						@Override
						public void onFailure(Throwable caught) {
							callback.onFailure(caught);
						}
					});
			multiRequest.add(
					new GenericRequest().setType(RequestType.CACHE)
							.setCacheRequest(
									new CacheRequest()
											.setType(CacheType.STAFF_MAP)),
					new AsyncCallback<GenericResponse>() {

						@SuppressWarnings("unchecked")
						@Override
						public void onSuccess(GenericResponse result) {
							staffMap = (Map<String, String>) result.getOutput();
						}

						@Override
						public void onFailure(Throwable caught) {
							callback.onFailure(caught);
						}
					});
			multiRequest.start();
		}

		@Override
		public void responseReceived(GenericResponse gResponse) {
			final String[] paths = clazz.split("-");
			downloadLink.setHref(request.getVisualizationQuery().concat(
					"&tqx=out:csv"));
			if (smsRegistration != null) {
				smsRegistration.removeHandler();
			}
			smsRegistration = smsLink.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					NewSMSPopup smsWidget = NewSMSPopup.INSTANCE;
					String tqQuery = URL.encode("select * where standard=\""
							+ paths[0] + "\" and section=\"" + paths[1] + "\"");
					String groupString = "Staffs of Class " + paths[0] + "-"
							+ paths[1];
					smsWidget.setRequestUri("/sms?rel=1&g=" + groupString
							+ "&type=STAFF&tq=" + tqQuery);
					smsWidget.setCapabilities(NewSMSPopup.NONE);
					smsWidget.center();
				}
			});

			statusMessage.setHTML("Parsing response");
			QueryResponse response = gResponse.getQueryResponse();
			DataTable dataTable = (DataTable) response.getDataTable();
			int noOfRows = dataTable.getNumberOfRows();
			for (int i = 0; i < noOfRows; i++) {
				FlowPanel staffPanel = new FlowPanel();
				staffPanel.setStyleName("clearfix span-6 mb3");
				if ((i + 1) % 4 == 0) {
					staffPanel.addStyleName("last");
				} else if (i % 4 == 0) {
					staffPanel.addStyleName("clear");
				}
				FlowPanel innerStaffPanel = new FlowPanel();
				innerStaffPanel.setStyleName("br1 p5 clearfix");
				String staffId = dataTable.getValue(i, 0, false);
				final Image staffImage = new Image("/photo?size=72&id="
						+ staffId);
				staffImage.setStyleName("fl mr4");
				innerStaffPanel.add(staffImage);
				Label subjectLabel = new Label(dataTable.getValue(i, 1));
				subjectLabel.setStyleName("role strong");
				innerStaffPanel.add(subjectLabel);
				Hyperlink staffLink = new Hyperlink(staffMap.get(staffId),
						"staff/" + staffId);
				staffLink.setStyleName("name");
				innerStaffPanel.add(staffLink);
				staffPanel.add(innerStaffPanel);
				contentsPanel.add(staffPanel);
			}
			statusMessage.setHTML("");
		}

		@Override
		protected void buildRequest(AsyncCallback<Void> callback) {
			if (clazz != null) {
				final String[] paths = clazz.split("-");
				String requestUri = builder.removeFilters()
						.addFilter(Generic.STANDARD, Operator.EQ, paths[0])
						.addFilter(Generic.SECTION, Operator.EQ, paths[1])
						.get();
				if (request == null) {
					request = new GenericRequest()
							.setType(RequestType.VISUALIZATION);
				}
				request.setVisualizationQuery(requestUri);
				callback.onSuccess(null);
			} else {
				callback.onFailure(new Throwable("Invalid clazz string"));
			}
		}
	}

	class StudentGadget extends BaseWidget {
		private final QueryBuilder builder = new QueryBuilder(Utils.sheetVizUrl);
		private final Anchor downloadLink = new Anchor("Download list",
				"javascript:");
		private final Anchor messageLink = new Anchor("Send message",
				"javascript:");
		private final Anchor smsLink = new Anchor("Send message", "javascript:");
		private HandlerRegistration smsRegistration;

		public StudentGadget() {
			title.setText("Student list");
			title.setStyleName("h13 mb3 strong");
			downloadLink.setStyleName("icon-16-inbox-download mr4");
			messageLink.setStyleName("icon-16-mail--arrow mr4");
			smsLink.setStyleName("icon-16-mobile-phone-cast mr4");
			controlsPanel.add(downloadLink);
			// controlsPanel.add(messageLink);
			controlsPanel.add(smsLink);

		}

		@Override
		protected void initialize(final AsyncCallback<Void> callback) {
			MetaDataRepository.get(DocumentType.SINGLE_STUDENT,
					new AsyncCallback<DocumentMetaData>() {

						@Override
						public void onSuccess(DocumentMetaData result) {
							builder.setMetaData(result).addSelections(
									Student.STUDENT_ID, Generic.NAME);
							callback.onSuccess(null);
						}

						@Override
						public void onFailure(Throwable caught) {
							callback.onFailure(caught);
						}
					});
		}

		@Override
		protected void buildRequest(AsyncCallback<Void> callback) {
			final String[] paths = clazz.split("-");
			String requestUri = builder.removeFilters()
					.addFilter(Generic.STANDARD, Operator.EQ, paths[0])
					.addFilter(Generic.SECTION, Operator.EQ, paths[1]).get();
			if (request == null) {
				request = new GenericRequest()
						.setType(RequestType.VISUALIZATION);
			}
			request.setVisualizationQuery(requestUri);
			callback.onSuccess(null);
		}

		@Override
		public void responseReceived(GenericResponse gResponse) {
			final String[] paths = clazz.split("-");
			downloadLink.setHref(request.getVisualizationQuery().concat(
					"&tqx=out:csv"));
			if (smsRegistration != null) {
				smsRegistration.removeHandler();
			}
			smsRegistration = smsLink.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					NewSMSPopup smsWidget = NewSMSPopup.INSTANCE;
					String tqQuery = URL.encode("select * where standard=\""
							+ paths[0] + "\" and section=\"" + paths[1] + "\"");
					String groupsQuery = "Parents of Class " + paths[0] + "-"
							+ paths[1];
					smsWidget.setRequestUri("/sms?g=" + groupsQuery
							+ "&type=PARENT&tq=" + tqQuery);
					smsWidget.setCapabilities(NewSMSPopup.NONE);
					smsWidget.center();
				}
			});

			QueryResponse response = gResponse.getQueryResponse();
			statusMessage.setHTML("Parsing response");
			DataTable table = (DataTable) response.getDataTable();
			int rows = table.getNumberOfRows();

			FlexTable studList = new FlexTable();
			studList.setStyleName("clear");
			int rowCount = 0, colCount = 0;
			for (int i = 0; i < rows; i++) {
				studList.setWidget(
						rowCount,
						colCount,
						new Hyperlink(table.getValue(i, 1), "student/"
								+ table.getValue(i, 0) + ""));
				colCount++;
				if (colCount == 5) {
					colCount = 0;
					rowCount++;
				}
			}
			contentsPanel.add(studList);
			statusMessage.setHTML("");

		}
	}

	class ExamGadget extends BaseWidget {
		protected String docKey;
		private ListBox examSelect;

		public ExamGadget() {
			title.setText("Examination Summary");
			title.setStyleName("h13 mb3 strong");
			header.setStyleName("form");
			examSelect = new ListBox(false);
			header.add(new FormWidget().addElement(new FormElement(false, 6)
					.setName("Examination").setValue(examSelect)));
		}

		@Override
		protected void initialize(final AsyncCallback<Void> callback) {
			MetaDataRepository.getDocuments(DocumentType.EXAMINATION,
					new AsyncCallback<List<Document>>() {

						@Override
						public void onSuccess(List<Document> result) {
							examSelect.clear();
							for (Document document : result) {
								examSelect.addItem(document.getParam1(),
										document.getCccKey());
							}
							docKey = examSelect.getValue(0);
							examSelect.addChangeHandler(new ChangeHandler() {

								@Override
								public void onChange(ChangeEvent event) {
									docKey = examSelect.getValue(examSelect
											.getSelectedIndex());
									refresh();
								}
							});

							if (clazz != null) {
								refresh();
							}
							callback.onSuccess(null);
						}

						@Override
						public void onFailure(Throwable caught) {
							callback.onFailure(caught);
						}
					});
		}

		@Override
		protected void buildRequest(final AsyncCallback<Void> callback) {
			final String[] paths = clazz.split("-");
			NewSpreadsheetUtils.getWorkSheetByTitle(docKey, "Consolidated",
					new AsyncCallback<String>() {

						@Override
						public void onFailure(Throwable caught) {
							callback.onFailure(caught);
						}

						@Override
						public void onSuccess(String result) {
							if (result == null) {
								contentsPanel.clear();
								contentsPanel.add(new Label("No data"));
								setLoading(false);
								return;
							}
							FeedURLFactory factory = FeedURLFactory
									.getDefault();
							String feedUrl = factory.getListFeedUrl(docKey,
									result, "private", "full");
							if (request == null) {
								request = new GenericRequest()
										.setType(RequestType.GDATA_LIST);
							}
							ListQuery query = ListQuery.newInstance(feedUrl);
							query.setSpreadsheetQuery("standard=\"" + paths[0]
									+ "\" and section=\"" + paths[1] + "\"");
							request.setListQuery(query);
							callback.onSuccess(null);
						}

					});
		}

		@Override
		public void responseReceived(GenericResponse response) {
			ListFeed feed = response.getListFeed();
			FlowPanel presentationLayer = new FlowPanel();
			presentationLayer.setStyleName("presentation");
			final FlowPanel vizLayer = new FlowPanel();
			vizLayer.setStyleName("visualization span-8 colborder");
			presentationLayer.add(vizLayer);
			final FlowPanel dataLayer = new FlowPanel();
			dataLayer.setStyleName("data span-15 last");
			presentationLayer.add(dataLayer);
			contentsPanel.add(presentationLayer);

			final ListEntry[] entries = feed.getEntries();

			Runnable runnable = new Runnable() {

				@Override
				public void run() {
					DataTable table = (DataTable) DataTable.create();
					table.addColumn(ColumnType.STRING, "Subject");
					table.addColumn(ColumnType.NUMBER, "No of pass");
					table.addColumn(ColumnType.NUMBER, "No of fail");
					table.addColumn(ColumnType.NUMBER, "Above 90%");
					table.addColumn(ColumnType.NUMBER, "Highest");
					String[] entryLevTags = new String[] { "subject", "cl1",
							"cl2", "cl3" };
					for (ListEntry entry : entries) {
						int rowNo = table.addRow();
						boolean flag = true;
						int i = 0;
						while (flag && i < 4) {
							try {
								table.setValue(rowNo, 0,
										entry.getValue(entryLevTags[i++])
												.toUpperCase().substring(0, 2));
								flag = false;
							} catch (StringIndexOutOfBoundsException e) {
							}
						}
						table.setValue(rowNo, 1,
								Integer.parseInt(entry.getValue("noofpass")));
						table.setValue(rowNo, 2,
								Integer.parseInt(entry.getValue("nooffail")));
						table.setValue(rowNo, 3,
								Integer.parseInt(entry.getValue("above90")));
						table.setValue(rowNo, 4,
								Integer.parseInt(entry.getValue("highest")));
					}

					LineChart.Options options = LineChart.Options.create();
					options.setSize(280, 210);
					vizLayer.add(new LineChart(table, options));
				}
			};
			VisualizationUtils
					.loadVisualizationApi(runnable, LineChart.PACKAGE);

			FlexTable dataTable = new FlexTable();
			// org.sagarius.radix.client.view.Table dataTable = new
			// org.sagarius.radix.client.view.Table();
			// dataTable.addHeader("Subject", SortType.ALPHA_NUMERIC);
			// dataTable.addHeader("No. Pass", SortType.NUMERIC);
			// dataTable.addHeader("No. fail", SortType.NUMERIC);
			// dataTable.addHeader("Above 90%", SortType.NUMERIC);
			// dataTable.addHeader("Highest", SortType.NUMERIC);
			dataTable.setText(0, 0, "Subject");
			dataTable.setText(0, 1, "CL1");
			dataTable.setText(0, 2, "CL2");
			dataTable.setText(0, 3, "CL3");
			dataTable.setText(0, 4, "No. Pass");
			dataTable.setText(0, 5, "No. fail");
			dataTable.setText(0, 6, "Above 90%");
			dataTable.setText(0, 7, "Highest");

			int i = 1;
			for (ListEntry entry : entries) {
				dataTable.setText(i, 0, entry.getValue("subject"));
				dataTable.setText(i, 1, entry.getValue("cl1"));
				dataTable.setText(i, 2, entry.getValue("cl2"));
				dataTable.setText(i, 3, entry.getValue("cl3"));
				dataTable.setText(i, 4, entry.getValue("noofpass"));
				dataTable.setText(i, 5, entry.getValue("nooffail"));
				dataTable.setText(i, 6, entry.getValue("above90"));
				dataTable.setText(i, 7, entry.getValue("highest"));
				i++;
			}
			dataLayer.add(dataTable);
			// dataTable.initialize();
		}
	}

	class AttendanceGadget extends BaseWidget {
		public AttendanceGadget() {
			setTitle("Attendance");
			title.setStyleName("h13 mb3 strong");
			refresh();
		}

		@Override
		protected void initialize(final AsyncCallback<Void> callback) {
			MetaDataRepository.initAttendance(new AsyncCallback<Void>() {

				@Override
				public void onSuccess(Void result) {
					String attendanceKey = MetaDataRepository
							.getAttendanceKey(clazz);
					String requestUri = new QueryBuilder(Utils.sheetVizUrl)
							.setKey(attendanceKey).setSheetName("Consolidated")
							.addFilter("A='Working days' OR A='Average'").get();
					request = new GenericRequest().setType(
							RequestType.VISUALIZATION).setVisualizationQuery(
							requestUri);
					callback.onSuccess(null);
				}

				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}
			});
		}

		@Override
		protected void buildRequest(final AsyncCallback<Void> callback) {
			Runnable runnable = new Runnable() {

				@Override
				public void run() {
					callback.onSuccess(null);
				}
			};
			VisualizationUtils
					.loadVisualizationApi(runnable, LineChart.PACKAGE);
		}

		@Override
		public void responseReceived(GenericResponse gResponse) {
			QueryResponse response = gResponse.getQueryResponse();
			FlowPanel presentationLayer = new FlowPanel();
			presentationLayer.setStyleName("presentation");
			final FlowPanel vizLayer = new FlowPanel();
			vizLayer.setStyleName("visualization span-8 colborder");
			presentationLayer.add(vizLayer);
			final FlowPanel dataLayer = new FlowPanel();
			dataLayer.setStyleName("data span-7 colborder");
			presentationLayer.add(dataLayer);
			final FlowPanel summaryLayer = new FlowPanel();
			summaryLayer.setStyleName("summary span-7 last");
			presentationLayer.add(summaryLayer);
			contentsPanel.add(presentationLayer);

			DataTable table = (DataTable) response.getDataTable();
			int noOfCols = table.getNumberOfColumns();
			int idCol = 0, totalCol = 14, nameCol = 1;
			for (int i = 0; i < noOfCols; i++) {
				String label = table.getColumnLabel(i);
				if (label.equals("Student id")) {
					idCol = i;
				} else if (label.equals("Name")) {
					nameCol = i;
				} else if (label.equals("Total")) {
					totalCol = i;
				}
			}

			DataTable forChart = (DataTable) DataTable.create();
			forChart.addColumn(ColumnType.STRING, "Month");
			forChart.addColumn(ColumnType.NUMBER, "Avg attendance");
			for (int i = 0; i < noOfCols; i++) {
				if (i == idCol || i == totalCol || i == nameCol) {
					continue;
				}
				int row = forChart.addRow();
				forChart.setValue(row, 0, table.getColumnLabel(i));
				String avg = table.getValue(1, i);
				double value = 0;
				if (avg != null) {
					try {
						value = Double.parseDouble(avg);
					} catch (Exception e) {
					}
				}
				forChart.setValue(row, 1, value);
			}
			LineChart.Options options = LineChart.Options.create();
			options.setSize(280, 210);
			options.setLegend(LegendPosition.BOTTOM);
			vizLayer.add(new LineChart(forChart, options));

			org.sagarius.radix.client.view.Table dataTable = new org.sagarius.radix.client.view.Table();
			dataTable.addHeader("Month", SortType.ALPHA_NUMERIC);
			dataTable.addHeader("No working", SortType.NUMERIC);
			dataTable.addHeader("No present", SortType.NUMERIC);
			dataTable.addHeader("Percentage", SortType.NUMERIC);
			for (int i = 0; i < noOfCols; i++) {
				if (i == idCol || i == nameCol || i == totalCol) {
					continue;
				}
				String month = table.getColumnLabel(i);
				dataTable.setText(i, 0, month);
				String work = table.getValue(0, i);
				double workVal = 0;
				if (work != null) {
					try {
						workVal = Double.parseDouble(work);
					} catch (NumberFormatException e) {
					}
				}
				if (workVal > 0) {
					dataTable.setText(i, 1, table.getValue(0, i));
					dataTable.setText(i, 2, table.getValue(1, i));
					dataTable.setText(
							i,
							3,
							Double.parseDouble(table.getValue(1, i))
									/ Double.parseDouble(table.getValue(0, i))
									* 100 + "");
				}
			}
			dataLayer.add(dataTable);
			dataTable.initialize();

			Grid grid = new Grid(3, 2);
			grid.setText(0, 0, "No. of working days");
			grid.setText(1, 0, "No of days present");
			grid.setText(2, 0, "Attendence Percentage");
			String total = table.getValue(0, totalCol);
			double totalVal = 0;
			if (total != null) {
				try {
					totalVal = Double.parseDouble(total);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
			if (totalVal > 0) {
				grid.setText(0, 1, totalVal + "");
				double avg = Double.parseDouble(table.getValue(1, totalCol));
				grid.setText(1, 1, avg + "");
				grid.setText(2, 1, avg / totalVal * 100 + "");
			}
			summaryLayer.add(grid);

		}

	}

	@Override
	public Template getTemplate() {
		return new Template("class/{classId}");
	}

	@Override
	public void handle(Map<String, String> attributes) {
		String classId = attributes.get("classId");
		if (classId.equals(clazz)) {
			return;
		}
		setClazz(classId);
	}

	@Override
	public void handleCrumb() {
		Hyperlink link = (Hyperlink) Breadcrumb.getCrumbAt(1, true);
		link.setTargetHistoryToken("class");
		link.setText("Classes");
		Utils.setActiveMainMenu("class");
		HiddenRowButtons.clear();
		HiddenRowButtons.setRefreshHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				refresh();
			}
		});
	}
}
