package net.buildtheearth.terrabungee.controller.util;

import net.buildtheearth.terrabungee.controller.TerraBungeeController;
import net.buildtheearth.terrabungee.controller.config.ConfigHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Temporary SQL Class until I (MineFact) understand how the Database handling works here
 *
 * @author MineFact
 */

public class MySQL {

    public static String DATABASE = "MineFactServernetzwerk";

    public static Connection con;
    public static String host;
    public static String port;
    public static String username;
    public static String passwort;
    public static String database;


    public static void start() {
        connect();
        TerraBungeeController.getInstance().getGeneralThreads().scheduleAtFixedRate(() -> {

            disconnect();

            if (con != null) {
                return;
            }

            connect();

        }, 6, 6, TimeUnit.HOURS);
    }

    public static void connect() {
        host = ConfigHandler.sqlHost;
        port = "" + ConfigHandler.sqlPort;
        database = DATABASE;
        username = ConfigHandler.sqlUser;
        passwort = ConfigHandler.sqlPassword;

        if (host.equals("<host>")) {
            System.out.println("§cPlease configure the MySQL Server.");
            return;
        }

        System.out.println("Connecting...");

        if (!isConnected()) {
            try {
                con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, passwort);
                System.out.println("§a[MySQL] Connection established.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void disconnect() {
        if (isConnected()) {
            try {
                con.close();
                con = null;
                System.out.println("§a[MySQL] Connection closed.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isConnected() {
        return (con != null);

    }

    public static Connection getConnection() {
        return con;
    }


    public static boolean Accountexists(UUID uuid, String database) {
        boolean boo = false;

        if (MySQL.getConnection() == null) {
            return false;
        }

        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT UUID FROM " + database + " WHERE UUID = ?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            boo = rs.next();

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return boo;
    }

    public static boolean Accountexists(String name, String database) {
        boolean boo = false;

        if (MySQL.getConnection() == null) {
            return false;
        }

        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT Spielername FROM " + database + " WHERE Spielername = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            boo = rs.next();

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return boo;
    }

    public static boolean Accountexists(String keyName, String keyValue, String database) {
        boolean boo = false;

        if (MySQL.getConnection() == null) {
            return false;
        }

        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM " + database + " WHERE " + keyName + " = ?");
            ps.setString(1, keyValue);
            ResultSet rs = ps.executeQuery();
            boo = rs.next();

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return boo;
    }

    public static boolean Accountexists(String key1Name, String key1Value, String key2Name, String key2Value, String database) {
        boolean boo = false;

        if (MySQL.getConnection() == null) {
            return false;
        }

        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM " + database + " WHERE " + key1Name + " = ? AND " + key2Name + " = ?");
            ps.setString(1, key1Value);
            ps.setString(2, key2Value);
            ResultSet rs = ps.executeQuery();
            boo = rs.next();

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return boo;
    }

    public static boolean getBoolean(String sql, String arg) {
        boolean boo = false;

        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                boo = rs.getBoolean(arg);
            }


            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return boo;
    }

    public static boolean getBoolean(UUID uuid, String arg, String database) {
        boolean boo = false;

        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT " + arg + " FROM " + database + " WHERE UUID = ?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                boo = rs.getBoolean(arg);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return boo;
    }

    public static boolean getBoolean(String keyName, String keyValue, String arg, String database) {
        boolean boo = false;
        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM " + database + " WHERE " + keyName + " = ?");
            ps.setString(1, keyValue);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                boo = rs.getBoolean(arg);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return boo;
    }

    public static boolean getBoolean(String key1Name, String key1Value, String key2Name, String key2Value, String arg, String database) {
        boolean boo = false;
        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM " + database + " WHERE " + key1Name + " = ? AND " + key2Name + " = ?");
            ps.setString(1, key1Value);
            ps.setString(2, key2Value);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                boo = rs.getBoolean(arg);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return boo;
    }

    public static int getInteger(String sql, String arg) {
        int integer = -1;
        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                integer = rs.getInt(arg);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return integer;
    }

    public static Integer getInteger(UUID uuid, String arg, String database) {
        int integer = -1;
        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM " + database + " WHERE UUID = ?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                integer = rs.getInt(arg);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return integer;
    }

    public static Integer getInteger(String name, String arg, String database) {
        int integer = -1;
        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM " + database + " WHERE UUID = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                integer = rs.getInt(arg);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return integer;
    }

    public static Integer getInteger(String keyName, String keyValue, String arg, String database) {
        int integer = -1;
        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM " + database + " WHERE " + keyName + " = ?");
            ps.setString(1, keyValue);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                integer = rs.getInt(arg);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return integer;
    }

    public static Integer getInteger(String key1Name, String key1Value, String key2Name, String key2Value, String arg, String database) {
        int integer = -1;
        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM " + database + " WHERE " + key1Name + " = ? AND " + key2Name + " = ?");
            ps.setString(1, key1Value);
            ps.setString(2, key2Value);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                integer = rs.getInt(arg);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return integer;
    }

    public static Integer getMaxInteger(String arg, String database) {
        int integer = 0;
        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT MAX(" + arg + ") FROM " + database);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                integer = rs.getInt(1);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return integer;
    }

    public static void setInteger(UUID uuid, String arg, String database, int value) {
        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE " + database + " SET " + arg + " = ? WHERE UUID = ?");
            ps.setInt(1, value);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setInteger(String keyName, String keyValue, String arg, String database, int value) {
        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE " + database + " SET " + arg + " = ? WHERE " + keyName + " = ?");
            ps.setInt(1, value);
            ps.setString(2, keyValue);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setInteger(String name, String arg, String database, int value) {
        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE " + database + " SET " + arg + " = ? WHERE Spielername = ?");
            ps.setInt(1, value);
            ps.setString(2, name);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getString(String sql, String arg) {
        String s = "";
        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                s = rs.getString(arg);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return s;
    }

    public static String getString(UUID uuid, String arg, String database) {
        String s = "";
        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM " + database + " WHERE UUID = ?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                s = rs.getString(arg);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return s;
    }

    public static String getString(String name, String arg, String database) {
        String s = "";
        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM " + database + " WHERE Spielername = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                s = rs.getString(arg);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return s;
    }

    public static String getString(String keyArg, String keyValue, String arg, String database) {
        String s = "";
        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM " + database + " WHERE " + keyArg + " = ?");
            ps.setString(1, keyValue);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                s = rs.getString(arg);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return s;
    }

    public static String getString(String key1Arg, String key1Value, String key2Arg, String key2Value, String arg, String database) {
        String s = "";
        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM " + database + " WHERE " + key1Arg + " = ? AND " + key2Arg + " = ?");
            ps.setString(1, key1Value);
            ps.setString(2, key2Value);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                s = rs.getString(arg);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return s;
    }

    public static String getStringList(String sql, String arg) {
        String s = "";
        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                if (s.equals("")) {
                    s = rs.getString(arg);
                } else {
                    s += "|" + rs.getString(arg);
                }
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return s;
    }

    public static HashMap<String, String> getMap(String sql, String arg1, String arg2) {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                map.put(rs.getString(arg1), rs.getString(arg2));
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return map;
    }

    public static HashMap<String, Integer> getMapStringInteger(String sql, String arg1, String arg2) {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>();

        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                map.put(rs.getString(arg1), rs.getInt(arg2));
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return map;
    }

    public static void setString(UUID uuid, String arg, String database, String value) {
        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE " + database + " SET " + arg + " = ? WHERE UUID = ?");
            ps.setString(1, value);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setString(String name, String arg, String database, String value) {
        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE " + database + " SET " + arg + " = ? WHERE Spielername = ?");
            ps.setString(1, value);
            ps.setString(2, name);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setString(String keyName, String keyValue, String arg, String database, String value) {
        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE " + database + " SET " + arg + " = ? WHERE " + keyName + " = ?");
            ps.setString(1, value);
            ps.setString(2, keyValue);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getPermission(UUID uuid) {
        String s = "";
        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM permissions_inheritance WHERE child = ?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                s = rs.getString("parent");
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return s;
    }

    public static UUID getUUID(String playername) {
        UUID playerUUID = null;

        String sql = "SELECT UUID FROM `Spieler` WHERE Spielername = '" + playername + "' OR andereSpielernamen LIKE '%" + playername + ",%' OR andereSpielernamen LIKE '%," + playername + "' OR andereSpielernamen = '" + playername + "';";

        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                playerUUID = UUID.fromString(rs.getString("UUID"));
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return playerUUID;
    }

    public static void deleteAccount(UUID uuid, String database) {
        if (Accountexists(uuid, database)) {
            try {
                PreparedStatement ps = MySQL.getConnection().prepareStatement("DELETE * FROM " + database + " WHERE UUID = ?");
                ps.setString(1, uuid.toString());
                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("[MySQL] Der Spieler mit der UUID: " + uuid + " befindet sich nicht in der Datenbank.");
        }
    }

    public static void deleteEntry(String keyName, String keyValue, String database) {
        if (Accountexists(keyName, keyValue, database)) {
            try {
                PreparedStatement ps = MySQL.getConnection().prepareStatement("DELETE FROM " + database + " WHERE " + keyName + " = ?");
                ps.setString(1, keyValue);
                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
