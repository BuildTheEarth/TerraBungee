package com.noahhusby.terrabungee.api;

public class InstanceNotServiceException extends Exception {
    public InstanceNotServiceException() {
        super("This TerraBungee instance is not a service. Call createService() to create one.");
    }
}
