package com.sagarius.goddess.client;

import org.restlet.client.Request;
import org.restlet.client.Response;
import org.restlet.client.Uniform;
import org.restlet.client.data.Method;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.sagarius.goddess.client.model.enumerations.SchoolType;
import com.sagarius.goddess.client.utils.Singletons;

public class RegisterPage extends Composite {

	public RegisterPage() {
		final FlowPanel mainPanel = new FlowPanel();
		Label headerLabel = new Label("Register a new school");
		headerLabel.setStyleName("cl text-preset-3");
		mainPanel.add(headerLabel);
		String[] labels = new String[] { "Name", "Address", "Phone Number",
				"Mobile Number", "Website", "E-mail", "Student Count",
				"Staff Count", "Host domain", "Admin e-mail" };
		final Grid schoolGrid = new Grid(labels.length + 4, 2);
		schoolGrid.setStyleName("cl");
		schoolGrid.setText(0, 0, "School id");
		final TextBox idBox = new TextBox();
		idBox.setName("id");
		schoolGrid.setWidget(0, 1, idBox);
		final Label statusLabel = new Label();
		schoolGrid.setWidget(1, 1, statusLabel);
		schoolGrid.setWidget(1, 0, new Button("Check availability",
				new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						Singletons.getClient().handle(
								new Request(Method.HEAD, "/register?id="
										.concat(idBox.getText())),
								new Uniform() {

									@Override
									public void handle(Request request,
											Response response) {
										if (response.getStatus().isError()) {
											statusLabel.getElement()
													.setAttribute("style",
															"color: red;");
											statusLabel
													.setText("The id is already in use");
										} else {
											statusLabel.getElement()
													.setAttribute("style",
															"color: green;");
											statusLabel
													.setText("The id is available");
										}
									}
								});
					}
				}));
		TextBox[] fields = new TextBox[labels.length];
		int i;
		for (i = 0; i < labels.length; i++) {
			schoolGrid.setText(i + 2, 0, labels[i]);
			schoolGrid.setWidget(i + 2, 1, fields[i] = new TextBox());
			fields[i].setName(labels[i].toLowerCase().replaceAll(" ", ""));
		}
		schoolGrid.setText(i + 2, 0, "Category");
		ListBox categoryBox = new ListBox(false);
		SchoolType[] types = SchoolType.values();
		for (SchoolType schoolType : types) {
			categoryBox.addItem(schoolType.getTypeString(),
					schoolType.toString());
		}
		categoryBox.setName("category");
		schoolGrid.setWidget(i + 2, 1, categoryBox);
		schoolGrid.setWidget(i + 3, 0, new Button("Register",
				new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						int rowCount = schoolGrid.getRowCount();
						StringBuffer buffer = new StringBuffer(Window.Location
								.getPath()
								.replace("register.html", "register?"));
						for (int j = 0; j < rowCount; j++) {
							Widget widget = schoolGrid.getWidget(j, 1);
							if (widget instanceof TextBox) {
								TextBox textBox = (TextBox) widget;
								buffer.append(textBox.getName()).append("=")
										.append(textBox.getValue()).append("&");
							} else if (widget instanceof ListBox) {
								ListBox listBox = (ListBox) widget;
								buffer.append(listBox.getName())
										.append("=")
										.append(listBox.getValue(listBox
												.getSelectedIndex()))
										.append("&");
							}
						}
						buffer.append("step=-1");
						Window.Location.replace(buffer.toString());
					}
				}));
		// formPanel.addSubmitCompleteHandler(new SubmitCompleteHandler() {
		//
		// @Override
		// public void onSubmitComplete(SubmitCompleteEvent event) {
		// mainPanel.clear();
		// mainPanel
		// .add(new Label(
		// "You can continue the deployment process by following the link in your mail box"));
		// }
		// });
		mainPanel.add(schoolGrid);
		initWidget(mainPanel);
	}

}
