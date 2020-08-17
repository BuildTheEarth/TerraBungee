package com.saghetti.TerraBungeeAPI;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class APIHelper {
	public static APIResponse makeRawRequest(HttpUriRequest request, HttpClient httpClient) {
		HttpResponse response;
		try {
			response = httpClient.execute(request);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		String responseData = "";
		try {
			responseData = EntityUtils.toString(response.getEntity());
		} catch (ParseException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		JsonElement jsonElement = new JsonParser().parse(responseData);
		return new APIResponse(response, jsonElement);
	}
}
