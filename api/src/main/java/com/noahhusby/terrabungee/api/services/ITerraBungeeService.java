package com.noahhusby.terrabungee.api.services;

public interface ITerraBungeeService {
    /**
     * Gets the ID of this service
     * @return The ID of this instance.
     */
    public String getId();

    /**
     * Sets the status of this service
     * @param s The new service status
     */
    void setStatus(ServiceStatus s);

    /**
     * Gets the status of this service
     * @return The status of this service
     */
    ServiceStatus getStatus();

    /**
     * Sets the websocket client of this service (Controller Only)
     * @param client The websocket client
     */
    void setClient(Object client);

    /**
     * Gets the websocket client of this service (Controller Only)
     * @return The websocket client
     */
    Object getClient();
}
