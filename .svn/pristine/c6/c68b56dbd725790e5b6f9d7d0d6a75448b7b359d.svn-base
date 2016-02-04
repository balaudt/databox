package com.sagarius.goddess.client.view;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.restlet.client.Request;
import org.restlet.client.data.Method;
import org.sagarius.radix.client.collect.Filter;
import org.sagarius.radix.client.collect.LinkedHashSetMultiMap;
import org.sagarius.radix.client.collect.MultiMap;
import org.sagarius.radix.client.collect.Operator;
import org.sagarius.radix.client.collect.Taffy;
import org.sagarius.radix.client.uri.StatefulWidget;
import org.sagarius.radix.client.uri.Template;
import org.sagarius.radix.client.util.JSMap;
import org.sagarius.radix.client.view.Table;
import org.sagarius.radix.client.view.Table.SortType;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.LegendPosition;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.ImageLineChart;
import com.sagarius.goddess.client.Utils;
import com.sagarius.goddess.client.ajax.CacheRequest;
import com.sagarius.goddess.client.ajax.CacheType;
import com.sagarius.goddess.client.ajax.GenericRequest;
import com.sagarius.goddess.client.ajax.GenericResponse;
import com.sagarius.goddess.client.ajax.Listener;
import com.sagarius.goddess.client.ajax.RequestSynchronizer;
import com.sagarius.goddess.client.ajax.RequestSynchronizer.RequestGenerator;
import com.sagarius.goddess.client.ajax.RequestType;
import com.sagarius.goddess.client.gadgets.ProfileGadget.ProfileDetails.FormWidget;
import com.sagarius.goddess.client.gadgets.ProfileGadget.ProfileDetails.FormWidget.FormElement;
import com.sagarius.goddess.client.model.enumerations.DocumentType;
import com.sagarius.goddess.client.present.CrumbHandler;
import com.sagarius.goddess.client.utils.DocumentMetaData;
import com.sagarius.goddess.client.utils.Fields.Examination;
import com.sagarius.goddess.client.utils.Fields.Generic;
import com.sagarius.goddess.client.utils.Fields.Staff;
import com.sagarius.goddess.client.utils.visualization.DataTable;
import com.sagarius.goddess.client.utils.visualization.QueryBuilder;
import com.sagarius.goddess.client.view.util.Breadcrumb;
import com.sagarius.goddess.client.view.util.HiddenRowButtons;
import com.sagarius.goddess.client.widgets.BaseWidget;
import com.sagarius.goddess.client.widgets.StructuredWidget;

public class ExamPresenter extends BaseWidget implements CrumbHandler,
		StatefulWidget {
	private Taffy relations = Taffy.create();
	private Taffy markList = Taffy.create();
	private MultiMap<String, String> clazzCategories = new LinkedHashSetMultiMap<String, String>();
	private MultiMap<String, String> staffCategories = new LinkedHashSetMultiMap<String, String>();
	private String docKey;
	private DocumentMetaData cssMeta;
	private DocumentMetaData clazzMeta;
	private Map<String, String> staffMap;
	private String examName;

	public ExamPresenter() {
		refresh();
	}

	public void setDocKey(String docKey) {
		this.docKey = docKey;
		((Label) Breadcrumb.getCrumbAt(2, false)).setText(docKey);
		refresh();
	}

	@Override
	protected void initialize(final AsyncCallback<Void> callback) {
		RequestSynchronizer synchronizer = new RequestSynchronizer(
				new GenericRequest().setType(RequestType.DOCUMENT_META)
						.setTypeQuery(DocumentType.CLASS))
				.add(new RequestGenerator() {

					@Override
					public GenericRequest getRequest(GenericResponse response) {
						clazzMeta = response.getMetaData();
						String requestUri = new QueryBuilder(Utils.sheetVizUrl)
								.setMetaData(clazzMeta)
								.addSelection(Generic.STANDARD)
								.addSelection(Generic.SECTION)
								.addSelection(Generic.CATEGORY).get();
						return new GenericRequest().setType(
								RequestType.VISUALIZATION)
								.setVisualizationQuery(requestUri);
					}
				}).add(new RequestGenerator() {

					@Override
					public GenericRequest getRequest(GenericResponse response) {
						DataTable table = (DataTable) response
								.getQueryResponse().getDataTable();
						int rows = table.getNumberOfRows();
						for (int j = 0; j < rows; j++) {
							clazzCategories.put(
									table.getValue(j, 2),
									table.getValue(j, 0).concat("-")
											.concat(table.getValue(j, 1)));
						}
						return new GenericRequest().setType(
								RequestType.DOCUMENT_META).setTypeQuery(
								DocumentType.CSSREL);
					}
				}).add(new RequestGenerator() {

					@Override
					public GenericRequest getRequest(GenericResponse response) {
						cssMeta = response.getMetaData();
						String vizQuery = new QueryBuilder(Utils.sheetVizUrl)
								.setMetaData(cssMeta).get();
						return new GenericRequest().setType(
								RequestType.VISUALIZATION)
								.setVisualizationQuery(vizQuery);
					}
				}).add(new RequestGenerator() {

					@Override
					public GenericRequest getRequest(GenericResponse response) {
						DataTable table = (DataTable) response
								.getQueryResponse().getDataTable();
						int standard = cssMeta.getFieldMeta(Generic.STANDARD)
								.getIndex();
						int section = cssMeta.getFieldMeta(Generic.SECTION)
								.getIndex();
						int subject = cssMeta.getFieldMeta(Generic.SUBJECT)
								.getIndex();
						int staffId = cssMeta.getFieldMeta(Staff.STAFF_ID)
								.getIndex();
						int rows = table.getNumberOfRows();
						Set<Entry<String, Collection<String>>> entrySet = clazzCategories
								.entrySet();
						for (int i = 0; i < rows; i++) {
							String std = table.getValue(i, standard);
							String sec = table.getValue(i, section);
							String clazz = std.concat("-").concat(sec);
							String category = null;
							for (Entry<String, Collection<String>> entry : entrySet) {
								if (entry.getValue().contains(clazz)) {
									category = entry.getKey();
								}
							}
							relations.insert(JSMap.create().put("std", std)
									.put("sub", table.getValue(i, subject))
									.put("tid", table.getValue(i, staffId))
									.put("sec", sec).put("ccat", category));
						}
						return new GenericRequest().setType(RequestType.CACHE)
								.setCacheRequest(
										new CacheRequest()
												.setType(CacheType.STAFF_MAP));
					}
				}).add(new RequestGenerator() {

					@SuppressWarnings("unchecked")
					@Override
					public GenericRequest getRequest(GenericResponse response) {
						staffMap = (Map<String, String>) response.getOutput();
						String vizQuery = new QueryBuilder(Utils.sheetVizUrl)
								.setKey(docKey).get();
						return new GenericRequest().setType(
								RequestType.VISUALIZATION)
								.setVisualizationQuery(vizQuery);
					}
				}).add(new RequestGenerator() {

					@Override
					public GenericRequest getRequest(GenericResponse response) {
						DataTable table = (DataTable) response
								.getQueryResponse().getDataTable();
						int columns = table.getNumberOfColumns();
						Map<String, Integer> colIndices = new HashMap<String, Integer>();
						for (int i = 0; i < columns; i++) {
							String columnLabel = table.getColumnLabel(i);
							colIndices
									.put(columnLabel.toLowerCase()
											.replaceAll(" ", "")
											.replaceAll("_", ""), i);
						}
						int rows = table.getNumberOfRows();
						for (int i = 0; i < rows; i++) {
							String staffId = table.getValue(i,
									colIndices.get("staff"), false);
							JSMap singleElement = JSMap
									.create()
									.put("std",
											table.getValue(i, colIndices
													.get(Generic.STANDARD)))
									.put("sec",
											table.getValue(i, colIndices
													.get(Generic.SECTION)))
									.put("t", staffMap.get(staffId))
									.put("s",
											table.getValue(i, colIndices
													.get(Generic.SUBJECT)))
									.put("h",
											table.getIntValue(i, colIndices
													.get(Examination.HIGHEST)))
									.put("p",
											table.getIntValue(
													i,
													colIndices
															.get(Examination.NO_OF_PASS)))
									.put("f",
											table.getIntValue(
													i,
													colIndices
															.get(Examination.NO_OF_FAIL)))
									.put("90",
											table.getIntValue(i, colIndices
													.get(Examination.ABOVE_90)))
									.put("tid", staffId);
							singleElement.put("c1",
									table.getValue(i, colIndices.get("cl1")));
							singleElement.put("c2",
									table.getValue(i, colIndices.get("cl2")));
							singleElement.put("c3",
									table.getValue(i, colIndices.get("cl3")));
							markList.insert(singleElement);
						}
						return new GenericRequest().setType(RequestType.HTTP)
								.setHttpRequest(
										new Request(Method.GET,
												"/documents?ccc=" + docKey));
					}
				}).add(new RequestGenerator() {

					@Override
					public GenericRequest getRequest(GenericResponse response) {
						try {
							String json = response.getResponse().getEntity()
									.getText();
							com.sagarius.goddess.client.model.Document document = new com.sagarius.goddess.client.model.Document(
									json);
							examName = document.getParam1();
							((Label) Breadcrumb.getCrumbAt(2, false))
									.setText(examName);
							if (Utils.currentSchool != null) {
								Window.setTitle("Databox - "
										+ Utils.currentSchool.getName()
										+ " - Exam " + examName);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						return new GenericRequest().setType(
								RequestType.DOCUMENT_META).setTypeQuery(
								DocumentType.STAFF);
					}
				}).add(new RequestGenerator() {

					@Override
					public GenericRequest getRequest(GenericResponse response) {
						DocumentMetaData staffMeta = response.getMetaData();
						String requestUri = new QueryBuilder(Utils.sheetVizUrl)
								.setMetaData(staffMeta)
								.addSelection(Staff.STAFF_ID)
								.addSelection(Generic.CATEGORY).get();
						return new GenericRequest().setType(
								RequestType.VISUALIZATION)
								.setVisualizationQuery(requestUri);
					}
				}).setCallback(new AsyncCallback<GenericResponse>() {

					@Override
					public void onSuccess(GenericResponse result) {
						DataTable table = (DataTable) result.getQueryResponse()
								.getDataTable();
						int rows = table.getNumberOfRows();
						for (int j = 0; j < rows; j++) {
							staffCategories.put(table.getValue(j, 1),
									table.getValue(j, 0));
						}

						statusMessage.setText("");
						callback.onSuccess(null);
					}

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}
				});
		synchronizer.addListener(new Listener() {

			@Override
			public void stateChanged(short status) {
				switch (status) {
				case 2:
					statusMessage.setText("Getting relation data");
					break;
				case 4:
					statusMessage.setText("Getting staff data");
					break;
				case 5:
					statusMessage.setText("Getting exam data");
				case 7:
					statusMessage.setText("Getting staff data");
				}
			}
		});
		synchronizer.start();
		statusMessage.setText("Getting class meta data");
	}

	@Override
	protected void buildRequest(AsyncCallback<Void> callback) {
		request = new GenericRequest().setType(RequestType.HTTP)
				.setHttpRequest(new Request(Method.GET, "/school"));
		callback.onSuccess(null);
	}

	@Override
	public void responseReceived(GenericResponse genericResponse) {
		TabPanel theMainPanel = new TabPanel();
		theMainPanel.setStyleName("tabp-preset-1");
		theMainPanel.add(new ExamConsolidateView(), "Custom reports");
		theMainPanel.add(new ClassAnalysis(), "Class Reports");
		theMainPanel.add(new StaffAnalysis(), "Staff Reports");
		theMainPanel.add(new Charts(), "Charts");
		theMainPanel.selectTab(0);
		contentsPanel.add(theMainPanel);
	}

	@Override
	public void handleCrumb() {
		Hyperlink link = (Hyperlink) Breadcrumb.getCrumbAt(1, true);
		link.setTargetHistoryToken("exam");
		link.setText("Exams");
		com.sagarius.goddess.client.Utils.setActiveMainMenu("exam");
		HiddenRowButtons.clear();
		HiddenRowButtons.setRefreshHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				refresh();
			}
		});
	}

	@Override
	public Template getTemplate() {
		return new Template("newexam/{examId}");
	}

	@Override
	public void handle(Map<String, String> attributes) {
		docKey = attributes.get("examId");
	}

	class StaffAnalysis extends FlowPanel {
		private TabPanel categorySwitcher = new TabPanel();

		public StaffAnalysis() {
			Set<String> categories = staffCategories.keySet();
			for (String category : categories) {
				categorySwitcher.add(new SingleStaffAnalysis(category),
						category);
			}
			categorySwitcher.setStyleName("tabp-preset-2");
			categorySwitcher
					.addSelectionHandler(new SelectionHandler<Integer>() {

						@Override
						public void onSelection(SelectionEvent<Integer> event) {
							SingleStaffAnalysis clazzPanel = (SingleStaffAnalysis) categorySwitcher
									.getWidget(event.getSelectedItem());
							if (!clazzPanel.isLoaded()) {
								clazzPanel.refresh();
							}
						}
					});
			categorySwitcher.selectTab(0);
			add(categorySwitcher);
		}

	}

	class SingleStaffAnalysis extends StructuredWidget {
		private String category;

		public SingleStaffAnalysis(String category) {
			this.category = category;
		}

		@Override
		public void responseReceived(GenericResponse genericResponse) {
			Collection<String> staffs = staffCategories.get(category);
			Label mainLabel = new Label(examName.concat(" - Staff-wise report"));
			mainLabel.setStyleName("h18 strong mb2");
			contentsPanel.add(mainLabel);
			FlowPanel table = new FlowPanel();
			Map<String, Boolean> hasData = new HashMap<String, Boolean>();
			Map<String, FlexTable> staffTables = new HashMap<String, FlexTable>();
			Map<String, Label> staffLabels = new HashMap<String, Label>();
			for (String staff : staffs) {
				Label staffLabel = new Label(staffMap.get(staff));
				staffLabel.setStyleName("heading-preset-1");
				table.add(staffLabel);
				FlexTable staffTable = new FlexTable();
				staffTable.setHTML(0, 0, "<b>Class</b>");
				staffTable.setHTML(0, 1, "<b>Subject</b>");
				staffTable.setHTML(0, 2, "<b>No of Pass</b>");
				staffTable.setHTML(0, 3, "<b>No of Failures</b>");
				staffTable.setHTML(0, 4, "<b>Above 90%</b>");
				staffTable.setHTML(0, 5, "<b>Highest mark</b>");
				staffTable.setStyleName("mb18");
				table.add(staffTable);
				table.add(new HTML("<br />"));
				staffTables.put(staff, staffTable);
				staffLabels.put(staff, staffLabel);
				hasData.put(staff, false);
			}
			JsArray<JavaScriptObject> consolidation = markList.get(Filter
					.create());
			int length = consolidation.length();
			for (int i = 0; i < length; i++) {
				JSMap current = consolidation.get(i).cast();
				String staffId = current.getString("tid");
				FlexTable staffTable = staffTables.get(staffId);
				if (staffTable == null) {
					continue;
				}
				hasData.put(staffId, true);
				int rowCount = staffTable.getRowCount();
				staffTable.setText(rowCount, 0, current.getString("std") + "-"
						+ current.getString("sec"));
				staffTable.setText(rowCount, 1, current.getString("s"));
				staffTable.setText(rowCount, 2, current.getInt("p") + "");
				staffTable.setText(rowCount, 3, current.getInt("f") + "");
				staffTable.setText(rowCount, 4, current.getInt("90") + "");
				staffTable.setText(rowCount, 5, current.getInt("h") + "");
			}
			for (String staff : staffs) {
				if (!hasData.get(staff)) {
					table.remove(staffTables.get(staff));
					table.remove(staffLabels.get(staff));
				}
			}
			contentsPanel.add(table);
		}
	}

	class ClassAnalysis extends FlowPanel {
		private TabPanel categorySwitcher = new TabPanel();

		public ClassAnalysis() {
			Set<String> categories = clazzCategories.keySet();
			for (String category : categories) {
				categorySwitcher.add(new SingleClassAnalysis(category),
						category);
			}
			categorySwitcher.setStyleName("tabp-preset-2");
			categorySwitcher
					.addSelectionHandler(new SelectionHandler<Integer>() {

						@Override
						public void onSelection(SelectionEvent<Integer> event) {
							SingleClassAnalysis clazzPanel = (SingleClassAnalysis) categorySwitcher
									.getWidget(event.getSelectedItem());
							if (!clazzPanel.isLoaded()) {
								clazzPanel.refresh();
							}
						}
					});
			categorySwitcher.selectTab(0);
			add(categorySwitcher);
		}
	}

	class SingleClassAnalysis extends StructuredWidget {
		private String category;

		public SingleClassAnalysis(String category) {
			this.category = category;
			Anchor printLink = new Anchor("Print", "javascript:");
			controlsPanel.add(printLink, "icon-16-printer");
			printLink.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					print();
				}
			});
		}

		@Override
		public void responseReceived(GenericResponse genericResponse) {
			Collection<String> clazzes = clazzCategories.get(category);
			Label mainLabel = new Label(examName.concat(" - Class-wise report"));
			mainLabel.setStyleName("h18 strong mb2");
			contentsPanel.add(mainLabel);
			FlowPanel table = new FlowPanel();
			Map<String, Boolean> hasData = new HashMap<String, Boolean>();
			Map<String, FlexTable> clazzTables = new HashMap<String, FlexTable>();
			Map<String, Label> clazzLabels = new HashMap<String, Label>();
			for (String clazz : clazzes) {
				Label clazzLabel = new Label(clazz);
				clazzLabel.setStyleName("heading-preset-1");
				table.add(clazzLabel);
				FlexTable clazzTable = new FlexTable();
				clazzTable.setHTML(0, 0, "<b>Subject</b>");
				clazzTable.setHTML(0, 1, "<b>Teachers Incharge</b>");
				clazzTable.setHTML(0, 2, "<b>No of Pass</b>");
				clazzTable.setHTML(0, 3, "<b>No of Failures</b>");
				clazzTable.setHTML(0, 4, "<b>Above 90%</b>");
				clazzTable.setHTML(0, 5, "<b>Highest mark</b>");
				clazzTable.setStyleName("mb18");
				table.add(clazzTable);
				table.add(new HTML("<br />"));
				clazzTables.put(clazz, clazzTable);
				clazzLabels.put(clazz, clazzLabel);
				hasData.put(clazz, false);
			}
			JsArray<JavaScriptObject> consolidation = markList.get(Filter
					.create());
			int length = consolidation.length();
			for (int i = 0; i < length; i++) {
				JSMap current = consolidation.get(i).cast();
				String clazz = current.getString("std") + "-"
						+ current.getString("sec");
				FlexTable clazzTable = clazzTables.get(clazz);
				if (clazzTable == null) {
					continue;
				}
				hasData.put(clazz, true);
				int rowCount = clazzTable.getRowCount();
				clazzTable.setText(rowCount, 0, current.getString("s"));
				clazzTable.setText(rowCount, 1, current.getString("t"));
				clazzTable.setText(rowCount, 2, current.getInt("p") + "");
				clazzTable.setText(rowCount, 3, current.getInt("f") + "");
				clazzTable.setText(rowCount, 4, current.getInt("90") + "");
				clazzTable.setText(rowCount, 5, current.getInt("h") + "");
			}
			for (String clazz : clazzes) {
				if (!hasData.get(clazz)) {
					table.remove(clazzTables.get(clazz));
					table.remove(clazzLabels.get(clazz));
				}
			}
			contentsPanel.add(table);
		}
	}

	class ExamConsolidateView extends StructuredWidget {
		private ListBox standardBox;
		private ListBox sectionBox;
		private ListBox subjectBox;
		private JsArray<JavaScriptObject> currentResult;

		public ExamConsolidateView() {
			standardBox = new ListBox(false);
			standardBox.addItem("All");
			sectionBox = new ListBox(false);
			sectionBox.addItem("All");
			subjectBox = new ListBox(false);
			subjectBox.addItem("All");
			title.setText(examName.concat(" - Custom reports"));

			header.add(new FormWidget()
					.addElement(
							new FormElement(false, 6).setName("Standard")
									.setValue(standardBox))
					.addElement(
							new FormElement(false, 6).setName("Section")
									.setValue(sectionBox))
					.addElement(
							new FormElement(false, 6).setName("Subject")
									.setValue(subjectBox))
					.addElement(new FormElement(true, 4)));

			Anchor refreshAnchor = new Anchor("Refresh", "javascript:");
			controlsPanel.add(refreshAnchor, "icon-16-arrow_refresh");

			Set<String> allStandards = new LinkedHashSet<String>();
			Set<String> allSections = new LinkedHashSet<String>();
			Set<String> allSubjects = new LinkedHashSet<String>();
			final Filter allFilter = Filter.create();
			JsArray<JavaScriptObject> stds = relations.get(allFilter);
			int length = stds.length();
			for (int i = 0; i < length; i++) {
				JSMap singleMap = (JSMap) stds.get(i);
				allStandards.add(singleMap.getString("std"));
				allSections.add(singleMap.getString("sec"));
				allSubjects.add(singleMap.getString("sub"));
			}
			for (String standard : allStandards) {
				standardBox.addItem(standard);
			}
			for (String section : allSections) {
				sectionBox.addItem(section);
			}
			for (String subject : allSubjects) {
				subjectBox.addItem(subject);
			}

			standardBox.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					sectionBox.clear();
					sectionBox.addItem("All");
					JsArray<JavaScriptObject> result = null;
					if (standardBox.getSelectedIndex() == 0) {
						result = relations.get(allFilter);

					} else {
						result = relations.get(Filter.create().add(
								"std",
								Operator.EQ,
								standardBox.getItemText(standardBox
										.getSelectedIndex())));
					}
					int resLen = result.length();
					Set<String> sections = new LinkedHashSet<String>();
					for (int i = 0; i < resLen; i++) {
						sections.add(((JSMap) result.get(i)).getString("sec"));
					}
					for (String section : sections) {
						sectionBox.addItem(section);
					}
					DomEvent.fireNativeEvent(
							Document.get().createChangeEvent(), sectionBox);
				}
			});

			sectionBox.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					subjectBox.clear();
					subjectBox.addItem("All");

					int selStandard = standardBox.getSelectedIndex();
					int selSection = sectionBox.getSelectedIndex();
					Filter filter = Filter.create();
					if (selStandard != 0) {
						filter.add("std", Operator.EQ, standardBox
								.getItemText(standardBox.getSelectedIndex()));
					}
					if (selSection != 0) {
						filter.add("sec", Operator.EQ, sectionBox
								.getItemText(sectionBox.getSelectedIndex()));
					}
					JsArray<JavaScriptObject> result = relations.get(filter);

					Set<String> subjects = new LinkedHashSet<String>();
					int resLen = result.length();
					for (int i = 0; i < resLen; i++) {
						subjects.add(((JSMap) result.get(i)).getString("sub"));
					}
					for (String subject : subjects) {
						subjectBox.addItem(subject);
					}
					DomEvent.fireNativeEvent(
							Document.get().createChangeEvent(), subjectBox);
				}
			});

			refreshAnchor.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					refresh();
				}
			});
		}

		@Override
		public void responseReceived(GenericResponse genericResponse) {
			int selStandard = standardBox.getSelectedIndex();
			int selSection = sectionBox.getSelectedIndex();
			int selSubj = subjectBox.getSelectedIndex();
			Filter filter = Filter.create();
			if (selStandard != 0) {
				filter.add("std", Operator.EQ,
						standardBox.getItemText(standardBox.getSelectedIndex()));
			}
			if (selSection != 0) {
				filter.add("sec", Operator.EQ,
						sectionBox.getItemText(sectionBox.getSelectedIndex()));
			}
			if (selSubj != 0) {
				filter.add("s", Operator.EQ,
						subjectBox.getItemText(subjectBox.getSelectedIndex()));
			}
			currentResult = markList.get(filter);
			int length = currentResult.length();
			if (length == 0) {
				contentsPanel.add(new Label("No data available"));
				return;
			}
			Table table = new Table();
			table.addHeader("Standard", SortType.ALPHA_NUMERIC);
			table.addHeader("Section", SortType.ALPHA_NUMERIC);
			table.addHeader("Subject", SortType.ALPHA_NUMERIC);
			table.addHeader("CL1", SortType.ALPHA_NUMERIC);
			table.addHeader("CL2", SortType.ALPHA_NUMERIC);
			table.addHeader("CL3", SortType.ALPHA_NUMERIC);
			table.addHeader("Staff", SortType.ALPHA_NUMERIC);
			table.addHeader("Highest", SortType.NUMERIC);
			table.addHeader("No of pass", SortType.NUMERIC);
			table.addHeader("No of fail", SortType.NUMERIC);
			table.addHeader("Above 90", SortType.NUMERIC);
			Utils.log(currentResult);
			for (int i = 0; i < length; i++) {
				JSMap single = (JSMap) currentResult.get(i);
				table.setText(i, 0, single.getString("std"));
				table.setText(i, 1, single.getString("sec"));
				table.setText(i, 2, single.getString("s"));
				table.setText(i, 3, single.getString("c1"));
				table.setText(i, 4, single.getString("c2"));
				table.setText(i, 5, single.getString("c3"));
				table.setText(i, 6, single.getString("t"));
				table.setText(i, 7, single.getInt("h") + "");
				table.setText(i, 8, single.getInt("p") + "");
				table.setText(i, 9, single.getInt("f") + "");
				table.setText(i, 10, single.getInt("90") + "");
			}
			contentsPanel.add(table);
			table.initialize();
		}
	}

	class Charts extends StructuredWidget {
		private ListBox categorySelector;
		private ListBox subjectSelector;

		public Charts() {
			title.setText(examName.concat(" - Charts"));
			categorySelector = new ListBox(false);
			subjectSelector = new ListBox(false);
			header.add(new FormWidget().addElement(
					new FormElement(false, 6).setName("Category").setValue(
							categorySelector)).addElement(
					new FormElement(false, 6).setName("Subject").setValue(
							subjectSelector)));
			Set<String> categories = clazzCategories.keySet();
			for (String category : categories) {
				categorySelector.addItem(category);
			}
			categorySelector.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					subjectSelector.clear();
					Collection<String> clazzes = clazzCategories
							.get(categorySelector.getItemText(categorySelector
									.getSelectedIndex()));
					JsArray<JavaScriptObject> allRelations = relations
							.get(Filter.create());
					int length = allRelations.length();
					Set<String> subjects = new LinkedHashSet<String>();
					for (int i = 0; i < length; i++) {
						JSMap single = allRelations.get(i).cast();
						String clazz = single.getString("std").concat("-")
								.concat(single.getString("sec"));
						if (!clazzes.contains(clazz)) {
							continue;
						}
						subjects.add(single.getString("sub"));
					}
					for (String subject : subjects) {
						subjectSelector.addItem(subject);
					}
				}
			});
			Anchor refreshAnchor = new Anchor("Refresh", "javascript:");
			controlsPanel.add(refreshAnchor, "icon-16-arrow_refresh");
			refreshAnchor.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					Runnable runnable = new Runnable() {
						public void run() {
							refresh();
						}
					};
					VisualizationUtils.loadVisualizationApi(runnable,
							ImageLineChart.PACKAGE);
				}
			});
			Anchor printLink = new Anchor("Print", "javascript:");
			controlsPanel.add(printLink, "icon-16-printer");
			printLink.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					print();
				}
			});
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(),
					categorySelector);
		}

		@Override
		public void responseReceived(GenericResponse genericResponse) {
			String category = categorySelector.getItemText(categorySelector
					.getSelectedIndex());
			Collection<String> clazzes = clazzCategories.get(category);
			String selSubject = subjectSelector.getItemText(subjectSelector
					.getSelectedIndex());
			JsArray<JavaScriptObject> entireMarks = markList.get(Filter
					.create());
			int length = entireMarks.length();
			DataTable table = (DataTable) DataTable.create();
			table.addColumn(ColumnType.STRING, "Class");
			table.addColumn(ColumnType.NUMBER, "Pass percentage");
			for (int i = 0; i < length; i++) {
				JSMap single = entireMarks.get(i).cast();
				String clazz = single.getString("std").concat("-")
						.concat(single.getString("sec"));
				String subject = single.getString("s");
				if (!clazzes.contains(clazz) || !subject.equals(selSubject)) {
					continue;
				}
				int pass = single.getInt("p");
				int fail = single.getInt("f");
				double perPass = pass * 100.0f / (pass + fail);
				int row = table.addRow();
				table.setValue(row, 0, clazz);
				table.setValue(row, 1, perPass);
			}
			ImageLineChart.Options options = ImageLineChart.Options.create();
			options.setTitle(selSubject + " analysis over " + category
					+ " category in " + examName);
			options.setLegend(LegendPosition.BOTTOM);
			options.setSize(640, 480);
			ImageLineChart chart = new ImageLineChart(table, options);
			contentsPanel.add(chart);
		}

	}
}
