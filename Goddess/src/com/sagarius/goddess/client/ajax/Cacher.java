package com.sagarius.goddess.client.ajax;

import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sagarius.goddess.client.model.enumerations.DocumentType;
import com.sagarius.goddess.client.present.NewSpreadsheetUtils;
import com.sagarius.goddess.client.utils.DocumentMetaData;
import com.sagarius.goddess.client.utils.MetaDataRepository;

public class Cacher {
	@SuppressWarnings("rawtypes")
	public static CacheHandler get(CacheType type) {
		switch (type) {
		case DOCUMENT:
			return new CacheHandler<DocumentType, DocumentMetaData>() {

				@Override
				public void handle(DocumentType input,
						AsyncCallback<DocumentMetaData> output) {
					MetaDataRepository.get(input, output);
				}
			};
		case STAFF_MAP:
			return new CacheHandler<Void, Map<String, String>>() {

				@Override
				public void handle(Void input,
						AsyncCallback<Map<String, String>> output) {
					NewSpreadsheetUtils.getNewStaffMap(false, output);
				}
			};
		}
		return null;
	}
}
