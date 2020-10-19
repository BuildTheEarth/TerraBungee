/*
 * TerraBungee - API
 * Copyright (c) 2020 Saghetti
 *
 * StaticRemoteInstance.java
 */

package com.noahhusby.terrabungee.api.services;

import org.json.simple.JSONObject;

public class Instance extends TerraBungeeService {
	public static final ServiceType type = ServiceType.INSTANCE;

	private String address;
	private boolean online;
	private boolean running;
	private String template;
	private InstanceType instanceType;

	public Instance(String id, String address, boolean online, boolean running, String template, String status, InstanceType type) {
		super(id);
		this.address = address;
		this.online = online;
		this.running = running;
		this.template = template;
		this.instanceType = type;
		this.setStatus(ServiceStatus.valueOf(status));
	}
	
	/**
	 * Checks if this instance actually exists on the network.
	 * This method is required because instances can be deleted by other services on the network.
	 * NOTE: always returns false if this instance is a static remote instance, because there is no guarantee that static remote instances actually exist.
	 * @return false
	 */
	public boolean exists() {
		return false;
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

	public InstanceType getInstanceType() {
		return instanceType;
	}

	@Override
	public String toString() {
		String addr = getAddress();
		if (addr != null) {
			return "Instance " + getId() + " on address " + address;
		} else {
			return "Instance " + getId();
		}
	}

	public static Instance fromJSON(JSONObject o) {
		return new Instance((String) o.get("id"), (String) o.get("address"), (boolean) o.get("online"),
				(boolean) o.get("running"), (String) o.get("template"), (String) o.get("status"), InstanceType.valueOf((String) o.get("type")));
	}

	public JSONObject toJSON() {
		JSONObject o = new JSONObject();
		o.put("id", getId());
		o.put("address", getAddress());
		o.put("online", isOnline());
		o.put("running", isRunning());
		o.put("template", getTemplate());
		o.put("status", getStatus().name());
		o.put("type", getInstanceType().name());
		return o;
	}

	public enum InstanceType {
		STATIC, DYNAMIC
	}
}
