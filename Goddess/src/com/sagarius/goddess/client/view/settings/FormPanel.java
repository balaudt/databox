package com.sagarius.goddess.client.view.settings;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class FormPanel extends FlowPanel {
	public FormPanel() {
		setStyleName("form-preset-1 pt40 span-14 append-1");
	}

	public FormPanel addWidget(Widget widget) {
		add(widget);
		return this;
	}

	public FormPanel addElement(FormElement element) {
		add(element);
		return this;
	}

	public static class FormElement extends FlowPanel {
		public FormElement() {
			setStyleName("element");
		}

		public FormElement setName(String name) {
			HTML nameLabel = new HTML(name);
			nameLabel.setStyleName("name");
			add(nameLabel);
			return this;
		}

		public FormElement setValue(ElementValue value) {
			add(value);
			return this;
		}
	}

	public static class ElementValue extends FlowPanel {
		public ElementValue() {
			setStyleName("value");
		}

		public ElementValue setText(String text) {
			add(new HTML(text));
			return this;
		}

		public ElementValue setWidget(Widget widget) {
			add(widget);
			return this;
		}

		public ElementValue setMeta(String meta) {
			HTML metaHtml = new HTML(meta);
			metaHtml.setStyleName("meta");
			add(metaHtml);
			return this;
		}
	}
}
