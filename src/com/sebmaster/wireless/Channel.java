package com.sebmaster.wireless;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Location;

/**
 * Represents a channel with unlimited amounts of transmitters and receivers.
 * 
 * @author Sebmaster
 */
public class Channel {
	
	public String name;
	private ArrayList<Transmitter> transmitters = new ArrayList<Transmitter>();
	private ArrayList<Receiver> receivers = new ArrayList<Receiver>();
	
	Channel(String name) {
		this.name = name;
	}
	
	public Transmitter addTransmitter(Location loc) {
		Transmitter t = new Transmitter(this, loc);
		if (!transmitters.contains(t)) {
			Logger.getLogger("Wireless").info("Created new transmitter.");
			transmitters.add(t);
		} else {
			t = transmitters.get(transmitters.indexOf(t));
		}
		return t;
	}
	
	public void removeTransmitter(Location loc) {
		for (Transmitter t : transmitters) {
			if (t.getLocation().equals(loc)) {
				transmitters.remove(t);
				Logger.getLogger("Wireless").info("Removed transmitter!");
				return;
			}
		}
	}
	
	public ArrayList<Transmitter> getTransmitters() {
		return transmitters;
	}
	
	public Receiver addReceiver(Location loc) {
		Receiver r = new Receiver(this, loc);
		if (!receivers.contains(r)) {
			Logger.getLogger("Wireless").info("Created new receiver.");
			receivers.add(r);
		} else {
			r = receivers.get(receivers.indexOf(r));
		}
		return r;
	}
	
	public void removeReceiver(Location loc) {
		for (Receiver r : receivers) {
			if (r.getLocation().equals(loc)) {
				receivers.remove(r);
				Logger.getLogger("Wireless").info("Removed receiver!");
				return;
			}
		}
	}
	
	public ArrayList<Receiver> getReceivers() {
		return receivers;
	}
	
	public void update() {
		boolean powered = false;
		for (Transmitter t : transmitters) {
			if (t.isPowered()) {
				powered = true;
				break;
			}
 		}

		for (Receiver r : receivers) {
			Location l = r.getLocation();
			l.getWorld().loadChunk(l.getBlockX(), l.getBlockZ());
			r.setState(powered);
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || o.getClass() != this.getClass()) return false;
		return this.name.equals(((Channel)o).name);
	}

	/**
     * Checks if the channel contains no ChannelNodes.
     * 
     * @return true, if no transmitters and no receivers exist in this channel
     */
    public boolean isEmpty() {
    	return transmitters.isEmpty() && receivers.isEmpty();
    }
}