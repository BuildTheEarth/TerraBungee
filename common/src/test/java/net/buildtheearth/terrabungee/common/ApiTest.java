package net.buildtheearth.terrabungee.common;

import net.buildtheearth.terrabungee.common.discord.BotApi;
import net.buildtheearth.terrabungee.common.discord.structures.Builder;
import net.buildtheearth.terrabungee.common.discord.structures.Role;
import net.buildtheearth.terrabungee.common.discord.structures.User;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ApiTest {

    private final BotApi botApi = new BotApi("http://localhost:8090/", "&T#uZ(tt@5+rpR;rRd");

    @Test
    void apiCheckBld() throws IOException {
        Builder bld = botApi.getBuilder("7123982103452045871");
        assertTrue(bld.isBuilder());
    }

    @Test
    void apiCheckRl() throws IOException {
        User us = botApi.getUser("712398210345205871");
        System.out.println(us.getRoles().size());

        assertTrue(us.hasRole(new Role("Manager", "789224355556360202")));
    }

}
