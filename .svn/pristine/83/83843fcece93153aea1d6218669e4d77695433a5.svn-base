package com.sagarius.goddess.server.util;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

public class PMFactory {
	private static final PersistenceManager MANAGER = JDOHelper
			.getPersistenceManagerFactory("goddess").getPersistenceManager();

	public static PersistenceManager getManager() {
		return MANAGER;
	}
}
