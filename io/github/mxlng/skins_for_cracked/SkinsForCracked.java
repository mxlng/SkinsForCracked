package io.github.mxlng.skins_for_cracked;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.util.UUIDTypeAdapter;

public class SkinsForCracked extends JavaPlugin implements Listener{
	
	@Override
	public void onEnable() {
		new MetricsLite(this, 6739);
		Bukkit.getPluginManager().registerEvents(this, this);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException  {
		Player player = e.getPlayer();
		Class<?> strClass = (Class<?>) Class.forName("org.bukkit.craftbukkit."+getServerVersion()+".entity.CraftPlayer");
		UUID uuid = UUIDFetcher.getUUID(e.getPlayer().getName());
		setSkin(strClass.cast(player).getClass().getMethod("getProfile").invoke(strClass.cast(player)), player.getName(), uuid);
	}

	
	public static String getServerVersion() {
		String version;
		version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		return version;
	}
	
	public static boolean setSkin(Object gameProfile, String name, UUID uuid) {
		try {
			HttpsURLConnection connection = (HttpsURLConnection) new URL(String.format("https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false", UUIDTypeAdapter.fromUUID(uuid))).openConnection();
			if(connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
				String reply = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
				String skin = reply.split("\"value\":\"")[1].split("\"")[0];
	            String signature = reply.split("\"signature\":\"")[1].split("\"")[0];
	            GameProfile profile = (GameProfile) gameProfile;
	            profile.getProperties().put("textures", new Property("textures", skin, signature));
			}
			return true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

}
