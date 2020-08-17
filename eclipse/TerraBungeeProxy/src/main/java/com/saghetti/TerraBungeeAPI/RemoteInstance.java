package com.saghetti.TerraBungeeAPI;

public interface RemoteInstance {
	/**
	 * Checks if this instance actually exists on the network.
	 * This method is required because instances can be deleted by other services on the network.
	 * @return If this instance exists.
	 */
	public boolean exists();
	
	/**
	 * Gets the ID of this instance
	 * @return The ID of this instance.
	 */
	public String getId();
	
	/**
	 * Gets the address of this instance
	 * @return The address of the instance, or null if it isn't online.
	 */
	public String getAddress();
	
	/**
	 * Checks if this instance is online. (If it has finished starting up and a player can connect to it)
	 * @return If this instance is online or not.
	 */
	public boolean isOnline();
	
	/**
	 * Checks if this instance is running. (If the process itself is running)
	 * @return If this instance is running or not.
	 */
	public boolean isRunning();

	/**
	 * Gets the template that this instance was created using.
	 * @return The template used to create this instance.
	 */
	public String getTemplate();
}
