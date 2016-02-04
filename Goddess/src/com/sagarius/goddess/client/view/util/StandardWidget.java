package com.sagarius.goddess.client.view.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.sagarius.goddess.client.Utils;
import com.sagarius.goddess.client.model.enumerations.DocumentType;
import com.sagarius.goddess.client.utils.DocumentMetaData;
import com.sagarius.goddess.client.utils.DocumentMetaData.FieldMetaData;
import com.sagarius.goddess.client.utils.Fields.Generic;
import com.sagarius.goddess.client.utils.Fields.Student;
import com.sagarius.goddess.client.utils.visualization.QueryBuilder;
import com.sagarius.goddess.client.utils.MetaDataRepository;

/**
 * Represents a single standard in UI <br />
 * Allows to get the selection in 3 forms
 * 
 * @author bala
 * 
 */
public class StandardWidget extends FlowPanel {
	public String std;
	public List<StandardWidget.Section> sections;
	private ControlPanel controlPanel = new ControlPanel();
	private FlowPanel sectionsPanel = new FlowPanel();

	public StandardWidget() {
		setStyleName("standard");
	}

	public StandardWidget setClass(String title) {
		std = title;
		Label titleWidget = new Label(title);
		titleWidget.setStyleName("class");
		add(titleWidget);
		add(sectionsPanel);
		add(controlPanel);
		return this;
	}

	public List<String> getSelectedClasses() {
		List<String> selectedClasses = new LinkedList<String>();
		for (StandardWidget.Section section : sections) {
			if (section.selector.getValue()) {
				selectedClasses.add(std + "-" + section.secStr);
			}
		}
		return selectedClasses;
	}

	public String getGroupsQuery() {
		boolean isNoneSelected = true;
		boolean isAllSelected = true;
		List<String> sectionsSelected = new LinkedList<String>();
		for (StandardWidget.Section section : sections) {
			if (section.selector.getValue()) {
				sectionsSelected.add(section.secStr);
				isNoneSelected = false;
			} else {
				isAllSelected = false;
			}
		}
		if (isNoneSelected) {
			return null;
		}
		if (isAllSelected) {
			return std;
		}
		return std + " : " + sectionsSelected.toString();
	}

	public String getSpreadsheetQuery() {
		String queryString = "select standard,section,student,name where standard =\""
				+ std + "\"";
		StringBuffer buffer = new StringBuffer(" and (");
		boolean isNoneSelected = true;
		boolean isAllSelected = true;
		for (StandardWidget.Section section : sections) {
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
			queryString += buffer.toString();
		}
		if (!isNoneSelected) {
			queryString = URL.encode(queryString);
			return queryString;
		}
		return null;
	}

	public void getVisualizationQuery(final AsyncCallback<String> callback) {
		MetaDataRepository.get(DocumentType.SINGLE_STUDENT,
				new AsyncCallback<DocumentMetaData>() {

					@Override
					public void onSuccess(DocumentMetaData metaData) {
						FieldMetaData standardMeta = metaData
								.getFieldMeta(Generic.STANDARD);
						FieldMetaData sectionMeta = metaData
								.getFieldMeta(Generic.SECTION);
						String standardId = standardMeta.getVizQueryId();
						String sectionId = sectionMeta.getVizQueryId();
						StringBuilder builder = new StringBuilder(standardId)
								.append("=").append(
										QueryBuilder.getQueryString(std,
												standardMeta.getType()));
						String baseUrl = builder.toString();
						builder.append(" and (");
						boolean isAllSelected = true, isNoneSelected = true;
						for (StandardWidget.Section section : sections) {
							if (section.selector.getValue()) {
								builder.append(sectionId)
										.append("=")
										.append(QueryBuilder.getQueryString(
												section.secStr,
												sectionMeta.getType()))
										.append("or ");
								isNoneSelected = false;
							} else {
								isAllSelected = false;
							}
						}
						if (isNoneSelected) {
							callback.onSuccess(null);
							return;
						}
						QueryBuilder queryBuilder = new QueryBuilder(
								Utils.sheetVizUrl).setMetaData(metaData)
								.addSelections(Generic.STANDARD,
										Generic.SECTION, Student.STUDENT_ID,
										Generic.NAME);
						if (isAllSelected) {
							callback.onSuccess(queryBuilder.addFilter(baseUrl)
									.get());
							return;
						}
						if (!isAllSelected && !isNoneSelected) {
							int length = builder.length();
							builder.delete(length - 3, length);
							builder.append(")");
						}
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

	public static class Section extends FlowPanel {
		public CheckBox selector;
		private Hyperlink inchargeLink;
		private HTML name;
		public String secStr;

		public Section(String section, String inchargeName, String inchargeId) {
			setStyleName("section");
			add(selector = new CheckBox(""));
			add(name = new HTML());
			name.setStyleName("fl");
			add(inchargeLink = new Hyperlink(inchargeName, "staff/"
					+ inchargeId));
			secStr = section;
		}

		public void setSelector(CheckBox selector) {
			this.selector = selector;
		}

		public CheckBox getSelector() {
			return selector;
		}

		public void setInchargeLink(Hyperlink inchargeLink) {
			this.inchargeLink = inchargeLink;
		}

		public Hyperlink getInchargeLink() {
			return inchargeLink;
		}

		public HTML setName(HTML name) {
			this.name = name;
			return name;
		}

		public HTML getName() {
			return name;
		}

		public void setSecStr(String secStr) {
			this.secStr = secStr;
		}

		public String getSecStr() {
			return secStr;
		}
	}

	public StandardWidget addSection(StandardWidget.Section section) {
		if (sections == null) {
			sections = new ArrayList<StandardWidget.Section>();
		}
		sectionsPanel.add(section);
		section.name.setHTML("<a href='#class/" + std + "-" + section.secStr
				+ "'>" + section.secStr + "</a>");
		sections.add(section);
		return this;
	}

	public ControlPanel getControlPanel() {
		return controlPanel;
	}
}