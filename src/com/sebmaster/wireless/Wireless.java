package com.sebmaster.wireless;

import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

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

		logger.info("Wireless v1.2.1 loaded successfully.");
	}

	private void load() {
		FileConfiguration config = this.getConfig();

		Server s = Bukkit.getServer();
		for (String idx : config.getKeys(false)) {
			Channel c = new Channel(config.getString(idx + ".name"), this);
			channels.add(c);

			Set<String> keys = config.getConfigurationSection(idx + ".transmitters").getKeys(false);
			if (keys != null) {
				for (String i : keys) {
					Location loc = new Location(s.getWorld(config.getString(idx + ".transmitters." + i + ".world")),
					        config.getInt(idx + ".transmitters." + i + ".x", 0),
					        config.getInt(idx + ".transmitters." + i + ".y", 0),
					        config.getInt(idx + ".transmitters." + i + ".z", 0));
					c.addTransmitter(loc, config.getBoolean(idx + ".transmitters." + i + ".isWallSign", false));
				}
			}

			keys = config.getConfigurationSection(idx + ".receivers").getKeys(false);
			if (keys != null) {
				for (String i : keys) {
					Location loc = new Location(s.getWorld(config.getString(idx + ".receivers." + i + ".world")),
					        config.getInt(idx + ".receivers." + i + ".x", 0),
					        config.getInt(idx + ".receivers." + i + ".y", 0),
					        config.getInt(idx + ".receivers." + i + ".z", 0));
					c.addReceiver(loc, config.getBoolean(idx + ".receivers." + i + ".isWallSign", false), (byte) config.getInt(idx + ".receivers." + i + ".signData", 0));
				}
			}
		}
	}

	private void save() {
		saveConfig();
	}

	public Channel getChannel(String ch) {
		Channel c = new Channel(ch, this);
		int idx = channels.indexOf(c);
		if (idx == -1) {
			Logger.getLogger("Wireless").info("Created new Wireless Channel \"" + ch + "\"");
			channels.add(c);
			this.getConfig().set((channels.size() - 1) + ".name", ch);
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
	 * @param c
	 *            the channel to remove
	 */
	public void removeChannel(Channel c) {
		this.getConfig().set(Integer.toString(channels.indexOf(c)), null);
		channels.remove(c);
	}

	/**
	 * 
	 * @param name
	 * @param idx
	 * @param t
	 */
	public void addTransmitter(Channel c, int idx, Transmitter t) {
		int chIdx = channels.indexOf(c);
		FileConfiguration config = this.getConfig();
		config.set(chIdx + ".transmitters." + idx + ".world", t.location.getWorld().getName());
		config.set(chIdx + ".transmitters." + idx + ".x", t.location.getBlockX());
		config.set(chIdx + ".transmitters." + idx + ".y", t.location.getBlockY());
		config.set(chIdx + ".transmitters." + idx + ".z", t.location.getBlockZ());
		config.set(chIdx + ".transmitters." + idx + ".isWallSign", t.isWallSign);
	}

	/**
	 * 
	 * @param channel
	 * @param size
	 */
	public void removeTransmitter(Channel c, int idx) {
		int chIdx = channels.indexOf(c);
		FileConfiguration config = this.getConfig();
		config.set(chIdx + ".transmitters." + idx, null);
	}

	/**
     * 
     * @param channel
     * @param size
     * @param r
     */
    public void addReceiver(Channel c, int idx, Receiver r) {
    	int chIdx = channels.indexOf(c);
		FileConfiguration config = this.getConfig();
		config.set(chIdx + ".receivers." + idx + ".world", r.location.getWorld().getName());
		config.set(chIdx + ".receivers." + idx + ".x", r.location.getBlockX());
		config.set(chIdx + ".receivers." + idx + ".y", r.location.getBlockY());
		config.set(chIdx + ".receivers." + idx + ".z", r.location.getBlockZ());
		config.set(chIdx + ".receivers." + idx + ".isWallSign", r.isWallSign);
		config.set(chIdx + ".receivers." + idx + ".signData", r.signData);
    }

	/**
	 * 
	 * @param channel
	 * @param size
	 */
	public void removeReceiver(Channel c, int idx) {
		int chIdx = channels.indexOf(c);
		FileConfiguration config = this.getConfig();
		config.set(chIdx + ".receivers." + idx, null);
	}
}