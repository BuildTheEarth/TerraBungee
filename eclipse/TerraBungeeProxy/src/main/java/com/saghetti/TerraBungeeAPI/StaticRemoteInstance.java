package com.saghetti.TerraBungeeAPI;

public class StaticRemoteInstance implements RemoteInstance {
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
	
	/**
	 * Checks if this instance actually exists on the network.
	 * This method is required because instances can be deleted by other services on the network.
	 * NOTE: always returns false if this instance is a static remote instance, because there is no guarantee that static remote instances actually exist.
	 * @return false
	 */
	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public String getAddress() {
		return address;
	}

	@Override
	public boolean isOnline() {
		return online;
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	public String getTemplate() {
		return template;
	}

	@Override
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
}
