package com.sagarius.goddess.client.view.parent;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.restlet.client.Request;
import org.restlet.client.Response;
import org.restlet.client.data.Method;
import org.sagarius.radix.client.view.Table;
import org.sagarius.radix.client.view.Table.SortType;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.ColumnChart;
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
import com.sagarius.goddess.client.utils.MetaDataRepository;
import com.sagarius.goddess.client.widgets.BaseWidget;

public class StudentPresenter extends FlowPanel {
	private String studentId;
	private StudentProfileGadget profileGadget;
	private boolean isLoaded = false;
	private ExamGadget examGadget;
	private AttendanceGadget attendGadget;

	public StudentPresenter(String studentId, String name) {
		this.studentId = studentId;
		Label mainLabel = new Label(name);
		mainLabel.setStyleName("h18 strong mb2");
		add(mainLabel);
		add(profileGadget = new StudentProfileGadget());
		add(new HTML("<hr class='space'/> <hr />"));
		add(examGadget = new ExamGadget());
		add(new HTML("<hr class='space'/> <hr />"));
		add(attendGadget = new AttendanceGadget());
		add(new HTML("<hr class='space'/> <hr />"));
		Event.addNativePreviewHandler(new NativePreviewHandler() {

			@Override
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				if (event.getTypeInt() == Event.ONKEYPRESS) {
					NativeEvent nativeEvent = event.getNativeEvent();
					if (nativeEvent.getAltKey() && nativeEvent.getCtrlKey()) {
						int code = nativeEvent.getKeyCode();
						if (code == 49) {
							refresh();
						}
					}
				}
			}
		});
	}

	public void load() {
		if (isLoaded) {
			return;
		}
		isLoaded = true;
		refresh();
	}

	public void refresh() {
		profileGadget.refresh();
		examGadget.refresh();
		attendGadget.refresh();
	}

	class StudentProfileGadget extends ProfileGadget {
		public StudentProfileGadget() {
			title.setStyleName("h18 mb3 strong");
		}

		@Override
		protected void initialize(AsyncCallback<Void> callback) {
			request = new GenericRequest().setType(RequestType.HTTP)
					.setHttpRequest(
							new Request(Method.GET, "/parent/student/"
									+ studentId));
			callback.onSuccess(null);
		}

		@Override
		protected void buildRequest(AsyncCallback<Void> callback) {
			callback.onSuccess(null);
		}

		@Override
		public void responseReceived(GenericResponse gResponse) {
			Response response = gResponse.getResponse();
			FormWidget formWidget = new FormWidget();
			setImage("/photo?size=200&id=" + studentId, true, 4, 138)
					.setDetails(
							new ProfileDetails(20, true)
									.setFormWidget(formWidget));
			try {
				String json = response.getEntity().getText();
				JSONObject input = (JSONObject) JSONParser.parse(json);
				Set<String> keys = input.keySet();
				int i = 0;
				for (String key : keys) {
					formWidget.addElement(new FormElement(i % 4 == 0, 4,
							(i + 1) % 4 == 0).setMeta(Utils.capitalize(key))
							.setValue(Utils.getString(input.get(key))));
					i++;
				}
			} catch (IOException e) {
				e.printStackTrace();
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
		protected void buildRequest(AsyncCallback<Void> callback) {
			String requestUri = "/parent/exam/" + docKey + "/" + studentId;
			if (request == null) {
				request = new GenericRequest().setType(RequestType.HTTP);
			}
			request.setHttpRequest(new Request(Method.GET, requestUri));
			callback.onSuccess(null);
		}

		@Override
		public void responseReceived(GenericResponse genericResponse) {
			final Response response = genericResponse.getResponse();
			FlowPanel presentationLayer = new FlowPanel();
			presentationLayer.setStyleName("presentation");
			final FlowPanel vizLayer = new FlowPanel();
			vizLayer.setStyleName("visualization span-8 colborder");
			presentationLayer.add(vizLayer);
			final FlowPanel dataLayer = new FlowPanel();
			dataLayer.setStyleName("data span-7 colborder");
			presentationLayer.add(dataLayer);
			final FlowPanel summaryLayer = new FlowPanel();
			summaryLayer.setStyleName("summary span-6 last");
			presentationLayer.add(summaryLayer);
			contentsPanel.add(presentationLayer);

			Runnable runnable = new Runnable() {
				public void run() {
					try {
						String json = response.getEntity().getText();
						JSONArray input = (JSONArray) JSONParser.parse(json);
						int size = input.size();
						DataTable vizTable = DataTable.create();
						vizTable.addColumn(ColumnType.STRING, "Subject");
						vizTable.addColumn(ColumnType.NUMBER, "Min mark");
						vizTable.addColumn(ColumnType.NUMBER, "Max mark");
						vizTable.addColumn(ColumnType.NUMBER, "Marks obtained");
						Table dataTable = new Table();
						dataTable.addHeader("Subject", SortType.ALPHA_NUMERIC);
						dataTable.addHeader("Min mark", SortType.NUMERIC);
						dataTable.addHeader("Max mark", SortType.NUMERIC);
						dataTable.addHeader("Marks obtained", SortType.NUMERIC);
						for (int i = 0; i < size - 1; i++) {
							JSONArray singleSubject = (JSONArray) input.get(i);
							String subject = Utils.capitalize(Utils
									.getString(singleSubject.get(0)));
							int row = vizTable.addRow();
							try {
								vizTable.setValue(row, 0,
										subject.substring(0, 1));
							} catch (StringIndexOutOfBoundsException e1) {
								System.out.println("Another empty string");
							}
							dataTable.setText(i, 0, subject);
							for (int j = 1; j < 4; j++) {
								try {
									int value = Integer.parseInt(Utils
											.getString(singleSubject.get(j)));
									vizTable.setValue(row, j, value);
									dataTable.setText(i, j, value + "");
								} catch (Exception e) {
								}
							}
						}
						ColumnChart.Options options = ColumnChart.Options
								.create();
						options.setSize(280, 210);
						vizLayer.add(new ColumnChart(vizTable, options));
						dataLayer.add(dataTable);
						dataTable.initialize();

						Grid summaryGrid = new Grid(4, 2);
						summaryGrid.setWidth("240px");
						summaryGrid.setText(0, 0, "Rank");
						summaryGrid.setText(1, 0, "Percentage");
						summaryGrid.setText(2, 0, "Total");
						summaryGrid.setText(3, 0, "Result");
						JSONArray consolidate = (JSONArray) input.get(size - 1);
						summaryGrid.setText(0, 1,
								Utils.getString(consolidate.get(0)));
						summaryGrid.setText(1, 1,
								Utils.getString(consolidate.get(1)));
						summaryGrid.setText(2, 1,
								Utils.getString(consolidate.get(2)));
						summaryGrid.setText(3, 1,
								Utils.getString(consolidate.get(3)));
						summaryLayer.add(summaryGrid);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			VisualizationUtils.loadVisualizationApi(runnable,
					ColumnChart.PACKAGE);
		}
	}

	class AttendanceGadget extends BaseWidget {
		public AttendanceGadget() {
			super();
			title.setText("Attendance");
			title.setStyleName("h13 mb3 strong");
		}

		@Override
		protected void initialize(AsyncCallback<Void> callback) {
			String requestUri = "/parent/attendance/" + studentId;
			if (request == null) {
				request = new GenericRequest().setType(RequestType.HTTP);
			}
			request.setHttpRequest(new Request(Method.GET, requestUri));
			callback.onSuccess(null);
		}

		@Override
		protected void buildRequest(AsyncCallback<Void> callback) {
			callback.onSuccess(null);
		}

		@Override
		public void responseReceived(GenericResponse genericResponse) {
			final Response response = genericResponse.getResponse();
			FlowPanel presentationLayer = new FlowPanel();
			presentationLayer.setStyleName("presentation");
			final FlowPanel vizLayer = new FlowPanel();
			vizLayer.setStyleName("visualization span-8 colborder");
			presentationLayer.add(vizLayer);
			final FlowPanel dataLayer = new FlowPanel();
			dataLayer.setStyleName("data span-7 colborder");
			presentationLayer.add(dataLayer);
			final FlowPanel summaryLayer = new FlowPanel();
			summaryLayer.setStyleName("summary span-6 last");
			presentationLayer.add(summaryLayer);
			contentsPanel.add(presentationLayer);

			Runnable runnable = new Runnable() {
				public void run() {
					try {
						String json = response.getEntity().getText();
						JSONArray input = (JSONArray) JSONParser.parse(json);
						int size = input.size();
						DataTable vizTable = DataTable.create();
						vizTable.addColumn(ColumnType.STRING, "Month");
						vizTable.addColumn(ColumnType.NUMBER, "Percentage");
						Table dataTable = new Table();
						dataTable.addHeader("Month", SortType.ALPHA_NUMERIC);
						dataTable.addHeader("No working", SortType.NUMERIC);
						dataTable.addHeader("No present", SortType.NUMERIC);
						dataTable.addHeader("Percentage", SortType.NUMERIC);
						for (int i = 0; i < size - 1; i++) {
							JSONArray singleMonth = (JSONArray) input.get(i);
							String month = Utils.capitalize(Utils
									.getString(singleMonth.get(0)));
							int row = vizTable.addRow();
							vizTable.setValue(row, 0, month.substring(0, 1));
							dataTable.setText(i, 0, month);

							try {
								int workingDays = Integer.parseInt(Utils
										.getString(singleMonth.get(1)));
								if (workingDays == 0) {
									continue;
								}
								int presentDays = Integer.parseInt(Utils
										.getString(singleMonth.get(2)));
								double percentage = presentDays / workingDays
										* 100;
								vizTable.setValue(row, 1, percentage);
								dataTable.setText(i, 1, workingDays + "");
								dataTable.setText(i, 2, presentDays + "");
								dataTable.setText(i, 3, percentage + "");
							} catch (Exception e) {
							}
						}
						LineChart.Options options = LineChart.Options.create();
						options.setSize(280, 210);
						vizLayer.add(new LineChart(vizTable, options));
						dataLayer.add(dataTable);
						dataTable.initialize();

						Grid summaryGrid = new Grid(3, 2);
						summaryGrid.setText(0, 0, "No. of working days");
						summaryGrid.setText(1, 0, "No of days present");
						summaryGrid.setText(2, 0, "Attendence Percentage");
						JSONArray consolidate = (JSONArray) input.get(size - 1);
						try {
							int total = Integer.parseInt(Utils
									.getString(consolidate.get(0)));
							if (total > 0) {
								summaryGrid.setText(0, 1, total + "");
								int present = Integer.parseInt(Utils
										.getString(consolidate.get(1)));
								summaryGrid.setText(1, 1, present + "");
								double percent = total / present * 100;
								summaryGrid.setText(2, 1, percent + "");
							}
						} catch (Exception e) {
						}
						summaryLayer.add(summaryGrid);
					} catch (Exception e) {

					}
				}
			};
			VisualizationUtils
					.loadVisualizationApi(runnable, LineChart.PACKAGE);
		}
	}
}
