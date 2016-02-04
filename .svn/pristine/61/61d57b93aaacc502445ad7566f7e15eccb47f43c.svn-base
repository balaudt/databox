package org.sagarius.radix.server.test;

import java.util.Date;

import org.sagarius.radix.client.model.enumerations.Status;
import org.sagarius.radix.server.model.Metadata;

public class TestClazz {
	public static void main(String[] args) {
		Metadata metadata = new Metadata();
		metadata.setPublished(new Date());
		metadata.setVersion("Something");
		metadata.setStatus(Status.ACTIVE);
		String json = metadata.render();
		System.out.println(json);

		metadata = new Metadata(json);
		System.out.println(metadata);
	}

	public static void test() {
		System.out.println("Test");
	}
}
