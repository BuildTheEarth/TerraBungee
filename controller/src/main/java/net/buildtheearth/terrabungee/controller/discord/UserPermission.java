package net.buildtheearth.terrabungee.controller.discord;

/**
 * @author Noah Husby
 */
public enum UserPermission {
    /**
     * Has ability to run all commands as well as add other roles
     */
    ADMIN,

    /**
     * Has ability to run most managerial commands
     */
    MODERATOR,

    /**
     * Has ability to run basic commands
     */
    STANDARD,

    /**
     * This discord user was too powerful so we had to nerf his permissions
     */
    NONE;
}
