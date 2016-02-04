package com.sagarius.goddess.server.resources;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

import org.apache.commons.fileupload.util.Streams;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.data.Tag;
import org.restlet.ext.servlet.ServletUtils;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.gdata.client.Query;
import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.MediaContent;
import com.google.gdata.data.photos.PhotoFeed;
import com.google.gdata.util.NotModifiedException;
import com.google.gdata.util.ServiceException;
import com.sagarius.goddess.server.Utils;

public class PhotoResource extends ServerResource {
	public PhotoResource() {
		getVariants().add(new Variant(MediaType.IMAGE_ALL));
	}

	@Override
	protected Representation get(Variant variant) throws ResourceException {
		Utils.initializeSchool();
		Form form = getRequest().getResourceRef().getQueryAsForm();
		String size = form.getFirstValue("size");
		String id = form.getFirstValue("id");
		if (id == null) {
			return null;
		}
		if (size == null) {
			size = "144";
		}
		try {
			URL DEFAULT_PHOTO_URL = new URL(
					"http://picasaweb.google.com/data/feed/api/user/default");
			Query query = new Query(DEFAULT_PHOTO_URL);
			query.setStringCustomParameter("tag", id);
			query.setStringCustomParameter("kind", "photo");
			query.setStrict(true);
			PicasawebService PICASA_SERVICE = new PicasawebService("Goddess");
			PICASA_SERVICE.setAuthSubToken((String) ServletUtils
					.getRequest(getRequest()).getSession()
					.getAttribute("picToken"));
			List<Tag> tags = getRequest().getConditions().getNoneMatch();
			String etag = null;
			if (tags != null && tags.size() > 0) {
				etag = tags.get(0).format();
			}
			PhotoFeed photoFeed = PICASA_SERVICE.getFeed(query,
					PhotoFeed.class, etag);
			int resultCount = photoFeed.getTotalResults();
			if (resultCount == 0) {
				OutputRepresentation representation = new OutputRepresentation(
						MediaType.IMAGE_GIF) {

					@Override
					public void write(OutputStream outputStream)
							throws IOException {
						FileInputStream inputStream = new FileInputStream(
								"goddess/images/q_silhouette.gif");
						Streams.copy(inputStream, outputStream, true);
					}
				};
				representation.setTag(Tag.parse(photoFeed.getEtag()));
				return representation;
			}
			String link = ((MediaContent) photoFeed.getEntries().get(0)
					.getContent()).getUri();
			String url = link;
			if (!size.equals("0")) {
				int i = link.lastIndexOf('/');
				url = link.substring(0, i + 1).concat("s").concat(size)
						.concat("/")
						.concat(link.substring(i + 1, link.length()));
			}
			final URL imageUrl = new URL(url);
			OutputRepresentation representation = new OutputRepresentation(
					MediaType.IMAGE_JPEG) {

				@Override
				public void write(OutputStream arg0) throws IOException {
					Streams.copy(imageUrl.openStream(), arg0, true);
				}
			};
			representation.setTag(Tag.parse(photoFeed.getEtag()));
			return representation;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ServiceException e) {
			if (e instanceof NotModifiedException) {
				getResponse().setStatus(Status.REDIRECTION_NOT_MODIFIED);
				return null;
			}
			e.printStackTrace();
			return null;
		}
	}
}
