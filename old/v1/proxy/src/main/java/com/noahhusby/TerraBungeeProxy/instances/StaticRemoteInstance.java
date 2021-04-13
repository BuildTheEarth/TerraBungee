/*
 * TerraBungee - API
 * Copyright (c) 2020 Saghetti
 *
 * StaticRemoteInstance.java
 */

package com.noahhusby.TerraBungeeProxy.instances;

import com.google.gson.JsonObject;

public class StaticRemoteInstance {
	private String id;
	private String address;
	private boolean online;
	private boolean running;
	private String template;
	
	public StaticRemoteInstance(String id, String address, boolean online, boolean running, String template) {
		this.id = id;
		this.address = address;
		this.online = online;
		this.running = running;
		this.template = template;
	}

	public String getAddress() {
		return address;
	}

	public boolean isOnline() {
		return online;
	}

	public boolean isRunning() {
		return running;
	}

	public String getTemplate() {
		return template;
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		String addr = getAddress();
		if (addr != null) {
			return "Instance " + id + " on address " + address;
		} else {
			return "Instance " + id;
		}
	}

	public static StaticRemoteInstance fromJSON(JsonObject o) {
		return new StaticRemoteInstance(o.get("id").getAsString(), o.get("address").getAsString(), o.get("online").getAsBoolean(),
				o.get("running").getAsBoolean(), o.get("template").getAsString());
	}
}
