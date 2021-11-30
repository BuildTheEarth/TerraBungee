package net.buildtheearth.terrabungee.controller.config;

import com.noahhusby.lib.application.config.Config;
import com.noahhusby.lib.application.config.Config.Comment;
import com.noahhusby.lib.application.config.Config.Name;
import com.noahhusby.lib.application.config.Config.Type;
import com.noahhusby.lib.data.sql.Credentials;

import java.net.InetSocketAddress;

/**
 * @author Noah Husby
 */
@SuppressWarnings("CanBeFinal")
@Config(name = "terrabungee", type = Type.HOCON)
public class TerraBungeeConfig {

    @Comment({
            "General settings for the TerraBungee Controller"
    })
    public static GeneralOptions general = new GeneralOptions();

    public static class GeneralOptions {
        @Comment({
                "The IP address that the controller should run on. [default: 127.0.0.1]"
        })
        @Name("Host")
        public String host = "127.0.0.1";

        @Comment({
                "The port that the controller should run on. [range: 0 ~ 65535, default: 7000]"
        })
        @Name("Port")
        public int port = 7000;

        public InetSocketAddress getSocketAddress() {
            return new InetSocketAddress(host, port);
        }
    }

    @Comment({
            "Settings for the MySQL Database"
    })
    public static DatabaseOptions database = new DatabaseOptions();

    public static class DatabaseOptions {
        @Comment({
                "The host IP for the database."
        })
        public String sqlHost = "127.0.0.1";

        @Comment({
                "The port for the database."
        })
        public int sqlPort = 3306;

        @Comment({
                "The username for the database."
        })
        public String sqlUser = "";

        @Comment({
                "The password for the database."
        })
        public String sqlPassword = "";

        @Comment({
                "The name of the database."
        })
        public String sqlDb = "";

        @Comment({
                "Should data be stored locally as well [default: false]"
        })
        @Name("Local_Storage")
        public boolean localStorage = false;

        public Credentials toCredentials() {
            return new Credentials(sqlHost, sqlPort, sqlUser, sqlPassword, sqlDb);
        }
    }
}
