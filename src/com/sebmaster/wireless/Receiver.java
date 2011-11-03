package com.sebmaster.wireless;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 * This class represents a wireless receiver. It produces power output if a
 * remote transmitter on the same channel gets a signal.
 * 
 * @author Sebmaster
 */
public class Receiver extends ChannelNode {

	public byte signData;

	private boolean isPowered;

	/**
	 * @param loc
	 *            the location of the receiver.
	 */
	public Receiver(Channel ch, Location loc) {
		super(ch, loc);
	}

	/**
	 * This method changes the appearance and power state of the receiver block.
	 * 
	 * @param power
	 *            should the receiver get powered or not?
	 */
	public void setState(boolean power) {
		Block b = location.getBlock();
		if (power && !isPowered) {
			if (isWallSign) {
				signData = b.getData();
				b.setData((byte) (6 - signData));
				b.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(), (byte) (6 - signData), true);
			} else {
				signData = b.getData();
				b.setData((byte) 0x05);
				b.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(), (byte) 0x05, true);
			}
			isPowered = true;
		} else if (!power && isPowered) {
			b.setData(signData);
			b.setTypeIdAndData((isWallSign ? Material.WALL_SIGN : Material.SIGN_POST).getId(), signData, true);
			Sign s = (Sign) location.getBlock().getState();
			s.setLine(0, "[Receiver]");
			s.setLine(1, channel.name);
			isPowered = false;
		}
	}
}
