/*
 * TerraBungee - API
 * Copyright (c) 2020 Saghetti
 *
 * DynamicRemoteInstance.java
 */

package com.noahhusby.TerraBungeeAPI;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

public class DynamicRemoteInstance implements RemoteInstance {
	private String instanceId;
	private Network parentNetwork;
	
	public DynamicRemoteInstance(String instanceId, Network parentNetwork) {
		this.instanceId = instanceId;
		this.parentNetwork = parentNetwork;
	}
	
	public boolean exists() {
		HttpGet request = new HttpGet(parentNetwork.controllerURL + "api/instances/" + instanceId);
		HttpResponse response;
		try {
			response = parentNetwork.httpClient.execute(request);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		return ((response.getStatusLine().getStatusCode() >= 200) && (response.getStatusLine().getStatusCode() <= 299));
	}
	
	public String getAddress() {
		if (!isRunning()) return null;
		HttpGet request = new HttpGet(parentNetwork.controllerURL + "api/instances/" + instanceId);
		APIResponse apiResponse = APIHelper.makeRawRequest(request, parentNetwork.httpClient);
		if (apiResponse.getStatusCode() != 200) return null;
		return apiResponse.getJson().getAsJsonObject().get("address").getAsString();
	}
	
	public boolean isOnline() {
		HttpGet request = new HttpGet(parentNetwork.controllerURL + "api/instances/" + instanceId);
		APIResponse apiResponse = APIHelper.makeRawRequest(request, parentNetwork.httpClient);
		if (apiResponse.getStatusCode() != 200) return false;
		return apiResponse.getJson().getAsJsonObject().get("online").getAsBoolean();
	}
	
	public boolean isRunning() {
		HttpGet request = new HttpGet(parentNetwork.controllerURL + "api/instances/" + instanceId);
		APIResponse apiResponse = APIHelper.makeRawRequest(request, parentNetwork.httpClient);
		if (apiResponse.getStatusCode() != 200) return false;
		return apiResponse.getJson().getAsJsonObject().get("running").getAsBoolean();
	}
	
	public void start() {
		if (isRunning()) return;
	}
	
	public void stop() {
		if (!isRunning()) return;
	}
	
	@Override
	public String toString() {
		String addr = getAddress();
		if (addr != null) {
			return "Instance " + instanceId + " on address " + addr;
		} else {
			return "Instance " + instanceId;
		}
	}

	@Override
	public String getTemplate() {
		HttpGet request = new HttpGet(parentNetwork.controllerURL + "api/instances/" + instanceId);
		APIResponse apiResponse = APIHelper.makeRawRequest(request, parentNetwork.httpClient);
		if (apiResponse.getStatusCode() != 200) return null;
		return apiResponse.getJson().getAsJsonObject().get("template").getAsString();
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return instanceId;
	}
}
