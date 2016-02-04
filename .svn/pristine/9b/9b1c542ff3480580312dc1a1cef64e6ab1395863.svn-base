package com.sagarius.goddess.server.util;

import java.net.URL;

import com.google.appengine.api.datastore.Key;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.FolderEntry;
import com.google.gdata.data.docs.SpreadsheetEntry;
import com.sagarius.goddess.server.Utils;
import com.sagarius.goddess.server.model.AcademicYear;

public class DocumentUtils {

	public static FolderEntry createSchoolFolder(String schoolId, String gToken) {
		FolderEntry folderEntry = new FolderEntry();
		folderEntry.setTitle(new PlainTextConstruct(schoolId));
		DocsService service = new DocsService("Goddess");
		service.setAuthSubToken(gToken);
		try {
			URL feedUrl = null;
			feedUrl = new URL(
					"https://docs.google.com/feeds/default/private/full/");
			return service.insert(feedUrl, folderEntry);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static DocumentListEntry createEmptySheet(String fileName,
			String gToken) {
		return createEmptySheet(fileName, gToken, null);
	}

	public static DocumentListEntry createEmptySheet(String fileName,
			String gToken, Key academicKey, boolean isFile) {
		Key yearKey = Utils.getAcademicYear();
		if (yearKey == null) {
			yearKey = academicKey;
		}
		String folderId = PMFactory.getManager()
				.getObjectById(AcademicYear.class, yearKey).getFolderId();
		DocsService service = new DocsService("Goddess");
		service.setAuthSubToken(gToken);
		DocumentListEntry newEntry = null;
		if (isFile) {
			newEntry = new SpreadsheetEntry();
		} else {
			newEntry = new FolderEntry();
		}
		newEntry.setTitle(new PlainTextConstruct(fileName));
		URL url;
		try {
			url = new URL(
					"http://docs.google.com/feeds/default/private/full/folder%3A"
							.concat(folderId).concat("/contents"));
			return service.insert(url, newEntry);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static DocumentListEntry createEmptySheet(String fileName,
			String gToken, Key academicKey) {
		return createEmptySheet(fileName, gToken, academicKey, true);
	}
}
