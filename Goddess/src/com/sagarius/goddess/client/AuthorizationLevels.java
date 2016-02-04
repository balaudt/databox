package com.sagarius.goddess.client;

import java.util.Map;

import org.sagarius.radix.client.collect.Maps.ImmutableHashMap;

import com.sagarius.goddess.client.model.enumerations.MemberType;

public class AuthorizationLevels {
	public static final Map<Integer, String[]> MENU_ITEMS = new ImmutableHashMap<Integer, String[]>()
			.put(0, new String[] { "Home", "home" })
			.put(1, new String[] { "Classes", "class" })
			.put(2, new String[] { "Staffs", "staff" })
			.put(3, new String[] { "Students", "student" })
			.put(4, new String[] { "Exam", "exam" })
			.put(5, new String[] { "Files", "files" })
			.put(6, new String[] { "SMS", "sms" })
			.put(7, new String[] { "Settings", "settings" })
			.put(8, new String[] { "Attendance", "attendance" }).build();

	public static int[] getMenus(MemberType type) {
		switch (type) {
		case ADMINISTRATOR:
		case STAFF:
		case PRINCIPAL:
			return new int[] { 0, 1, 2, 4, 5, 6, 7, 8 };
		case PARENT:
			return new int[] { 0, 3 };
		case NON_TEACHING_STAFF:
		case STUDENT:
		default:
			return new int[] { 0 };
		}
	}
}
