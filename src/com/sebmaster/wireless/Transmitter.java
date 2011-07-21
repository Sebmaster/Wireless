package com.sebmaster.wireless;

import org.bukkit.Location;

/**
 * @author Sebmaster
 */
public class Transmitter extends ChannelNode {
	
	/**
	 * @param loc the location of the transmitter
	 */
	public Transmitter(Channel ch, Location loc) {
		super(ch, loc);
	}

	/**
     * Checks if the transmitter block has any redstone power.
     * 
     * @return true, if power is available
     */
    public boolean isPowered() {
    	return location.getBlock().isBlockPowered() || location.getBlock().isBlockIndirectlyPowered();
    }
}
