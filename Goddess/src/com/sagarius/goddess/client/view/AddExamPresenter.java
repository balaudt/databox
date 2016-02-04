package com.sagarius.goddess.client.view;

import java.util.List;

import org.restlet.client.Request;
import org.restlet.client.Response;
import org.restlet.client.Uniform;
import org.restlet.client.data.Method;
import org.restlet.client.representation.StringRepresentation;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.sagarius.goddess.client.Utils;
import com.sagarius.goddess.client.ajax.GenericRequest;
import com.sagarius.goddess.client.ajax.RequestType;
import com.sagarius.goddess.client.present.CrumbHandler;
import com.sagarius.goddess.client.utils.Singletons;
import com.sagarius.goddess.client.view.util.Breadcrumb;
import com.sagarius.goddess.client.view.util.HiddenRowButtons;
import com.sagarius.goddess.client.view.util.LoadingPanel;
import com.sagarius.goddess.client.view.util.MessagePopup;
import com.sagarius.goddess.client.view.util.StandardsGadget;

public class AddExamPresenter extends StandardsGadget implements CrumbHandler {
	private final class CreateHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			createAnchor.setEnabled(false);
			List<String> selectedClasses = getSelectedClasses();
			System.out.println(selectedClasses);
			JSONObject reqInput = new JSONObject();
			reqInput.put("name", new JSONString(nameBox.getText()));
			JSONArray clazzes = new JSONArray();
			reqInput.put("clazz", clazzes);
			int i = 0;
			for (String selectedClass : selectedClasses) {
				clazzes.set(i++, new JSONString(selectedClass));
			}
			LoadingPanel.INSTANCE.setMessage("Creating exam...").center();
			Singletons.getClient().handle(
					new Request(Method.POST, "/nexam",
							new StringRepresentation(reqInput.toString())),
					new Uniform() {

						@Override
						public void handle(Request request, Response response) {
							createAnchor.setEnabled(true);
							LoadingPanel.INSTANCE.hide();
							if (response.getStatus().isError()) {
								MessagePopup.INSTANCE
										.setMessage(
												"There has been a problem creating exam.  Please try after some time")
										.center();
								return;
							}
							try {
								final PopupPanel panel = new PopupPanel(false,
										true);
								Grid grid = new Grid(2, 1);
								panel.setWidget(grid);
								grid.setWidget(0, 0, new HTML(
										"A new document has beeen created @ <a href='"
												+ response.getEntity()
														.getText()
												+ "' target='_blank'>"
												+ nameBox.getText() + "<a/>"));
								grid.setWidget(1, 0, new Button("Ok",
										new ClickHandler() {

											@Override
											public void onClick(ClickEvent event) {
												panel.hide(true);
												History.newItem("exam", true);
												DomEvent.fireNativeEvent(
														Document.get()
																.createClickEvent(
																		0, 0,
																		0, 0,
																		0,
																		true,
																		false,
																		false,
																		false),
														HiddenRowButtons
																.getRefreshButton());
											}
										}));
								panel.center();
							} catch (Exception e) {
								e.printStackTrace();
								MessagePopup.INSTANCE
										.setMessage(
												"There has been a problem creating exam.  Please try after some time")
										.center();
							}
						}
					});
		}
	}

	// TODO: The fields that are to be copied from the students' list to exam
	// file can
	// also be got from the user. As of now, the two mandate fields: name and
	// studentid are generated
	private TextBox nameBox;
	private Anchor createAnchor;

	public AddExamPresenter() {
		Grid nameGrid = new Grid(1, 2);
		nameGrid.setText(0, 0, "Name");
		nameBox = new TextBox();
		nameGrid.setWidget(0, 1, nameBox);
		header.add(nameGrid);
		createAnchor = new Anchor("Create", "javascript:");
		footer.add(createAnchor);
		createAnchor.addClickHandler(new CreateHandler());
		refresh();
	}

	@Override
	public void handleCrumb() {
		Hyperlink link = (Hyperlink) Breadcrumb.getCrumbAt(1, true);
		link.setTargetHistoryToken("exam");
		link.setText("Exams");
		com.sagarius.goddess.client.Utils.setActiveMainMenu("exam");

		((Label) Breadcrumb.getCrumbAt(2, false)).setText("New Exam");
		Breadcrumb.ensureMaxLength(3);
		if (Utils.currentSchool != null) {
			Window.setTitle("Databox - " + Utils.currentSchool.getName()
					+ " - New exam");
		}

		HiddenRowButtons.clear();
	}

	@Override
	public void addControls() {

	}

	@Override
	protected void buildRequest(AsyncCallback<Void> callback) {
		if (request == null) {
			String requestUri = builder.get();
			request = new GenericRequest().setType(RequestType.VISUALIZATION)
					.setVisualizationQuery(requestUri);
		}
		callback.onSuccess(null);
	}

}