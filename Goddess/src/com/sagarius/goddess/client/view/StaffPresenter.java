package com.sagarius.goddess.client.view;

import java.util.List;
import java.util.Map;

import org.sagarius.radix.client.uri.StatefulWidget;
import org.sagarius.radix.client.uri.Template;
import org.sagarius.radix.client.view.Table;
import org.sagarius.radix.client.view.Table.SortType;

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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.QueryResponse;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.LineChart;
import com.sagarius.goddess.client.Utils;
import com.sagarius.goddess.client.ajax.GenericRequest;
import com.sagarius.goddess.client.ajax.GenericResponse;
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
import com.sagarius.goddess.client.utils.MetaDataRepository;
import com.sagarius.goddess.client.utils.visualization.DataTable;
import com.sagarius.goddess.client.utils.visualization.QueryBuilder;
import com.sagarius.goddess.client.utils.visualization.QueryBuilder.Operator;
import com.sagarius.goddess.client.view.util.BoxItemWidget;
import com.sagarius.goddess.client.view.util.Breadcrumb;
import com.sagarius.goddess.client.view.util.HiddenRowButtons;
import com.sagarius.goddess.client.view.util.NewSMSPopup;
import com.sagarius.goddess.client.widgets.BaseWidget;

public class StaffPresenter extends FlowPanel implements StatefulWidget,
		CrumbHandler {
	private String staffId;
	private Label mainLabel;
	private StaffProfileGadget profileGadget;
	private SubjectsGadget subjectGadget;
	private ExamGadget examGadget;

	public StaffPresenter() {
		this(null);
	}

	public StaffPresenter(String staffId) {
		add(mainLabel = new Label("Staff "));
		mainLabel.setStyleName("h18 strong mb2");
		add(profileGadget = new StaffProfileGadget());
		add(new HTML("<hr class='space'/> <hr />"));
		add(subjectGadget = new SubjectsGadget());
		add(new HTML("<hr class='space'/> <hr />"));
		add(examGadget = new ExamGadget());
		add(new HTML("<hr class='space'/> <hr />"));
		if (staffId != null) {
			setStaffId(staffId);
		}
	}

	public void setStaffId(String staffId) {
		this.staffId = staffId;
		refresh();
		((Label) Breadcrumb.getCrumbAt(2, false)).setText(staffId);
	}

	public void refresh() {
		profileGadget.refresh();
		subjectGadget.refresh();
		examGadget.refresh();
	}

	class StaffProfileGadget extends ProfileGadget {
		private final QueryBuilder builder = new QueryBuilder(Utils.sheetVizUrl);

		public StaffProfileGadget() {
			title.setStyleName("h18 mb3 strong");
			Anchor smsLink = new Anchor("Send sms", "javascript:");
			smsLink.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					NewSMSPopup smsWidget = NewSMSPopup.INSTANCE;
					String groupsString = "Staff [" + staffId + "]";
					smsWidget.setRequestUri("/sms?g=" + groupsString
							+ "&type=STAFF&tq=select * where staffid='"
							+ staffId + "'");
					smsWidget.setCapabilities(NewSMSPopup.NONE);
					smsWidget.center();
				}
			});
			smsLink.setStyleName("icon-16-mobile-phone-cast");
			controlsPanel.add(smsLink);
		}

		@Override
		protected void initialize(final AsyncCallback<Void> callback) {
			MetaDataRepository.get(DocumentType.STAFF,
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
					.addFilter(Staff.STAFF_ID, Operator.EQ, staffId).get();
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
			int nameIndex = builder.getMetaData().getFieldMeta(Generic.NAME)
					.getIndex();
			FormWidget formWidget = new FormWidget();
			setImage("/photo?size=200&id=" + staffId, true, 4, 138).setDetails(
					new ProfileDetails(20, true).setFormWidget(formWidget));
			DataTable table = (DataTable) response.getDataTable();
			int columns = table.getNumberOfColumns();

			String staffName = table.getValue(0, nameIndex);
			((Label) Breadcrumb.getCrumbAt(2, false)).setText(staffName);
			if (Utils.currentSchool != null) {
				Window.setTitle("Databox - " + Utils.currentSchool.getName()
						+ " - Staff " + staffName);
			}
			mainLabel.setText(staffName);

			for (int i = 0; i < columns; i++) {
				formWidget.addElement(new FormElement(i % 5 == 0, 4,
						(i + 1) % 5 == 0).setMeta(table.getColumnLabel(i))
						.setValue(table.getValue(0, i)));
			}
		}

	}

	class SubjectsGadget extends BaseWidget {
		private final Anchor downloadLink = new Anchor("Download list",
				"javascript:");
		private QueryBuilder builder = new QueryBuilder(Utils.sheetVizUrl);

		public SubjectsGadget() {
			title.setText("Subjects handled");
			title.setStyleName("h13 mb3 strong");
			downloadLink.setStyleName("icon-16-inbox-download mr4");
			controlsPanel.add(downloadLink);
		}

		@Override
		protected void initialize(final AsyncCallback<Void> callback) {
			MetaDataRepository.get(DocumentType.CSSREL,
					new AsyncCallback<DocumentMetaData>() {

						@Override
						public void onSuccess(DocumentMetaData result) {
							builder.setMetaData(result).addSelections(
									Generic.SUBJECT, Generic.STANDARD,
									Generic.SECTION);
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
					.addFilter(Staff.STAFF_ID, Operator.EQ, staffId).get();
			if (request == null) {
				request = new GenericRequest()
						.setType(RequestType.VISUALIZATION);
			}
			request.setVisualizationQuery(requestUri);
			callback.onSuccess(null);
		}

		@Override
		public void responseReceived(GenericResponse gResponse) {
			downloadLink.setHref(request.getVisualizationQuery()
					+ "&tqx=out:csv");
			QueryResponse response = gResponse.getQueryResponse();
			DataTable dataTable = (DataTable) response.getDataTable();
			int noOfRows = dataTable.getNumberOfRows();
			for (int i = 0; i < noOfRows; i++) {
				String clazz = dataTable.getValue(i, 1, false) + "-"
						+ dataTable.getValue(i, 2);
				Hyperlink clazzLink = new Hyperlink(clazz, "class/" + clazz);
				contentsPanel.add(new BoxItemWidget(6, "icon-32-building",
						clazzLink, i % 4 == 0, (i + 1) % 4 == 0)
						.addControl(new Label(dataTable.getValue(i, 0))));
			}
		}

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
							ListQuery query = ListQuery.newInstance(feedUrl);
							query.setSpreadsheetQuery("staff=\"" + staffId
									+ "\"");
							if (request == null) {
								request = new GenericRequest()
										.setType(RequestType.GDATA_LIST);
							}
							request.setListQuery(query);
							callback.onSuccess(null);
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
			dataLayer.setStyleName("data span-15 last");
			presentationLayer.add(dataLayer);
			contentsPanel.add(presentationLayer);

			final ListEntry[] entries = feed.getEntries();

			Runnable runnable = new Runnable() {

				@Override
				public void run() {
					DataTable table = (DataTable) DataTable.create();
					table.addColumn(ColumnType.STRING, "Class");
					table.addColumn(ColumnType.NUMBER, "No of pass");
					table.addColumn(ColumnType.NUMBER, "No of fail");
					table.addColumn(ColumnType.NUMBER, "Above 90%");
					table.addColumn(ColumnType.NUMBER, "Highest");

					for (ListEntry entry : entries) {
						int rowNo = table.addRow();
						table.setValue(rowNo, 0, entry.getValue("standard")
								+ "-" + entry.getValue("section"));
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

			Table dataTable = new Table();
			dataTable.addHeader("Class", SortType.ALPHA_NUMERIC);
			dataTable.addHeader("No. Pass", SortType.NUMERIC);
			dataTable.addHeader("No. fail", SortType.NUMERIC);
			dataTable.addHeader("Above 90%", SortType.NUMERIC);
			dataTable.addHeader("Highest", SortType.NUMERIC);
			int i = 1;
			for (ListEntry entry : entries) {
				dataTable.setText(i, 0, entry.getValue("standard") + "-"
						+ entry.getValue("section"));
				dataTable.setText(i, 1, entry.getValue("noofpass"));
				dataTable.setText(i, 2, entry.getValue("nooffail"));
				dataTable.setText(i, 3, entry.getValue("above90"));
				dataTable.setText(i, 4, entry.getValue("highest"));
				i++;
			}
			dataLayer.add(dataTable);
			dataTable.initialize();
		}

	}

	@Override
	public Template getTemplate() {
		return new Template("staff/{staffId}");
	}

	@Override
	public void handle(Map<String, String> attributes) {
		String staff = attributes.get("staffId");
		if (staff.equals(staffId)) {
			return;
		}
		setStaffId(staff);
	}

	@Override
	public void handleCrumb() {
		Hyperlink link = (Hyperlink) Breadcrumb.getCrumbAt(1, true);
		link.setTargetHistoryToken("staff");
		link.setText("Staffs");
		com.sagarius.goddess.client.Utils.setActiveMainMenu("staff");
		HiddenRowButtons.clear();
		HiddenRowButtons.setRefreshHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				refresh();
			}
		});
	}
}
