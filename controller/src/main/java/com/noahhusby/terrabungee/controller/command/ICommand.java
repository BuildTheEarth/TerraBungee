package com.noahhusby.terrabungee.controller.command;

public interface ICommand {
    String getName();
    String getPurpose();
    void execute(String[] args);
}
