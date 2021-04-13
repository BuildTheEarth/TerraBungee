package net.buildtheearth.api.plugin;

/**
 * @author Noah Husby
 */
public abstract class Command {
    public abstract String getName();

    public abstract String getPurpose();

    public abstract void execute(String[] args);
}
