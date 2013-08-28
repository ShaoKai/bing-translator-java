package com.sky.tools.translate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class TranslatorClient implements ITranslatorClient {
	private ClientConfig clientConfig;
	private Client client;
	final static private String from = "en";
	private String to;
	private String appId;

	final private static String domain = "https://api.datamarket.azure.com/Bing/MicrosoftTranslator/v1/Translate";

	public TranslatorClient(String appId, String to) {
		super();
		this.appId = appId;
		this.to = to;
		this.clientConfig = new DefaultClientConfig();
		this.clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		this.client = Client.create(clientConfig);

	}

	private WebResource createBaseResource() {
		WebResource baseResource = client.resource(domain);
		// baseResource.addFilter(new LoggingFilter());
		baseResource.addFilter(new ClientFilter() {

			@Override
			public ClientResponse handle(final ClientRequest request) {
				final Map<String, List<Object>> headers = request.getHeaders();
				final List<Object> valueList = new ArrayList<Object>();

				String code = "Basic " + Base64Coder.encodeString(":" + appId).toString();
				valueList.add(code);
				headers.put("Authorization", valueList);

				return getNext().handle(request);
			}

		});
		return baseResource;
	}

	public String translate(String text) {
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		params.putSingle("To", "'" + to + "'");
		if (from != null) {
			params.putSingle("From", "'" + from + "'");
		}
		params.putSingle("Text", "'" + text + "'");

		StringBuilder sb = new StringBuilder(createBaseResource().queryParams(params).type("text/plain").get(String.class));
		int startIndex = sb.indexOf("Edm.String") + 12;
		int endIndex = sb.indexOf("</d:Text>");
		return sb.substring(startIndex, endIndex);
	}
}
