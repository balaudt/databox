package com.sagarius.goddess.client.gadgets;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sagarius.goddess.client.widgets.BaseWidget;

public abstract class ProfileGadget extends BaseWidget {

	public ProfileGadget() {
		mainPanel.setStyleName("profile");
	}

	public ProfileGadget setName(String name) {
		FlowPanel nameContainer = new FlowPanel();
		nameContainer.setStyleName("name");
		Label nameWidget = new Label(name);
		nameWidget.setStyleName("h18 strong mb3");
		nameContainer.add(nameWidget);
		contentsPanel.add(nameContainer);
		return this;
	}

	public ProfileGadget setImage(String imageUrl, boolean isLeftFloat,
			int span, int width) {
		FlowPanel imageContainer = new FlowPanel();
		imageContainer.setStyleName("photo span-" + span);
		if (isLeftFloat) {
			imageContainer.addStyleName("fl");
		} else {
			imageContainer.addStyleName("fr");
		}
		FlowPanel anotherOne = new FlowPanel();
		anotherOne.setStyleName("br1 p5");
		Image schoolImage = new Image(imageUrl);
		schoolImage.getElement()
				.setAttribute("style", "width:" + width + "px;");
		anotherOne.add(schoolImage);
		imageContainer.add(anotherOne);
		contentsPanel.add(imageContainer);
		return this;
	}

	public ProfileGadget setDetails(ProfileDetails details) {
		contentsPanel.add(details);
		return this;
	}

	public static class ProfileDetails extends FlowPanel {
		public ProfileDetails(int span, boolean isLast) {
			setStyleName("details span-" + span);
			if (isLast) {
				addStyleName("last");
			}
		}

		public static class FormWidget extends FlowPanel {
			public FormWidget() {
				setStyleName("form");
			}

			public static class FormElement extends FlowPanel {
				private FlowPanel elementValue;

				public FormElement(boolean doClear, int valueSpan,
						boolean isLast) {
					setStyleName("element");
					if (doClear) {
						addStyleName("clear");
					}
					if (isLast) {
						addStyleName("last");
					}
					elementValue = new FlowPanel();
					elementValue.setStyleName("value-" + valueSpan + " last");
					add(elementValue);
				}

				public FormElement(boolean doClear, int valueSpan) {
					this(doClear, valueSpan, false);
				}

				public FormElement setMeta(String metaStr) {
					HTML meta = new HTML(metaStr);
					meta.setStyleName("meta");
					elementValue.add(meta);
					return this;
				}

				public FormElement setMeta(Widget meta) {
					meta.setStyleName("meta");
					elementValue.add(meta);
					return this;
				}

				public FormElement setName(String nameStr) {
					HTML meta = new HTML(nameStr);
					meta.setStyleName("name");
					insert(meta, 0);
					return this;
				}

				public FormElement setName(Widget name) {
					name.setStyleName("name");
					insert(name, 0);
					return this;
				}

				public FormElement setValue(String valueStr) {
					HTML value = new HTML(valueStr);
					elementValue.add(value);
					return this;
				}

				public FormElement setValue(Widget value) {
					elementValue.add(value);
					return this;
				}
			}

			public FormWidget addElement(FormElement element) {
				add(element);
				return this;
			}
		}

		public ProfileDetails setFormWidget(FormWidget formWidget) {
			add(formWidget);
			return this;
		}
	}
}
