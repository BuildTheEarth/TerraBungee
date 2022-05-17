package net.buildtheearth.terrabungee.common.instance;

/**
 * An enumeration of the type of instance.
 *
 * @author Noah Husby
 */
public enum InstanceType {
    /*
     * An instance that has is assigned an address AND is not controlled by a node.
     */
    STATIC,

    /*
     * An instance that is spun up using a TB Node.
     */
    DYNAMIC
}
