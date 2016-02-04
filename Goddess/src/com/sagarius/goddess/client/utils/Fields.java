package com.sagarius.goddess.client.utils;

public interface Fields {
	public static final String[] CLASS_META = new String[] { Generic.STANDARD,
			Generic.SECTION, Clazz.INCHARGE_ID, Generic.CATEGORY };
	public static final String[] STAFF_META = new String[] { Generic.NAME,
			Generic.MOBILE, Staff.STAFF_ID, Generic.CATEGORY };
	public static final String[] STUDENT_META = new String[] { Generic.NAME,
			Student.STUDENT_ID, Student.PARENTS_MOBILE, Generic.STANDARD,
			Generic.SECTION };
	public static final String[] RELATIONS_META = new String[] {
			Generic.STANDARD, Generic.SECTION, Staff.STAFF_ID, Generic.SUBJECT };

	public static class Generic {
		public static final String STANDARD = "standard";
		public static final String SECTION = "section";
		public static final String NAME = "name";
		public static final String EMAIL = "email";
		public static final String MOBILE = "mobile";
		public static final String SUBJECT = "subject";
		public static final String CATEGORY = "category";
	}

	public static class Staff {
		public static final String STAFF_ID = "staffid";
	}

	public static class Clazz {
		public static final String INCHARGE_ID = "incharge";
	}

	public static class Student {
		public static final String STUDENT_ID = "studentid";
		public static final String ADMISSION_ID = "admissionno";
		public static final String PARENTS_MOBILE = "parentsmobile";
		public static final String PARENTS_EMAIL = "parentsemail";
	}

	public static class Examination {
		public static final String TOTAL = "total";
		public static final String AVERAGE = "average";
		public static final String RANK = "rank";
		public static final String PASSED = "passed";
		public static final String MINIMUM_MARKS = "Minimum marks";
		public static final String MAXIMUM_MARKS = "Maximum marks";
		public static final String HIGHEST = "highest";
		public static final String HIGHEST_ROW = "Highest";
		public static final String AVERAGE_ROW = "Average";
		public static final String NO_OF_PASS = "noofpass";
		public static final String NO_OF_PASS_ROW = "No of Pass";
		public static final String NO_OF_FAIL = "nooffail";
		public static final String NO_OF_FAIL_ROW = "No of Fail";
		public static final String NO_OF_ABS = "No of abs";
		public static final String ABOVE_90 = "above90";
		public static final String ABOVE_90_ROW = "Above 90";
	}
}
