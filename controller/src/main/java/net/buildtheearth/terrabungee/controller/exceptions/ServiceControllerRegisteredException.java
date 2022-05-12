package net.buildtheearth.terrabungee.controller.exceptions;

import net.buildtheearth.terrabungee.common.services.Service;

/**
 * An exception for registering a service controller for a service type that is already registered.
 *
 * @author Noah Husby
 */
public class ServiceControllerRegisteredException extends Exception {
    public ServiceControllerRegisteredException(Class<? extends Service> clazz) {
        super(String.format("A service controller for \"%s\" has already been registered. The existing service controller must be unregistered before registering a new one.", clazz.getName()));
    }
}
