/*
 * TerraBungee - API
 * Copyright (c) 2020 Saghetti
 *
 * APIResponse.java
 */

package com.noahhusby.TerraBungeeAPI;

import org.apache.http.HttpResponse;

import com.google.gson.JsonElement;

public class APIResponse {
	private HttpResponse response;
	private JsonElement json;
	
	public APIResponse(HttpResponse response, JsonElement json) {
		this.response = response;
		this.json = json;
	}
	
	public HttpResponse getResponse() {
		return response;
	}
	
	public JsonElement getJson() {
		return json;
	}
	
	public int getStatusCode() {
		return response.getStatusLine().getStatusCode();
	}
}
