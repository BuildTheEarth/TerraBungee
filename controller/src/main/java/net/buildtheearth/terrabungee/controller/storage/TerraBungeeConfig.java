package net.buildtheearth.terrabungee.controller.storage;

import com.noahhusby.lib.application.config.Config;
import com.noahhusby.lib.application.config.Config.Type;

import java.net.InetSocketAddress;

/**
 * @author Noah Husby
 */
@SuppressWarnings("CanBeFinal")
@Config(name = "terrabungee", type = Type.JSON)
public class TerraBungeeConfig {

    public static String host = "127.0.0.1";
    public static int port = 7000;

    public static InetSocketAddress getSocketAddress() {
        return new InetSocketAddress(host, port);
    }

    public static DatabaseOptions mongodb = new DatabaseOptions();

    public static class DatabaseOptions {

        public MongoServer[] servers = {
                new MongoServer()
        };

        public String user = "";

        public String password = "";

        public String database = "";
    }

    public static class MongoServer {
        public String host = "127.0.0.1";
        public int port = 27017;
    }
}
