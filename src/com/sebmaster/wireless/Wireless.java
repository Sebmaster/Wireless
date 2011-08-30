package com.sebmaster.wireless;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

/**
 * @author Sebastian Mayr
 */
public class Wireless extends JavaPlugin {

	private ArrayList<Channel> channels = new ArrayList<Channel>();
	private Logger logger = Logger.getLogger("Wireless");

	@Override
	public void onDisable() {
		this.save();
	}

	@Override
	public void onEnable() {
		load();
		Listener l = new Listener(this);
		
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Type.WORLD_UNLOAD, l.wl, Priority.Normal, this);
		pm.registerEvent(Type.BLOCK_BREAK, l.bl, Priority.Normal, this);
		pm.registerEvent(Type.SIGN_CHANGE, l.bl, Priority.High, this);
		pm.registerEvent(Type.REDSTONE_CHANGE, l.bl, Priority.High, this);
		
		logger.info("Wireless v0.1 loaded successfully.");
	}

	private void load() {
		Configuration config = this.getConfiguration();
		config.load();

		Server s = Bukkit.getServer();
		for (String idx : config.getKeys()) {
			Channel c = new Channel(config.getString(idx + ".name"));
			channels.add(c);
			
			List<String> keys = config.getKeys(idx + ".transmitters");
			if (keys != null) {
				for (String i : keys) {
					Location loc = new Location(s.getWorld(config.getString(idx + ".transmitters." + i + ".world")),
							config.getInt(idx + ".transmitters." + i + ".x", 0),
							config.getInt(idx + ".transmitters." + i + ".y", 0), 
							config.getInt(idx + ".transmitters." + i + ".z", 0));
					Transmitter t = c.addTransmitter(loc);
					t.isWallSign = config.getBoolean(idx + ".transmitters." + i + ".isWallSign", false);
				}
			}

			keys = config.getKeys(idx + ".receivers");
			if (keys != null) {
				for (String i : keys) {
					Location loc = new Location(s.getWorld(config.getString(idx + ".receivers." + i + ".world")),
							config.getInt(idx + ".receivers." + i + ".x", 0),
							config.getInt(idx + ".receivers." + i + ".y", 0),
							config.getInt(idx + ".receivers." + i + ".z", 0));
					Receiver r = c.addReceiver(loc);
					r.isWallSign = config.getBoolean(idx + ".receivers." + i + ".isWallSign", false);
					r.signData = (byte)config.getInt(idx + ".receivers." + i + ".signData", 0);
				}
			}
		}
	}

	private void save() {
		Configuration config = this.getConfiguration();
		for (int i = 0; i < this.channels.size(); ++i) {
			Channel c = this.channels.get(i);
			config.setProperty(i + ".name", c.name);

			ArrayList<Transmitter> trans = c.getTransmitters();
			for (int j = 0; j < trans.size(); ++j) {
				Transmitter t = trans.get(j);
				Location loc = t.getLocation();
				config.setProperty(i + ".transmitters." + j + ".world", loc.getWorld().getName());
				config.setProperty(i + ".transmitters." + j + ".x", loc.getBlockX());
				config.setProperty(i + ".transmitters." + j + ".y", loc.getBlockY());
				config.setProperty(i + ".transmitters." + j + ".z", loc.getBlockZ());
				config.setProperty(i + ".transmitters." + j + ".isWallSign", t.isWallSign);
			}

			ArrayList<Receiver> rec = c.getReceivers();
			for (int j = 0; j < rec.size(); ++j) {
				Receiver r = rec.get(j);
				Location loc = r.getLocation();
				config.setProperty(i + ".receivers." + j + ".world", loc.getWorld().getName());
				config.setProperty(i + ".receivers." + j + ".x", loc.getBlockX());
				config.setProperty(i + ".receivers." + j + ".y", loc.getBlockY());
				config.setProperty(i + ".receivers." + j + ".z", loc.getBlockZ());
				config.setProperty(i + ".receivers." + j + ".isWallSign", r.isWallSign);
				config.setProperty(i + ".receivers." + j + ".signData", r.signData);
			}
		}
		config.save();
	}

	public Channel getChannel(String ch) {
		Channel c = new Channel(ch);
		int idx = channels.indexOf(c);
		if (idx == -1) {
			Logger.getLogger("Wireless").info("Created new Wireless Channel \""+ch+"\"");
			channels.add(c);
			return c;
		} else {
			return channels.get(idx);
		}
	}

	public ArrayList<Channel> getChannels() {
		return channels;
	}

	/**
     * 
     * @param c the channel to remove
     */
    public void removeChannel(Channel c) {
	    channels.remove(c);
    }
}