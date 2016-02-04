package com.sagarius.goddess.client.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sagarius.radix.client.uri.StatefulWidget;
import org.sagarius.radix.client.uri.Template;
import org.sagarius.radix.client.view.Table;
import org.sagarius.radix.client.view.Table.SortType;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gwt.gdata.client.spreadsheet.ListEntry;
import com.google.gwt.gdata.client.spreadsheet.ListFeed;
import com.google.gwt.gdata.client.spreadsheet.ListQuery;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
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
import com.google.gwt.visualization.client.visualizations.ColumnChart;
import com.google.gwt.visualization.client.visualizations.LineChart;
import com.sagarius.goddess.client.Utils;
import com.sagarius.goddess.client.ajax.CacheRequest;
import com.sagarius.goddess.client.ajax.CacheType;
import com.sagarius.goddess.client.ajax.GenericRequest;
import com.sagarius.goddess.client.ajax.GenericResponse;
import com.sagarius.goddess.client.ajax.MultiRequestCallback;
import com.sagarius.goddess.client.ajax.RequestType;
import com.sagarius.goddess.client.gadgets.ProfileGadget;
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

public class StudentPresenter extends FlowPanel implements StatefulWidget,
		CrumbHandler {
	private String studentId;
	private Label mainLabel;
	private String clazz;
	private StudentProfileGadget profileGadget;
	private RelatedStaffGadget staffGadget;
	private AttendanceGadget attendanceGadget;
	private ExamGadget examGadget;

	public StudentPresenter() {
		this(null);
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
		profileGadget.refresh();
		((Label) Breadcrumb.getCrumbAt(1, false)).setText(studentId);
	}

	public StudentPresenter(String studentId) {
		add(mainLabel = new Label("Student "));
		mainLabel.setStyleName("h18 strong mb2");
		add(profileGadget = new StudentProfileGadget());
		add(new HTML("<hr class='space'/> <hr />"));
		if (studentId != null) {
			setStudentId(studentId);
		}
	}

	class StudentProfileGadget extends ProfileGadget {
		private final Anchor smsLink = new Anchor("Send message to parent",
				"javascript:");
		private final QueryBuilder builder = new QueryBuilder(Utils.sheetVizUrl);

		public StudentProfileGadget() {
			title.setStyleName("h18 mb3 strong");
			smsLink.setStyleName("icon-16-mobile-phone-cast");
			smsLink.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					NewSMSPopup smsWidget = NewSMSPopup.INSTANCE;
					String groupsString = "Student [" + studentId + "]";
					smsWidget.setRequestUri("/sms?g=" + groupsString
							+ "&type=PARENT&tq=select * where studentid='"
							+ studentId + "'");
					smsWidget.setCapabilities(NewSMSPopup.NONE);
					smsWidget.center();
				}
			});
			controlsPanel.add(smsLink);
		}

		@Override
		protected void initialize(final AsyncCallback<Void> callback) {
			MetaDataRepository.get(DocumentType.SINGLE_STUDENT,
					new AsyncCallback<DocumentMetaData>() {

						@Override
						public void onSuccess(DocumentMetaData result) {
							builder.setMetaData(result);
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
			String requestUri = builder.removeFilters()
					.addFilter(Student.STUDENT_ID, Operator.EQ, studentId)
					.get();
			if (request == null) {
				request = new GenericRequest()
						.setType(RequestType.VISUALIZATION);
			}
			request.setVisualizationQuery(requestUri);
			callback.onSuccess(null);
		}

		@Override
		public void responseReceived(GenericResponse gResponse) {
			QueryResponse response = gResponse.getQueryResponse();
			DocumentMetaData metaData = builder.getMetaData();
			FormWidget formWidget = new FormWidget();
			setImage("/photo?size=200&id=" + studentId, true, 4, 138)
					.setDetails(
							new ProfileDetails(20, true)
									.setFormWidget(formWidget));
			DataTable table = (DataTable) response.getDataTable();
			int columns = table.getNumberOfColumns();
			String standard = table.getValue(0,
					metaData.getFieldMeta(Generic.STANDARD).getIndex());
			String section = table.getValue(0,
					metaData.getFieldMeta(Generic.SECTION).getIndex());
			onClazzLoad(standard + "-" + section);

			String studentName = table.getValue(0,
					metaData.getFieldMeta(Generic.NAME).getIndex());
			((Label) Breadcrumb.getCrumbAt(1, false)).setText(studentName);
			if (Utils.currentSchool != null) {
				Window.setTitle("Databox - " + Utils.currentSchool.getName()
						+ " - Student " + studentName);
			}
			mainLabel.setText(studentName);

			for (int i = 0; i < columns; i++) {
				final String columnLabel = table.getColumnLabel(i);
				formWidget.addElement(new FormElement(i % 5 == 0, 4,
						(i + 1) % 5 == 0).setMeta(columnLabel).setValue(
						table.getValue(0, i)));
			}
		}
	}

	public void onClazzLoad(String clazz) {
		this.clazz = clazz;
		if (examGadget == null) {
			add(staffGadget = new RelatedStaffGadget());
			add(new HTML("<hr class='space'/> <hr />"));
			add(examGadget = new ExamGadget());
			add(new HTML("<hr class='space'/> <hr />"));
			add(attendanceGadget = new AttendanceGadget());
			add(new HTML("<hr class='space'/> <hr />"));
		}
		staffGadget.refresh();
		attendanceGadget.refresh();
		examGadget.refresh();
	}

	class ExamGadget extends BaseWidget {
		protected String docKey;

		public ExamGadget() {
			title.setText("Examination Summary");
			title.setStyleName("h13 mb3 strong");
		}

		@Override
		protected void initialize(final AsyncCallback<Void> callback) {
			MetaDataRepository.getDocuments(DocumentType.EXAMINATION,
					new AsyncCallback<List<Document>>() {

						@Override
						public void onSuccess(List<Document> result) {
							header.setStyleName("form");
							final ListBox examSelect = new ListBox(false);
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
							header.add(new FormWidget()
									.addElement(new FormElement(false, 6)
											.setName("Examination").setValue(
													examSelect)));
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
			NewSpreadsheetUtils.getWorkSheetByTitle(docKey, clazz,
					new AsyncCallback<String>() {

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
							ListQuery query = ListQuery.newInstance(feedUrl);
							StringBuilder builder = new StringBuilder();
							String[] reqRows = new String[] { "Minimum marks",
									"Maximum marks", studentId, "Average",
									"CL1", "CL2", "CL3" };
							for (String reqRow : reqRows) {
								builder.append("studentid=\"" + reqRow
										+ "\" or ");
							}
							int length = builder.length();
							builder.delete(length - 4, length);
							query.setSpreadsheetQuery(builder.toString());
							if (request == null) {
								request = new GenericRequest()
										.setType(RequestType.GDATA_LIST);
							}
							request.setListQuery(query);
							callback.onSuccess(null);
						}

						@Override
						public void onFailure(Throwable caught) {
							callback.onFailure(caught);
						}
					});
		}

		@Override
		public void responseReceived(GenericResponse genericResponse) {
			ListFeed feed = genericResponse.getListFeed();
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

			ListEntry[] entries = feed.getEntries();
			String rank = null;
			String percentage = null;
			String total = null;
			String maxTotal = null;
			String result = null;
			ListEntry minMarkEntry = null;
			ListEntry maxMarkEntry = null;
			ListEntry studMarkEntry = null;
			List<String> subjList = null;
			final Map<String, String> realSubjectNames = new HashMap<String, String>();
			for (ListEntry listEntry : entries) {
				String admNo = listEntry.getValue("studentid");
				if (admNo.equals("Minimum Marks")) {
					minMarkEntry = listEntry;
				} else if (admNo.equals("Maximum Marks")) {
					maxMarkEntry = listEntry;
					maxTotal = listEntry.getValue("total");
				} else if (admNo.equals(studentId)) {
					rank = listEntry.getValue("rank");
					percentage = listEntry.getValue("percentage");
					total = listEntry.getValue("total");
					result = listEntry.getValue("passed");
					studMarkEntry = listEntry;
					subjList = listEntry.getTags();
					subjList.remove("studentid");
					subjList.remove("name");
					subjList.remove("total");
					subjList.remove("passed");
					subjList.remove("percentage");
					subjList.remove("result");
					subjList.remove("rank");
				} else if (admNo.startsWith("CL")) {
					List<String> tags = listEntry.getTags();
					for (String tag : tags) {
						String value = listEntry.getValue(tag);
						if (tag.startsWith("_") && !Utils.isEmpty(value)) {
							realSubjectNames.put(tag, value);
						}
					}
				}
			}

			final List<String> subjects = subjList;
			final ListEntry maxMark = maxMarkEntry;
			final ListEntry minMark = minMarkEntry;
			final ListEntry studMark = studMarkEntry;
			Runnable runnable = new Runnable() {

				@Override
				public void run() {
					DataTable table = (DataTable) DataTable.create();
					table.addColumn(ColumnType.STRING, "Subject");
					table.addColumn(ColumnType.NUMBER, "Max mark");
					table.addColumn(ColumnType.NUMBER, "Obtained");
					for (String subject : subjects) {
						int rowNo = table.addRow();
						if (subject.startsWith("_")) {
							table.setValue(rowNo, 0,
									realSubjectNames.get(subject).toUpperCase()
											.substring(0, 2));
						} else {
							table.setValue(rowNo, 0, subject.toUpperCase()
									.substring(0, 2));
						}
						table.setValue(rowNo, 1,
								Integer.parseInt(maxMark.getValue(subject)));
						table.setValue(rowNo, 2,
								Integer.parseInt(studMark.getValue(subject)));
					}
					ColumnChart.Options options = ColumnChart.Options.create();
					options.setSize(280, 210);
					vizLayer.add(new ColumnChart(table, options));
				}
			};
			VisualizationUtils.loadVisualizationApi(runnable,
					ColumnChart.PACKAGE);

			Table aTable = new Table();
			aTable.addHeader("Subject", SortType.ALPHA_NUMERIC);
			aTable.addHeader("Min mark", SortType.NUMERIC);
			aTable.addHeader("Max mark", SortType.NUMERIC);
			aTable.addHeader("Obtained", SortType.NUMERIC);

			int i = 1;
			for (String subject : subjects) {
				try {
					if (subject.startsWith("_")) {
						aTable.setText(i, 0, realSubjectNames.get(subject));
					} else {
						aTable.setText(i, 0, Utils.capitalize(subject));
					}
					aTable.setText(i, 1, minMark.getValue(subject));
					aTable.setText(i, 2, maxMark.getValue(subject));
					aTable.setText(i, 3, studMark.getValue(subject));
				} catch (JavaScriptException e) {
				}
				i++;
			}
			dataLayer.add(aTable);
			aTable.initialize();

			Grid summaryGrid = new Grid(4, 2);
			summaryGrid.setWidth("240px");
			summaryGrid.setText(0, 0, "Rank");
			summaryGrid.setText(1, 0, "Percentage");
			summaryGrid.setText(2, 0, "Total");
			summaryGrid.setText(3, 0, "Result");
			summaryGrid.setText(0, 1, rank);
			summaryGrid.setText(1, 1, percentage);
			summaryGrid.setText(2, 1, total + "/" + maxTotal);
			summaryGrid.setText(3, 1,
					result.toLowerCase().equals("true") ? "Pass" : "Fail");
			summaryLayer.add(summaryGrid);
		}

	}

	class AttendanceGadget extends BaseWidget {
		public AttendanceGadget() {
			title.setText("Attendance");
			title.setStyleName("h13 mb3 strong");
		}

		@Override
		protected void initialize(final AsyncCallback<Void> callback) {
			MetaDataRepository.initAttendance(new AsyncCallback<Void>() {

				@Override
				public void onSuccess(Void result) {
					String attendanceKey = MetaDataRepository
							.getAttendanceKey(clazz);
					String requestUri = new QueryBuilder(Utils.sheetVizUrl)
							.setKey(attendanceKey)
							.setSheetName("Consolidated")
							.addFilter(
									"A='Working days' OR A='" + studentId + "'")
							.get();
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
			VisualizationUtils.loadVisualizationApi(new Runnable() {

				@Override
				public void run() {
					callback.onSuccess(null);
				}
			}, LineChart.PACKAGE);

		}

		@Override
		public void responseReceived(GenericResponse genericResponse) {
			QueryResponse response = genericResponse.getQueryResponse();
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
				forChart.setValue(row, 0,
						table.getColumnLabel(i).substring(0, 2) + "");
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

	class RelatedStaffGadget extends BaseWidget {
		private final Anchor messageLink = new Anchor("Send message",
				"javascript:");
		private final Anchor smsLink = new Anchor("Send message", "javascript:");
		private final QueryBuilder builder = new QueryBuilder(Utils.sheetVizUrl);
		private Map<String, String> staffMap;

		public RelatedStaffGadget() {
			title.setText("Related teachers");
			title.setStyleName("h13 mb3 strong");
			messageLink.setStyleName("icon-16-mail--arrow mr4");
			smsLink.setStyleName("icon-16-mobile-phone-cast mr4");
			smsLink.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					NewSMSPopup smsWidget = NewSMSPopup.INSTANCE;
					String[] stdSec = clazz.split("-");
					String groupString = "Staffs of Class " + stdSec[0] + "-"
							+ stdSec[1];
					smsWidget.setRequestUri("/sms?g=" + groupString
							+ "&rel=1&type=STAFF&tq=select * where standard='"
							+ stdSec[0] + "' and section='" + stdSec[1] + "'");
					smsWidget.setCapabilities(NewSMSPopup.NONE);
					smsWidget.center();
				}
			});
			controlsPanel.add(messageLink);
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
							builder.setMetaData(
									(DocumentMetaData) result.getOutput())
									.addSelections(Staff.STAFF_ID,
											Generic.SUBJECT);
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
		public void responseReceived(GenericResponse genericResponse) {
			QueryResponse response = genericResponse.getQueryResponse();
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
				Image staffImage = new Image("/photo?size=72&id=" + staffId);
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
		}

	}

	@Override
	public Template getTemplate() {
		return new Template("student/{studId}");
	}

	@Override
	public void handle(Map<String, String> attributes) {
		String studId = attributes.get("studId");
		if (studId.equals(studentId)) {
			return;
		}
		setStudentId(studId);
	}

	@Override
	public void handleCrumb() {
		Breadcrumb.ensureMaxLength(2);
		if (Utils.currentSchool != null) {
			Window.setTitle("Databox - " + Utils.currentSchool.getName()
					+ " - Exam ");
		}
		com.sagarius.goddess.client.Utils.setActiveMainMenu("none");
		HiddenRowButtons.clear();
		HiddenRowButtons.setRefreshHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				profileGadget.refresh();
			}
		});
	}
}
