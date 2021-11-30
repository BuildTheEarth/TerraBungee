package net.buildtheearth.terrabungee.controller.security;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.Getter;
import net.buildtheearth.terrabungee.controller.TerraBungeeController;
import net.buildtheearth.terrabungee.controller.modules.Module;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author Noah Husby
 */
public class SecurityManager extends Module {
    @Getter
    private static final SecurityManager instance = new SecurityManager();

    private List<String> whitelist = Lists.newArrayList();
    private File whitelistFile;

    private SecurityManager() {
        super("Security");
    }

    public void loadWhitelist() {
        List<String> tempWhitelist = Lists.newArrayList();
        try {
            String strLine;
            BufferedReader br = new BufferedReader(new FileReader(whitelistFile));
            while ((strLine = br.readLine()) != null) {
                tempWhitelist.add(strLine);
            }
            this.whitelist = ImmutableList.copyOf(tempWhitelist);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean verifyConnection(InetSocketAddress address) {
        if (whitelist.contains(address.getHostString())) {
            return true;
        }
        TerraBungeeController.getInstance().getLogger().warning("An unknown host attempted to establish a connection with the controller: " + address.getHostString());
        return false;
    }

    @Override
    public void onEnable() {
        whitelistFile = new File(TerraBungeeController.getInstance().getFolder(), "whitelist.tb");
        if (!whitelistFile.exists()) {
            try {
                whitelistFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        loadWhitelist();
    }

    @Override
    public void onDisable() {

    }
}
