package com.sagarius.goddess.client.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sagarius.radix.client.collect.LinkedHashSetMultiMap;
import org.sagarius.radix.client.collect.MultiMap;
import org.sagarius.radix.client.uri.StatefulWidget;
import org.sagarius.radix.client.uri.Template;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.LegendPosition;
import com.google.gwt.visualization.client.Query;
import com.google.gwt.visualization.client.Query.Callback;
import com.google.gwt.visualization.client.QueryResponse;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.ImageLineChart;
import com.google.gwt.visualization.client.visualizations.Table;
import com.sagarius.goddess.client.Utils;
import com.sagarius.goddess.client.gadgets.ProfileGadget.ProfileDetails.FormWidget;
import com.sagarius.goddess.client.gadgets.ProfileGadget.ProfileDetails.FormWidget.FormElement;
import com.sagarius.goddess.client.gadgets.VisualizationGadget;
import com.sagarius.goddess.client.model.enumerations.DocumentType;
import com.sagarius.goddess.client.present.CrumbHandler;
import com.sagarius.goddess.client.present.SpreadsheetUtils;
import com.sagarius.goddess.client.utils.DocumentMetaData;
import com.sagarius.goddess.client.utils.Fields.Generic;
import com.sagarius.goddess.client.utils.Fields.Staff;
import com.sagarius.goddess.client.utils.MetaDataRepository;
import com.sagarius.goddess.client.utils.Print;
import com.sagarius.goddess.client.utils.visualization.DataTable;
import com.sagarius.goddess.client.utils.visualization.QueryBuilder;
import com.sagarius.goddess.client.view.util.Breadcrumb;

public class DeprecatedExamPresenter extends TabPanel implements StatefulWidget,
		CrumbHandler {
	private MultiMap<String, String> stdSecMap = new LinkedHashSetMultiMap<String, String>();
	private MultiMap<String, String> clazzSubjMap = new LinkedHashSetMultiMap<String, String>();
	private Set<String> allSections = new HashSet<String>();
	private MultiMap<String, String> standardSubjectsMap = new LinkedHashSetMultiMap<String, String>();
	private Set<String> allSubjects = new HashSet<String>();
	private Map<String, String> allStaffs = new HashMap<String, String>();
	private String docKey = null;
	private ExamConsolidateView consolidateView;
	private Label titleLabel;
	private String examTitle;
	private CategorizedClassPanel categorizedClassPanel;
	private CategorizedStaffPanel categorizedStaffPanel;
	private MultiMap<String, String> clazzCategories = new LinkedHashSetMultiMap<String, String>();
	private MultiMap<String, String> staffCategories = new LinkedHashSetMultiMap<String, String>();
	private List<ExamDTO> consolidatedSheet = new ArrayList<DeprecatedExamPresenter.ExamDTO>();
	private Charts charts;

	class ExamDTO {
		String clazz;
		String subject;
		String staff;
		String staffId;
		String noOfPass;
		String noOfFail;
		String above90;
		String highest;

		@Override
		public String toString() {
			return "ExamDTO [clazz=" + clazz + ", subject=" + subject
					+ ", staff=" + staff + ", staffId=" + staffId
					+ ", noOfPass=" + noOfPass + ", noOfFail=" + noOfFail
					+ ", above90=" + above90 + ", highest=" + highest + "]";
		}

	}

	public DeprecatedExamPresenter() {
		this(null);
	}

	public DeprecatedExamPresenter(String id) {
		setStyleName("tabp-preset-1");
		titleLabel = new Label("Examination");
		titleLabel.setStyleName("h18 mb3 strong");
		docKey = id;
		add(consolidateView = new ExamConsolidateView(), "Custom Reports");
		categorizedClassPanel = new CategorizedClassPanel();
		add(categorizedClassPanel, "Class Reports");
		categorizedStaffPanel = new CategorizedStaffPanel();
		add(categorizedStaffPanel, "Staff Reports");
		charts = new Charts();
		add(charts, "Charts");
		addSelectionHandler(new SelectionHandler<Integer>() {

			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				Integer selectedItem = event.getSelectedItem();
				switch (selectedItem) {
				case 0:
					break;
				case 1:
					if (!categorizedClassPanel.isLoaded()) {
						categorizedClassPanel.refresh();
					}
					break;
				case 2:
					if (!categorizedStaffPanel.isLoaded()) {
						categorizedStaffPanel.refresh();
					}
				case 3:
					if (!charts.isLoaded()) {
						charts.refresh();
					}
				}
			}
		});
		setupActions();
		selectTab(0);
	}

	private void setupActions() {
		SpreadsheetUtils.getQueryString(DocumentType.CSSREL, new String[] {
				"standard", "section", "subject", "staff", "staffname" }, null,
				new AsyncCallback<String>() {

					@Override
					public void onSuccess(final String result) {
						Runnable runnable = new Runnable() {
							public void run() {
								Query query = Query.create(result);
								query.send(new Callback() {

									@Override
									public void onResponse(
											QueryResponse response) {
										if (response.isError()) {
											// contentsPanel
											// .add(new Label(
											// "There has been an error processing your request"));
											return;
										}
										DataTable table = (DataTable) response
												.getDataTable();
										int size = table.getNumberOfRows();
										for (int i = 0; i < size; i++) {
											String standard = table.getValue(i,
													0);
											String section = table.getValue(i,
													1);
											String subject = table.getValue(i,
													2);
											stdSecMap.put(standard, section);
											String clazz = standard + "-"
													+ section;
											clazzSubjMap.put(clazz, subject);
											allSections.add(section);
											standardSubjectsMap.put(standard,
													subject);
											allSubjects.add(subject);
											allStaffs.put(
													table.getValue(i, 3, false),
													table.getValue(i, 4, false));
										}
										consolidateView.setupActions();
										if (categorizedStaffPanel
												.wasRequested()) {
											categorizedStaffPanel.categorySwitcher
													.selectTab(0);
										}
										if (categorizedClassPanel
												.wasRequested()) {
											categorizedClassPanel.categorySwitcher
													.selectTab(0);
										}
										if (!charts.isLoaded()) {
											charts.anotherCall();
										}
									}
								});
							}
						};
						VisualizationUtils.loadVisualizationApi(runnable,
								Table.PACKAGE);

					}

					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
				});
	}

	public void setExam(String id) {
		if (docKey != null && docKey.equals(id)) {
			return;
		}
		docKey = id;
		consolidateView.refresh();
		((Label) Breadcrumb.getCrumbAt(2, false)).setText(id);
		MetaDataRepository
				.getDocuments(
						DocumentType.EXAMINATION,
						new AsyncCallback<List<com.sagarius.goddess.client.model.Document>>() {

							@Override
							public void onSuccess(
									List<com.sagarius.goddess.client.model.Document> result) {
								for (com.sagarius.goddess.client.model.Document document : result) {
									if (document.getCccKey().equals(docKey)) {
										examTitle = document.getDocTitle();
										((Label) Breadcrumb
												.getCrumbAt(2, false))
												.setText(examTitle);
										if (Utils.currentSchool != null) {
											Window.setTitle("Databox - "
													+ Utils.currentSchool
															.getName()
													+ " - Exam " + examTitle);
											titleLabel.setText(examTitle);
										}
									}
								}
							}

							@Override
							public void onFailure(Throwable caught) {
								caught.printStackTrace();
							}
						});
	}

	@Override
	public Template getTemplate() {
		return new Template("exam/{examId}");
	}

	@Override
	public void handle(Map<String, String> attributes) {
		String examId = attributes.get("examId");
		if (examId.equals(docKey)) {
			return;
		}
		setExam(examId);
	}

	@Override
	public void handleCrumb() {
		Hyperlink link = (Hyperlink) Breadcrumb.getCrumbAt(1, true);
		link.setTargetHistoryToken("exam");
		link.setText("Exams");
		com.sagarius.goddess.client.Utils.setActiveMainMenu("exam");
	}

	class ExamConsolidateView extends VisualizationGadget {
		private ListBox standardBox;
		private ListBox sectionBox;
		private ListBox subjectBox;

		private Anchor downloadLink = new Anchor("Download list",
				"javascript:", "_blank");

		public ExamConsolidateView() {
			super("", "", Table.PACKAGE);
			setTitle("Queries");
			title.setStyleName("h13 mb3 strong");
			initComponents();
			controlsPanel.add(downloadLink);
		}

		public void setupActions() {
			Set<String> standards = standardSubjectsMap.keySet();
			for (String standard : standards) {
				standardBox.addItem(standard);
			}
			standardBox.setSelectedIndex(1);
			String standard = standardBox.getValue(1);
			Collection<String> sections = stdSecMap.get(standard);
			for (String sec : sections) {
				sectionBox.addItem(sec);
			}
			sectionBox.setSelectedIndex(1);
			String section = sectionBox.getValue(1);
			Collection<String> subjects = clazzSubjMap.get(standard + "-"
					+ section);
			for (String subj : subjects) {
				subjectBox.addItem(subj);
			}
			subjectBox.setSelectedIndex(1);
			refresh();
			standardBox.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					String standard = standardBox.getValue(standardBox
							.getSelectedIndex());
					sectionBox.clear();
					sectionBox.addItem("All");
					if (!standard.equals("All")) {
						Collection<String> sections = stdSecMap.get(standard);
						for (String section : sections) {
							sectionBox.addItem(section);
						}
					} else {
						for (String section : allSections) {
							sectionBox.addItem(section);
						}
					}
					sectionBox.setSelectedIndex(1);
					DomEvent.fireNativeEvent(
							Document.get().createChangeEvent(), sectionBox);
				}
			});
			sectionBox.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					int standardIndex = standardBox.getSelectedIndex();
					String standard = standardBox.getValue(standardIndex);
					int sectionIndex = sectionBox.getSelectedIndex();
					String section = sectionBox.getValue(sectionIndex);
					int perm = 0;
					if (standardIndex == 0) {
						perm |= 0x10;
					}
					if (sectionIndex == 0) {
						perm |= 0x1;
					}
					subjectBox.clear();
					subjectBox.addItem("All");
					Collection<String> subjects = null;
					switch (perm) {
					case 0x0:
						subjects = clazzSubjMap.get(standard + "-" + section);
						break;
					case 0x1:
						subjects = standardSubjectsMap.get(standard);
						break;
					case 0x10:
						subjects = allSubjects;
						break;
					case 0x11:
						subjects = allSubjects;
					default:
						System.out.println("Default");
					}
					for (String subject : subjects) {
						subjectBox.addItem(subject);
					}
					subjectBox.setSelectedIndex(1);
					DomEvent.fireNativeEvent(
							Document.get().createChangeEvent(), subjectBox);
				}
			});
		}

		private void initComponents() {
			standardBox = new ListBox(false);
			standardBox.addItem("All");
			sectionBox = new ListBox(false);
			sectionBox.addItem("All");
			subjectBox = new ListBox(false);
			subjectBox.addItem("All");

			header.add(new FormWidget()
					.addElement(
							new FormElement(false, 6).setName("Standard")
									.setValue(standardBox))
					.addElement(
							new FormElement(false, 6).setName("Section")
									.setValue(sectionBox))
					.addElement(
							new FormElement(false, 6).setName("Subject")
									.setValue(subjectBox)));
		}

		@Override
		public void refresh() {
			if (docKey == null || stdSecMap.isEmpty()) {
				return;
			}
			SpreadsheetUtils.getHeaders(docKey, "Consolidated",
					new AsyncCallback<Map<String, String>>() {

						@Override
						public void onSuccess(Map<String, String> result) {
							StringBuffer buffer = new StringBuffer(
									"select * where ");
							boolean flag = false;
							String standard = standardBox.getValue(standardBox
									.getSelectedIndex());
							if (!standard.equals("All")) {
								buffer.append("standard='" + standard + "' ");
								flag = true;
							}
							String section = sectionBox.getValue(sectionBox
									.getSelectedIndex());
							if (!section.equals("All")) {
								if (flag) {
									buffer.append("and ");
								}
								flag = true;
								buffer.append("section='" + section + "' ");
							}
							String subject = subjectBox.getValue(subjectBox
									.getSelectedIndex());
							if (!subject.equals("All")) {
								if (flag) {
									buffer.append("and ");
								}
								flag = true;
								buffer.append("subject='" + subject + "'");
							}
							if (!flag) {
								buffer = new StringBuffer("select *");
							}
							String tqString = URL.encode(buffer.toString());
							requestUri = (Utils.sheetVizUrl + "?tq=" + tqString
									+ "&key=" + docKey + "&sheet=Consolidated")
									.replace("standard", result.get("standard"))
									.replace("section", result.get("section"))
									.replace("subject", result.get("subject"));
							downloadLink.setHref(requestUri + "&tqx=out:csv");
							ExamConsolidateView.super.refresh();
						}

						@Override
						public void onFailure(Throwable caught) {
							caught.printStackTrace();
						}
					});
		}

		@Override
		public void responseRecevied(QueryResponse response) {
			DataTable table = (DataTable) response.getDataTable();
			Table.Options tableOptions = Table.Options.create();
			Table tableGadget = new Table(table, tableOptions);
			contentsPanel.add(tableGadget);
		}

	}

	class CategorizedClassPanel extends VisualizationGadget {
		private TabPanel categorySwitcher = new TabPanel();
		private QueryBuilder builder;
		private boolean wasRequested;

		public CategorizedClassPanel() {
			super("", "", Table.PACKAGE);
			categorySwitcher.setStyleName("tabp-preset-2");
			MetaDataRepository.get(DocumentType.CLASS,
					new AsyncCallback<DocumentMetaData>() {

						@Override
						public void onSuccess(DocumentMetaData result) {
							builder = new QueryBuilder(Utils.sheetVizUrl)
									.setMetaData(result);
							requestUri = builder.addSelection(Generic.STANDARD)
									.addSelection(Generic.SECTION)
									.addSelection(Generic.CATEGORY).get();
							categorySwitcher
									.addSelectionHandler(new SelectionHandler<Integer>() {

										@Override
										public void onSelection(
												SelectionEvent<Integer> event) {
											ClassWisePanel clazzWisePanel = (ClassWisePanel) categorySwitcher
													.getWidget(event
															.getSelectedItem());
											if (!clazzWisePanel.isStarted()) {
												clazzWisePanel
														.generateClassWise();
											}
										}
									});
						}

						@Override
						public void onFailure(Throwable caught) {
							contentsPanel
									.add(new Label(
											"There has been a problem connecting to the network"));
						}
					});
		}

		@Override
		public void responseRecevied(QueryResponse response) {
			categorySwitcher.clear();
			contentsPanel.add(categorySwitcher);
			DataTable table = (DataTable) response.getDataTable();
			int rows = table.getNumberOfRows();
			for (int i = 0; i < rows; i++) {
				String clazz = table.getValue(i, 0) + "-"
						+ table.getValue(i, 1);
				clazzCategories.put(table.getValue(i, 2), clazz);
			}
			Set<String> categories = clazzCategories.keySet();
			for (String category : categories) {
				categorySwitcher.add(new ClassWisePanel(category),
						category.equals("") ? "Others" : category);
			}
			categorySwitcher.add(new ClassWisePanel(null), "All");
			if (!clazzSubjMap.isEmpty()) {
				Query query = Query.create(Utils.sheetVizUrl + "?key=" + docKey
						+ "&sheet=Consolidated");
				query.send(new Callback() {

					@Override
					public void onResponse(QueryResponse response) {
						DataTable dataTable = (DataTable) response
								.getDataTable();
						int rowCount = dataTable.getNumberOfRows();
						for (int i = 0; i < rowCount; i++) {
							int standard = 0;
							int section = 1;
							int subject = 2;
							int staffName = 3;
							int staffId = 4;
							int highest = 7;
							int noOfPass = 9;
							int noOfFail = 10;
							int above90 = 11;
							ExamDTO dto = new ExamDTO();
							dto.clazz = dataTable.getValue(i, standard) + "-"
									+ dataTable.getValue(i, section);
							dto.subject = dataTable.getValue(i, subject);
							dto.staff = dataTable.getValue(i, staffName);
							dto.staffId = dataTable.getValue(i, staffId, false);
							dto.highest = dataTable.getValue(i, highest, false);
							dto.noOfPass = dataTable.getValue(i, noOfPass,
									false);
							dto.noOfFail = dataTable.getValue(i, noOfFail,
									false);
							dto.above90 = dataTable.getValue(i, above90, false);
							consolidatedSheet.add(dto);
						}
						categorySwitcher.selectTab(0);
					}
				});
			} else {
				wasRequested = true;
			}
			// ((ClassWisePanel) categorySwitcher.getWidget(0))
			// .generateClassWise();
		}

		public boolean wasRequested() {
			return wasRequested;
		}
	}

	class CategorizedStaffPanel extends VisualizationGadget {
		private TabPanel categorySwitcher = new TabPanel();
		private QueryBuilder builder;
		private boolean wasRequested;

		public CategorizedStaffPanel() {
			super("", "", Table.PACKAGE);
			categorySwitcher.setStyleName("tabp-preset-2");
			MetaDataRepository.get(DocumentType.STAFF,
					new AsyncCallback<DocumentMetaData>() {

						@Override
						public void onSuccess(DocumentMetaData result) {
							builder = new QueryBuilder(Utils.sheetVizUrl)
									.setMetaData(result);
							requestUri = builder.addSelection(Staff.STAFF_ID)
									.addSelection(Generic.CATEGORY).get();
							categorySwitcher
									.addSelectionHandler(new SelectionHandler<Integer>() {

										@Override
										public void onSelection(
												SelectionEvent<Integer> event) {
											StaffWisePanel staffWisePanel = (StaffWisePanel) categorySwitcher
													.getWidget(event
															.getSelectedItem());
											if (!staffWisePanel.isStarted()) {
												staffWisePanel
														.generateStaffWise();
											}
										}
									});
						}

						@Override
						public void onFailure(Throwable caught) {
							contentsPanel
									.add(new Label(
											"There has been a problem connecting to the network"));
						}
					});

		}

		@Override
		public void responseRecevied(QueryResponse response) {
			categorySwitcher.clear();
			contentsPanel.add(categorySwitcher);
			DataTable table = (DataTable) response.getDataTable();
			int rows = table.getNumberOfRows();
			for (int i = 0; i < rows; i++) {
				staffCategories.put(table.getValue(i, 1),
						table.getValue(i, 0, false));
			}
			Set<String> categories = staffCategories.keySet();
			for (String category : categories) {
				categorySwitcher.add(new StaffWisePanel(category),
						category.equals("") ? "Others" : category);
			}
			categorySwitcher.add(new StaffWisePanel(null), "All");
			if (!allStaffs.isEmpty()) {
				Query query = Query.create(Utils.sheetVizUrl + "?key=" + docKey
						+ "&sheet=Consolidated");
				query.send(new Callback() {

					@Override
					public void onResponse(QueryResponse response) {
						DataTable dataTable = (DataTable) response
								.getDataTable();
						int rowCount = dataTable.getNumberOfRows();
						for (int i = 0; i < rowCount; i++) {
							int standard = 0;
							int section = 1;
							int subject = 2;
							int staffName = 3;
							int staffId = 4;
							int highest = 7;
							int noOfPass = 9;
							int noOfFail = 10;
							int above90 = 11;
							ExamDTO dto = new ExamDTO();
							dto.clazz = dataTable.getValue(i, standard) + "-"
									+ dataTable.getValue(i, section);
							dto.subject = dataTable.getValue(i, subject);
							dto.staff = dataTable.getValue(i, staffName);
							dto.staffId = dataTable.getValue(i, staffId, false);
							dto.highest = dataTable.getValue(i, highest, false);
							dto.noOfPass = dataTable.getValue(i, noOfPass,
									false);
							dto.noOfFail = dataTable.getValue(i, noOfFail,
									false);
							dto.above90 = dataTable.getValue(i, above90, false);
							consolidatedSheet.add(dto);
						}
						categorySwitcher.selectTab(0);
					}
				});
			} else {
				wasRequested = true;
			}
		}

		public boolean wasRequested() {
			return wasRequested;
		}
	}

	class ClassWisePanel extends FlowPanel {
		public ClassWisePanel(String category) {
			this.category = category;
		}

		private boolean isStarted;
		private String category;
		private BareReport report;

		public boolean isStarted() {
			return isStarted;
		}

		void generateClassWise() {
			isStarted = true;
			if (docKey == null || clazzSubjMap.isEmpty()) {
				return;
			}
			report = new BareReport("Generating class-wise report...");

			// Category multi map cannot be empty @ this time as only after
			// loading that can a category be selected and a single category
			// class presenter can be published [Similarly consolidateSheet]

			// TODO wrap it up with meta data repository and change the
			// hard-codings
			add(report);
			manipulateClassWise(report.getMainTable());
			report.complete();
		}

		void manipulateClassWise(FlowPanel table) {
			Label subTabHeading = new Label(examTitle + " - Class-wise report");
			subTabHeading.setStyleName("h18 strong mb2");
			table.add(subTabHeading);
			Collection<String> clazzes = null;
			if (category == null) {
				clazzes = clazzSubjMap.keySet();
			} else {
				clazzes = clazzCategories.get(category);
			}
			Map<String, FlexTable> clazzTables = new HashMap<String, FlexTable>();
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
			}
			for (ExamDTO dto : consolidatedSheet) {
				FlexTable clazzTable = clazzTables.get(dto.clazz);
				if (clazzTable == null) {
					continue;
				}
				int rowCount = clazzTable.getRowCount();
				clazzTable.setText(rowCount, 0, dto.subject);
				clazzTable.setText(rowCount, 1, dto.staff);
				clazzTable.setText(rowCount, 2, dto.noOfPass);
				clazzTable.setText(rowCount, 3, dto.noOfFail);
				clazzTable.setText(rowCount, 4, dto.above90);
				clazzTable.setText(rowCount, 5, dto.highest);
			}
			// int standard = result.getFieldMeta(Generic.STANDARD).getIndex();
			// int section = result.getFieldMeta(Generic.SECTION).getIndex();
			// int subject = result.getFieldMeta(Generic.SUBJECT).getIndex();
			// int staffName = result.getFieldMeta(Generic.NAME).getIndex();
			// int highest =
			// result.getFieldMeta(Examination.HIGHEST).getIndex();
			// int noOfPass = result.getFieldMeta(Examination.NO_OF_PASS)
			// .getIndex();
			// int noOfFail = result.getFieldMeta(Examination.NO_OF_FAIL)
			// .getIndex();
			// int above90 =
			// result.getFieldMeta(Examination.ABOVE_90).getIndex();
		}
	}

	class StaffWisePanel extends FlowPanel {
		public StaffWisePanel(String category) {
			this.category = category;
		}

		private boolean isStarted;
		private String category;
		private BareReport report;

		public boolean isStarted() {
			return isStarted;
		}

		void generateStaffWise() {
			isStarted = true;
			if (docKey == null || allStaffs.isEmpty()) {
				return;
			}
			report = new BareReport("Generating staff-wise report...");

			// Category multi map cannot be empty @ this time as only after
			// loading that can a category be selected and a single category
			// class presenter can be published [Similarly consolidateSheet]

			// TODO wrap it up with meta data repository and change the
			// hard-codings
			add(report);
			manipulateStaffWise(report.getMainTable());
			report.complete();
		}

		void manipulateStaffWise(FlowPanel table) {
			Label subTabHeading = new Label(examTitle + " - Staff-wise report");
			subTabHeading.setStyleName("h18 strong mb2");
			table.add(subTabHeading);
			Collection<String> staffs = null;
			if (category == null) {
				staffs = allStaffs.keySet();
			} else {
				staffs = staffCategories.get(category);
			}
			Map<String, FlexTable> staffTables = new HashMap<String, FlexTable>();
			for (String staff : staffs) {
				Label staffLabel = new Label(allStaffs.get(staff));
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
			}
			for (ExamDTO dto : consolidatedSheet) {
				FlexTable staffTable = staffTables.get(dto.staffId);
				if (staffTable == null) {
					continue;
				}
				int rowCount = staffTable.getRowCount();
				staffTable.setText(rowCount, 0, dto.clazz);
				staffTable.setText(rowCount, 1, dto.subject);
				staffTable.setText(rowCount, 2, dto.noOfPass);
				staffTable.setText(rowCount, 3, dto.noOfFail);
				staffTable.setText(rowCount, 4, dto.above90);
				staffTable.setText(rowCount, 5, dto.highest);
			}
			// int standard = result.getFieldMeta(Generic.STANDARD).getIndex();
			// int section = result.getFieldMeta(Generic.SECTION).getIndex();
			// int subject = result.getFieldMeta(Generic.SUBJECT).getIndex();
			// int staffName = result.getFieldMeta(Generic.NAME).getIndex();
			// int highest =
			// result.getFieldMeta(Examination.HIGHEST).getIndex();
			// int noOfPass = result.getFieldMeta(Examination.NO_OF_PASS)
			// .getIndex();
			// int noOfFail = result.getFieldMeta(Examination.NO_OF_FAIL)
			// .getIndex();
			// int above90 =
			// result.getFieldMeta(Examination.ABOVE_90).getIndex();
		}
	}

	class BareReport extends FlowPanel {
		private DeckPanel decker;
		private Label label;
		private FlowPanel mainTable;
		private FlowPanel mainTableContainer;

		public BareReport(String title) {
			label = new Label(title);
			mainTable = new FlowPanel();
			decker = new DeckPanel();
			decker.add(label);
			mainTableContainer = new FlowPanel();
			mainTableContainer.add(mainTable);
			mainTableContainer.add(new Button("Print", new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					Print.it(mainTable);
				}
			}));
			decker.add(mainTableContainer);
			decker.showWidget(0);
			add(decker);
		}

		public FlowPanel getMainTable() {
			return mainTable;
		}

		public void complete() {
			decker.showWidget(1);
		}
	}

	class Charts extends VisualizationGadget {
		private ListBox categorySelector;
		private ListBox subjectSelector;

		public Charts() {
			super("", "", ImageLineChart.PACKAGE);
			categorySelector = new ListBox(false);
			header.add(categorySelector);
			subjectSelector = new ListBox(false);
			header.add(subjectSelector);
		}

		public void anotherCall() {
			if (clazzSubjMap == null) {
				return;
			}
			MetaDataRepository.get(DocumentType.CLASS,
					new AsyncCallback<DocumentMetaData>() {

						private QueryBuilder builder;

						@Override
						public void onSuccess(DocumentMetaData result) {
							builder = new QueryBuilder(Utils.sheetVizUrl)
									.setMetaData(result);
							requestUri = builder.addSelection(Generic.STANDARD)
									.addSelection(Generic.SECTION)
									.addSelection(Generic.CATEGORY).get();
							Query query = Query.create(requestUri);
							query.send(new Callback() {

								@Override
								public void onResponse(QueryResponse response) {
									if (response.isError()) {
										onFailure(new Throwable(response
												.getDetailedMessage()));
										return;
									}
									init(response);
								}
							});
						}

						@Override
						public void onFailure(Throwable caught) {
							contentsPanel
									.add(new Label(
											"There has been a problem connecting to the network"));
						}
					});
		}

		public void init(QueryResponse response) {
			DataTable table = (DataTable) response.getDataTable();
			int rows = table.getNumberOfRows();
			for (int i = 0; i < rows; i++) {
				String clazz = table.getValue(i, 0) + "-"
						+ table.getValue(i, 1);
				clazzCategories.put(table.getValue(i, 2), clazz);
			}
			categorySelector.clear();
			Set<String> categories = clazzCategories.keySet();
			for (String category : categories) {
				categorySelector.addItem(category);
			}
			categorySelector.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					String category = categorySelector
							.getItemText(categorySelector.getSelectedIndex());
					Collection<String> clazzes = clazzCategories.get(category);
					Set<String> subjects = new HashSet<String>();
					for (String clazz : clazzes) {
						Collection<String> clazzSubjects = clazzSubjMap
								.get(clazz);
						subjects.addAll(clazzSubjects);
					}
					subjectSelector.clear();
					for (String subject : subjects) {
						subjectSelector.addItem(subject);
					}
					subjectSelector.setSelectedIndex(0);
				}
			});
			categorySelector.setSelectedIndex(0);
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(),
					categorySelector);
			isLoaded = true;
			Query query = Query.create(Utils.sheetVizUrl + "?key=" + docKey
					+ "&sheet=Consolidated");
			query.send(new Callback() {

				@Override
				public void onResponse(QueryResponse response) {
					DataTable dataTable = (DataTable) response.getDataTable();
					int rowCount = dataTable.getNumberOfRows();
					for (int i = 0; i < rowCount; i++) {
						int standard = 0;
						int section = 1;
						int subject = 2;
						int staffName = 3;
						int staffId = 4;
						int highest = 7;
						int noOfPass = 9;
						int noOfFail = 10;
						int above90 = 11;
						ExamDTO dto = new ExamDTO();
						dto.clazz = dataTable.getValue(i, standard) + "-"
								+ dataTable.getValue(i, section);
						dto.subject = dataTable.getValue(i, subject);
						dto.staff = dataTable.getValue(i, staffName);
						dto.staffId = dataTable.getValue(i, staffId, false);
						dto.highest = dataTable.getValue(i, highest, false);
						dto.noOfPass = dataTable.getValue(i, noOfPass, false);
						dto.noOfFail = dataTable.getValue(i, noOfFail, false);
						dto.above90 = dataTable.getValue(i, above90, false);
						consolidatedSheet.add(dto);
					}
				}
			});
		}

		@Override
		public void refresh() {
			if (!isLoaded) {
				anotherCall();
			}
			setLoading(true);
			contentsPanel.clear();
			responseRecevied(null);
		}

		@Override
		public void responseRecevied(QueryResponse response) {
			Runnable runnable = new Runnable() {
				public void run() {
					String category = categorySelector
							.getItemText(categorySelector.getSelectedIndex());
					Collection<String> clazzes = clazzCategories.get(category);
					String subject = subjectSelector
							.getItemText(subjectSelector.getSelectedIndex());
					com.google.gwt.visualization.client.DataTable table = com.google.gwt.visualization.client.DataTable
							.create();
					table.addColumn(ColumnType.STRING, "Class");
					table.addColumn(ColumnType.NUMBER, "Percentage pass");
					for (ExamDTO dto : consolidatedSheet) {
						if (!clazzes.contains(dto.clazz)) {
							continue;
						}
						if (!subject.equals(dto.subject)) {
							continue;
						}
						try {
							int pass = dto.noOfPass == null ? 0 : Integer
									.parseInt(dto.noOfPass);
							int fail = dto.noOfFail == null ? 0 : Integer
									.parseInt(dto.noOfFail);
							double perPass = pass * 100.0f / (pass + fail);
							int row = table.addRow();
							table.setValue(row, 0, dto.clazz);
							table.setValue(row, 1, perPass);
						} catch (NumberFormatException e) {
						}
					}
					ImageLineChart.Options options = ImageLineChart.Options
							.create();
					options.setTitle(subject + " analysis over " + category
							+ " category in " + examTitle);
					options.setLegend(LegendPosition.BOTTOM);
					options.setSize(640, 480);
					ImageLineChart chart = new ImageLineChart(table, options);
					BareReport report = new BareReport("");
					report.mainTable.add(chart);
					contentsPanel.add(report);
					report.complete();
					setLoading(false);
				}
			};
			VisualizationUtils.loadVisualizationApi(runnable,
					ImageLineChart.PACKAGE);
		}
	}
}
