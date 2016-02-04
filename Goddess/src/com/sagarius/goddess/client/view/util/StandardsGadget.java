package com.sagarius.goddess.client.view.util;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.visualization.client.QueryResponse;
import com.sagarius.goddess.client.Utils;
import com.sagarius.goddess.client.ajax.GenericResponse;
import com.sagarius.goddess.client.model.enumerations.DocumentType;
import com.sagarius.goddess.client.present.NewSpreadsheetUtils;
import com.sagarius.goddess.client.utils.DocumentMetaData;
import com.sagarius.goddess.client.utils.Fields.Clazz;
import com.sagarius.goddess.client.utils.Fields.Generic;
import com.sagarius.goddess.client.utils.Fields.Student;
import com.sagarius.goddess.client.utils.MetaDataRepository;
import com.sagarius.goddess.client.utils.visualization.DataTable;
import com.sagarius.goddess.client.utils.visualization.QueryBuilder;
import com.sagarius.goddess.client.view.util.StandardWidget.Section;
import com.sagarius.goddess.client.widgets.BaseWidget;

public abstract class StandardsGadget extends BaseWidget {
	protected List<StandardWidget> standards;
	protected QueryBuilder builder = new QueryBuilder(Utils.sheetVizUrl);

	@Override
	public void responseReceived(GenericResponse gResponse) {
		QueryResponse response = gResponse.getQueryResponse();
		final DataTable dataTable = (DataTable) response.getDataTable();
		final Map<String, List<Integer>> stdMap = new LinkedHashMap<String, List<Integer>>();
		int noOfRows = dataTable.getNumberOfRows();
		for (int i = 0; i < noOfRows; i++) {
			String std = dataTable.getValue(i, 0, false);
			List<Integer> secList = stdMap.get(std);
			if (secList == null) {
				secList = new LinkedList<Integer>();
				stdMap.put(std, secList);
			}
			secList.add(i);
		}
		NewSpreadsheetUtils.getNewStaffMap(false,
				new AsyncCallback<Map<String, String>>() {

					@Override
					public void onFailure(Throwable caught) {
						statusMessage.setText(caught.getMessage());
					}

					@Override
					public void onSuccess(Map<String, String> result) {
						controlsPanel.clear();
						Set<Entry<String, List<Integer>>> entrySet = stdMap
								.entrySet();
						standards = new LinkedList<StandardWidget>();
						for (Entry<String, List<Integer>> entry : entrySet) {
							final String std = entry.getKey();
							List<Integer> secs = entry.getValue();
							final StandardWidget standard = new StandardWidget()
									.setClass(std);
							for (Integer rowIndex : secs) {
								// TODO Error prone
								int index = builder.getMetaData()
										.getFieldMeta(Clazz.INCHARGE_ID)
										.getIndex();
								String staffId = dataTable.getValue(rowIndex,
										index, false);
								standard.addSection(new Section(dataTable
										.getValue(rowIndex, 1), result
										.get(staffId), staffId));
							}
							contentsPanel.add(standard);
							standards.add(standard);

							ControlPanel controlPanel = standard
									.getControlPanel();
							controlPanel.add(
									new Anchor("Select All", "javascript:"),
									"icon-16-tick").add(
									new Anchor("Select None", "javascript:"),
									"icon-16-cross");
							((Anchor) (controlPanel.getControlAt(0)))
									.addClickHandler(new ClickHandler() {

										@Override
										public void onClick(ClickEvent event) {
											for (Section section : standard.sections) {
												section.selector.setValue(true);
											}
										}
									});
							((Anchor) (controlPanel.getControlAt(1)))
									.addClickHandler(new ClickHandler() {

										@Override
										public void onClick(ClickEvent event) {
											for (Section section : standard.sections) {
												section.selector
														.setValue(false);
											}
										}
									});
						}
						Anchor selectAllAnchor = new Anchor("All",
								"javascript:");
						Anchor selectNoneAnchor = new Anchor("None",
								"javascript:");
						selectAllAnchor.setStyleName("mr4 icon-16-tick");
						selectNoneAnchor.setStyleName("mr4 icon-16-cross");
						controlsPanel.add(selectAllAnchor);
						controlsPanel.add(selectNoneAnchor);
						selectAllAnchor.addClickHandler(new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {
								for (StandardWidget std : standards) {
									for (Section sec : std.sections) {
										sec.selector.setValue(true);
									}
								}
							}
						});
						selectNoneAnchor.addClickHandler(new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {
								for (StandardWidget std : standards) {
									for (Section sec : std.sections) {
										sec.selector.setValue(false);
									}
								}
							}
						});
						addControls();
					}
				});
	}

	@Override
	protected void initialize(final AsyncCallback<Void> callback) {
		MetaDataRepository.get(DocumentType.CLASS,
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

	public StandardsGadget() {
		title.setStyleName("h18 mb3 strong");
		setTitle("Classes");
	}

	public List<String> getSelectedClasses() {
		List<String> selectedClasses = new LinkedList<String>();
		for (StandardWidget std : standards) {
			selectedClasses.addAll(std.getSelectedClasses());
		}
		return selectedClasses;
	}

	public abstract void addControls();

	public String getGroupsQuery() {
		boolean isAllSelected = true, isNoneSelected = true;
		List<String> groupsQuery = new LinkedList<String>();
		for (StandardWidget std : standards) {
			String query = std.getGroupsQuery();
			if (query == null) {
				isAllSelected = false;
			} else {
				isNoneSelected = false;
				groupsQuery.add(query);
			}
		}
		if (isNoneSelected) {
			return null;
		}
		if (isAllSelected) {
			return "All students";
		}
		return groupsQuery.toString();
	}

	public String getSpreadsheetQuery() {
		String baseQueryString = "select standard,section,studentid,name ";
		StringBuffer queryString = new StringBuffer(baseQueryString)
				.append(" where ");
		boolean isAllSelected = true, isNoneSelected = true;
		for (StandardWidget std : standards) {
			String subQuery = handleSheetStandard(std);
			if (subQuery.equals("")) {
				isAllSelected = false;
			} else {
				isNoneSelected = false;
				queryString.append(subQuery + " or ");
			}
		}
		if (isNoneSelected) {
			return null;
		}
		if (isAllSelected) {
			return baseQueryString;
		}
		int length = queryString.length();
		queryString.delete(length - 3, length);
		return queryString.toString();
	}

	private String handleStandard(StandardWidget standard,
			DocumentMetaData metaData) {
		String standardId = metaData.getFieldMeta(Generic.STANDARD)
				.getVizQueryId();
		String sectionId = metaData.getFieldMeta(Generic.SECTION)
				.getVizQueryId();
		String subQueryString = "(" + standardId + "=\"" + standard.std + "\"";
		StringBuffer buffer = new StringBuffer(" and (");
		boolean isAllSelected = true, isNoneSelected = true;
		for (Section section : standard.sections) {
			if (section.selector.getValue()) {
				buffer.append(sectionId + "=\"" + section.secStr + "\" or ");
				isNoneSelected = false;
			} else {
				isAllSelected = false;
			}
		}
		if (!isAllSelected && !isNoneSelected) {
			int length = buffer.length();
			buffer.delete(length - 3, length);
			buffer.append(")");
			subQueryString += buffer.toString();
		}
		if (!isNoneSelected) {
			subQueryString += ")";
			return subQueryString;
		}
		return "";
	}

	public void getVisualizationQuery(final AsyncCallback<String> callback) {
		MetaDataRepository.get(DocumentType.SINGLE_STUDENT,
				new AsyncCallback<DocumentMetaData>() {

					@Override
					public void onSuccess(DocumentMetaData metaData) {
						StringBuilder builder = new StringBuilder();
						boolean isAllSelected = true, isNoneSelected = true;
						for (StandardWidget std : standards) {
							String subQuery = handleStandard(std, metaData);
							if (subQuery.equals("")) {
								isAllSelected = false;
							} else {
								isNoneSelected = false;
								builder.append(subQuery + " or ");
							}
						}
						QueryBuilder queryBuilder = new QueryBuilder(
								Utils.sheetVizUrl).setMetaData(metaData)
								.addSelections(Generic.STANDARD,
										Generic.SECTION, Student.STUDENT_ID,
										Generic.NAME);
						if (isNoneSelected) {
							callback.onSuccess(null);
						}
						if (isAllSelected) {
							callback.onSuccess(queryBuilder.get());
						}
						int length = builder.length();
						builder.delete(length - 3, length);
						String queryString = queryBuilder.addFilter(
								builder.toString()).get();
						callback.onSuccess(queryString);
					}

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}
				});
	}

	private String handleSheetStandard(StandardWidget standard) {
		String subQueryString = "(standard=\"" + standard.std + "\"";
		StringBuffer buffer = new StringBuffer(" and (");
		boolean isAllSelected = true, isNoneSelected = true;
		for (Section section : standard.sections) {
			if (section.selector.getValue()) {
				buffer.append("section=\"" + section.secStr + "\" or ");
				isNoneSelected = false;
			} else {
				isAllSelected = false;
			}
		}
		if (!isAllSelected && !isNoneSelected) {
			int length = buffer.length();
			buffer.delete(length - 3, length);
			buffer.append(")");
			subQueryString += buffer.toString();
		}
		if (!isNoneSelected) {
			subQueryString += ")";
			return subQueryString;
		}
		return "";
	}

}
