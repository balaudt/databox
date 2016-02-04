package org.sagarius.radix.client.collect;

import org.sagarius.radix.client.util.JSMap;

import com.google.gwt.core.client.JavaScriptObject;

public class Filter extends JavaScriptObject {
	protected Filter() {
	}

	public static native Filter create()/*-{
		return {};
	}-*/;

	public final Filter add(String name, Operator operator,
			JavaScriptObject value) {
		return add(name, operator, value, false);
	}

	public final Filter add(String name, Operator operator, int value) {
		return add(name, operator, value, false);
	}

	public final Filter add(String name, Operator operator, float value) {
		return add(name, operator, value, false);
	}

	public final Filter add(String name, Operator operator, double value) {
		return add(name, operator, value, false);
	}

	public final Filter add(String name, Operator operator, boolean value) {
		return add(name, operator, value, false);
	}

	public final Filter add(String name, Operator operator, String value) {
		return add(name, operator, value, false);
	}

	public final Filter add(String name, Operator operator,
			JavaScriptObject value, boolean doInvert) {
		String operatorString = "";
		if (doInvert) {
			operatorString = "!";
		}
		operatorString += operator.getOperator();
		JSMap right = JSMap.create().put(operatorString, value);
		JSMap asMap = cast();
		asMap.put(name, right);
		return this;
	}

	public final Filter add(String name, Operator operator, int value,
			boolean doInvert) {
		String operatorString = "";
		if (doInvert) {
			operatorString = "!";
		}
		operatorString += operator.getOperator();
		JSMap right = JSMap.create().put(operatorString, value);
		JSMap asMap = cast();
		asMap.put(name, right);
		return this;
	}

	public final Filter add(String name, Operator operator, float value,
			boolean doInvert) {
		String operatorString = "";
		if (doInvert) {
			operatorString = "!";
		}
		operatorString += operator.getOperator();
		JSMap right = JSMap.create().put(operatorString, value);
		JSMap asMap = cast();
		asMap.put(name, right);
		return this;
	}

	public final Filter add(String name, Operator operator, double value,
			boolean doInvert) {
		String operatorString = "";
		if (doInvert) {
			operatorString = "!";
		}
		operatorString += operator.getOperator();
		JSMap right = JSMap.create().put(operatorString, value);
		JSMap asMap = cast();
		asMap.put(name, right);
		return this;
	}

	public final Filter add(String name, Operator operator, boolean value,
			boolean doInvert) {
		String operatorString = "";
		if (doInvert) {
			operatorString = "!";
		}
		operatorString += operator.getOperator();
		JSMap right = JSMap.create().put(operatorString, value);
		JSMap asMap = cast();
		asMap.put(name, right);
		return this;
	}

	public final Filter add(String name, Operator operator, String value,
			boolean doInvert) {
		String operatorString = "";
		if (doInvert) {
			operatorString = "!";
		}
		operatorString += operator.getOperator();
		JSMap right = JSMap.create().put(operatorString, value);
		JSMap asMap = cast();
		asMap.put(name, right);
		return this;
	}
}
