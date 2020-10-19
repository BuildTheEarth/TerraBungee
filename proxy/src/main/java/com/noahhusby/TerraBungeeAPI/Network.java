/*
 * TerraBungee - API
 * Copyright (c) 2020 Saghetti
 *
 * Network.java
 */

package com.noahhusby.TerraBungeeAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Network {
	protected String controllerURL;
	protected CloseableHttpClient httpClient;
	
	/**
	 * @param controllerURL The URL that the controller can be accessed from.
	 * @throws NetworkException If the controller could not be pinged.
	 */
	public Network(String controllerURL) throws NetworkException {
		this.controllerURL = controllerURL;
		httpClient = HttpClientBuilder.create().build();
		if (!pingNetwork()) {
			throw new NetworkException("Unable to ping controller");
		}
	}
	
	/**
	 * Pings the controller to check if it's available.
	 * @return If the ping succeeded.
	 */
	public boolean pingNetwork() {
		HttpGet request = new HttpGet(controllerURL + "api/ping");
		HttpResponse response;
		try {
			response = httpClient.execute(request);
		} catch (IOException e) {
			return false;
		}
		if (response.getStatusLine().getStatusCode() != 200) {
			return false;
		}
		return true;
	}
	
	/**
	 * Closes the connection to the controller. Sending HTTP calls to the controller after closing the connection will result in undefined behavior.
	 * @throws IOException If an error occurred while closing the connection.
	 */
	public void closeConnection() throws IOException {
		httpClient.close();
	}
	
	/**
	 * Gets a list of all instances on the network.
	 * @return A list of all instances on the network.
	 */
	public List<DynamicRemoteInstance> getAllInstances() {
		List<DynamicRemoteInstance> returnData = new ArrayList<DynamicRemoteInstance>();
		HttpGet request = new HttpGet(controllerURL + "api/instances/");
		APIResponse apiResponse = APIHelper.makeRawRequest(request, httpClient);
		if (apiResponse.getStatusCode() != 200) return returnData;
		//return apiResponse.getJson().get("online").getAsBoolean();
		JsonArray instanceJsonArray = apiResponse.getJson().getAsJsonArray();
		for (JsonElement instanceJsonElement : instanceJsonArray) {
			JsonObject instanceJsonObject = instanceJsonElement.getAsJsonObject();
			returnData.add(new DynamicRemoteInstance(instanceJsonObject.get("id").getAsString(), this));
		}
		return returnData;
	}
	
	/**
	 * Gets a list of all instances on the network, read-only.
	 * NOTE: the data for the objects returned will not automatically update.
	 * This is more efficient as it only takes one HTTP request to fetch all data, but is only useful under certain circumstances (such as printing a list of all instances to a player).
	 * @return A list of StaticRemoteInstances representing all instances on the network.
	 */
	public List<StaticRemoteInstance> getAllInstancesStatic() {
		List<StaticRemoteInstance> returnData = new ArrayList<StaticRemoteInstance>();
		HttpGet request = new HttpGet(controllerURL + "api/instances/");
		APIResponse apiResponse = APIHelper.makeRawRequest(request, httpClient);
		if (apiResponse.getStatusCode() != 200) return returnData;
		//return apiResponse.getJson().get("online").getAsBoolean();
		JsonArray instanceJsonArray = apiResponse.getJson().getAsJsonArray();
		for (JsonElement instanceJsonElement : instanceJsonArray) {
			JsonObject instanceJsonObject = instanceJsonElement.getAsJsonObject();
			if (instanceJsonObject.get("address") != null) {
				returnData.add(new StaticRemoteInstance(
					instanceJsonObject.get("id").getAsString(),
					instanceJsonObject.get("address").getAsString(),
					instanceJsonObject.get("online").getAsBoolean(),
					instanceJsonObject.get("running").getAsBoolean(),
					instanceJsonObject.get("template").getAsString()
				));
			} else {
				returnData.add(new StaticRemoteInstance(
					instanceJsonObject.get("id").getAsString(),
					null,
					instanceJsonObject.get("online").getAsBoolean(),
					instanceJsonObject.get("running").getAsBoolean(),
					instanceJsonObject.get("template").getAsString()
				));
			}
		}
		return returnData;
	}
}
