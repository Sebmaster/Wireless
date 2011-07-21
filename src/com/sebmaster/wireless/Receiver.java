package com.sebmaster.wireless;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;

/**
 * This class represents a wireless receiver.
 * It produces power output if a remote transmitter on the same channel gets a signal.
 * 
 * @author Sebmaster
 */
public class Receiver extends ChannelNode {
	
	public byte signData;
	
	private boolean isPowered;
	
	/**
	 * @param loc the location of the receiver.
	 */
	public Receiver(Channel ch, Location loc) {
		super(ch, loc);
	}

	/**
     * This method changes the appearance and power state of the receiver block.
     * 
     * @param power should the receiver get powered or not?
     */
    public void setState(boolean power) {
    	if (power && !isPowered) {
    		signData = location.getBlock().getData();
			location.getBlock().setData((byte) (isWallSign ? (6 - signData) : 0x05));
    		location.getBlock().setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(), (byte) (isWallSign ? (6 - signData) : 0x05), false);
    		isPowered = true;
    	} else if (!power && isPowered) {
    		location.getBlock().setData(signData);
    		location.getBlock().setTypeIdAndData((isWallSign ? Material.WALL_SIGN : Material.SIGN_POST).getId(), signData, false);
    		Sign s = (Sign)location.getBlock().getState();
    		s.setLine(0, "[Receiver]");
    		s.setLine(1, channel.name);
    		isPowered = false;
    	}
    }
}
