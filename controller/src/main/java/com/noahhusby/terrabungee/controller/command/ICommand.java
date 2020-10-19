package com.noahhusby.terrabungee.controller.command;

public interface ICommand {
    String getName();
    void execute(String[] args);
}
