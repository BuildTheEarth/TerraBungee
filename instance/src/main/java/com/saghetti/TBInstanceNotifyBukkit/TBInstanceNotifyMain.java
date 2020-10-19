package com.saghetti.TBInstanceNotifyBukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class TBInstanceNotifyMain extends JavaPlugin  {
	final Logger logger = getLogger();
	FileConfiguration config = null;
	String instanceId = "";
	String controllerURL = "";
	String address = "";
	
	@Override
    public void onEnable() {
    	Path rootServerFolder = this.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().toPath();
    	try {
			instanceId = new String(Files.readAllBytes(rootServerFolder.resolve("tb_info/id.txt")));
			controllerURL = new String(Files.readAllBytes(rootServerFolder.resolve("tb_info/controllerurl.txt")));
			address = new String(Files.readAllBytes(rootServerFolder.resolve("tb_info/address.txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	logger.info("Pushing status to " + controllerURL + "push/instance/online");
    	try {
			HttpURLConnection con = (HttpURLConnection) new URL(controllerURL + "push/instance/online").openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json; utf-8");
			con.setRequestProperty("Accept", "application/json");
			String jsonInputString = "{\"instance_id\": \"" + instanceId + "\", \"address\": \"" + address + "\"}";
			con.setDoOutput(true);
			try(OutputStream os = con.getOutputStream()) {
			    byte[] input = jsonInputString.getBytes("utf-8");
			    os.write(input, 0, input.length);
			}
			try(BufferedReader br = new BufferedReader(
				new InputStreamReader(con.getInputStream(), "utf-8"))) {
				StringBuilder response = new StringBuilder();
				String responseLine = null;
				while ((responseLine = br.readLine()) != null) {
					response.append(responseLine.trim());
				}
				//System.out.println(response.toString());
			}
			con.disconnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	logger.info("Now online!");
    }
    
    @Override
    public void onDisable() {
    	logger.info("Pushing status to " + controllerURL + "push/instance/offline");
    	try {
			HttpURLConnection con = (HttpURLConnection) new URL(controllerURL + "push/instance/offline").openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json; utf-8");
			con.setRequestProperty("Accept", "application/json");
			String jsonInputString = "{\"instance_id\": \"" + instanceId + "\"}";
			con.setDoOutput(true);
			try(OutputStream os = con.getOutputStream()) {
			    byte[] input = jsonInputString.getBytes("utf-8");
			    os.write(input, 0, input.length);
			}
			try(BufferedReader br = new BufferedReader(
				new InputStreamReader(con.getInputStream(), "utf-8"))) {
				StringBuilder response = new StringBuilder();
				String responseLine = null;
				while ((responseLine = br.readLine()) != null) {
					response.append(responseLine.trim());
				}
				//System.out.println(response.toString());
			}
			con.disconnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	logger.info("Now offline!");
    }
}
