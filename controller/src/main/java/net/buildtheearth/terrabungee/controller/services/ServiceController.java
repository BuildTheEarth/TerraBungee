package net.buildtheearth.terrabungee.controller.services;

import net.buildtheearth.terrabungee.common.services.Service;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * An issuer of service
 *
 * @author Noah Husby
 */
public abstract class ServiceController<V extends Service> {
    protected Collection<V> getServices() {
        return null;
    }

    /**
     * Event fired when a service is connected to the controller.
     *
     * @param service The service that is connected.
     */
    public abstract void onServiceConnect(V service);

    /**
     * Event fired when a service is being initialized.
     *
     * @param service The service that is initialized.
     */
    public abstract void onServiceInit(V service);
}