package org.sagarius.radix.client.uri;

import java.util.Map;

public interface StatefulWidget {
	Template getTemplate();

	void handle(Map<String, String> attributes);
}
