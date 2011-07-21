package com.sebmaster.wireless;

import org.bukkit.Location;

/**
 * Represents a part of a channel. This is either a transmitter or a receiver.
 * 
 * @author Sebastian Mayr
 */
public class ChannelNode {
	
	/**
	 * The channel which the receiver uses.
	 */
	protected Channel channel;
	
	/*
	 * The location of the receiver.
	 */
	protected Location location;
	
	/**
	 * Specifies if this sign is mounted onto a wall
	 */
	public boolean isWallSign;
	
	/**
	 * @param loc the location of the receiver.
	 */
	public ChannelNode(Channel ch, Location loc) {
		this.channel = ch;
		this.location = loc;
	}

	/**
	 * @return the location of the receiver.
	 */
	public Location getLocation() {
		return location;
	}
    
    /**
     * Checks if the given ChannelNode is on the same position as the current one.
     * 
     * @param o the other ChannelNode
     * @return true, if the locations are equal
     */
    @Override
    public boolean equals(Object o) {
    	if (o == null || !this.getClass().isAssignableFrom(o.getClass())) return false;
    	ChannelNode c = (ChannelNode)o;
    	return c.channel.equals(c.channel) && this.location.equals(c.location);
    }
}
