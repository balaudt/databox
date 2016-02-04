package com.sagarius.goddess.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;

public class Dummy {
	public static void main(String[] args) throws MalformedURLException,
			IOException, ServiceException {
		SpreadsheetService service = new SpreadsheetService("Goddess");
		service.setAuthSubToken("1/bpY8IxYsjCmtyes7u6UP15L3Pvs0WPiF40uKGLaC8zk");
		WorksheetEntry cssEntry = service
				.getEntry(
						new URL(
								"https://spreadsheets.google.com/feeds/worksheets/tjIaJlYmRbmNrJwiVNQFISA/od6/private/full"),
						WorksheetEntry.class);
		System.out.println(cssEntry.getId());
	}
}
