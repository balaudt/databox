package com.sagarius.goddess.client.model.enumerations;

public enum SchoolType {
	MATRICULATION("Matriculation School"), PUBLIC("Public School"), ENGINEERING(
			"Engineering College"), ARTS("Arts & Science College");
	private String typeString;

	private SchoolType(String type) {
		this.typeString = type;
	}

	public String getTypeString() {
		return typeString;
	}

	public static SchoolType getTypeByString(String typeString) {
		SchoolType[] values = SchoolType.values();
		for (SchoolType type : values) {
			if (type.getTypeString().equals(typeString)) {
				return type;
			}
		}
		return null;
	}
}
