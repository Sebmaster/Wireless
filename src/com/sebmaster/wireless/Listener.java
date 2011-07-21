package com.sebmaster.wireless;

import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Sebmaster
 */
public class Listener {
	
	Wireless plugin;
	
	public class BlockListener extends org.bukkit.event.block.BlockListener {
		
		@Override
		public void onBlockBreak(BlockBreakEvent evt) {
			Block b = evt.getBlock();
			if (b.getState() instanceof Sign) {
				Sign sign = (Sign)b.getState();
				if (sign.getLine(0).equals("[Transmitter]")) {
					Channel c = plugin.getChannel(sign.getLine(1));
					c.removeTransmitter(b.getLocation());
					c.update();
					
					if (c.isEmpty()) {
						plugin.removeChannel(c);
					}
				} else if (sign.getLine(0).equals("[Receiver]")) {
					Channel c = plugin.getChannel(sign.getLine(1));
					c.removeReceiver(b.getLocation());
					c.update();
					
					if (c.isEmpty()) {
						plugin.removeChannel(c);
					}
				}
			} else if (b.getType() == Material.REDSTONE_TORCH_ON) {
				for (Channel c : plugin.getChannels()) {
					for (Receiver r : c.getReceivers()) {
						if (r.getLocation().equals(b.getLocation())) {
							evt.setCancelled(true);
							b.setType(Material.AIR);
							b.getLocation().getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.SIGN, 1));
							c.removeReceiver(b.getLocation());
							
							if (c.isEmpty()) {
								plugin.removeChannel(c);
							}
							return;
						}
					}
				}
			}
		}

		@Override
		public void onBlockRedstoneChange(BlockRedstoneEvent evt) {
			ArrayList<Channel> changed = new ArrayList<Channel>();
			BlockState b = evt.getBlock().getState();
			if (b instanceof Sign) {
				Sign sign = (Sign)b;
				
				if (sign.getLine(0).equals("[Transmitter]")) {
					Channel c = plugin.getChannel(sign.getLine(1));
					if (!changed.contains(c)) {
						changed.add(c);
					}
				}
			}
			for (Channel c : changed) {
				c.update();
			}
		}
		
		/**
		 * If a sign changes, need to check if it's a sign we want to use for Wireless.
		 * 
		 * @param evt the signchange event
		 */
		@Override
		public void onSignChange(SignChangeEvent evt) {
			String type = setTransmitterOrReceiver(evt.getLine(0));
			Channel channel = null;
			
			if (type == null) {
				return;
			} else {
				evt.setLine(0, type);
				channel = plugin.getChannel(evt.getLine(1));
			}
			
			ChannelNode c;
			if (type.equals("[Transmitter]")) {
				c = channel.addTransmitter(evt.getBlock().getLocation());
			} else {
				Receiver r = channel.addReceiver(evt.getBlock().getLocation());
				r.signData = evt.getBlock().getData();
				c = r;
			}
			c.isWallSign = evt.getBlock().getType() == Material.WALL_SIGN ? true : false;
			
			channel.update();
		}
	}
	
	public class WorldListener extends org.bukkit.event.world.WorldListener {
		
		@Override
		public void onChunkUnload(ChunkUnloadEvent evt) {
			if (evt.isCancelled()) return;
			
			Chunk chunk = evt.getChunk();
			for (Channel c : plugin.getChannels()) {
				for (Receiver r : c.getReceivers()) {
					Location l = r.getLocation();
					World w = l.getWorld();
					if (w.getChunkAt(l).equals(chunk)) { // If a receiver lies in that chunk, we need to check
						for (Transmitter t : c.getTransmitters()) { // if a transmitter is loaded
							Location locT = t.getLocation();
							if (locT.getWorld().isChunkLoaded(locT.getBlockX(), locT.getBlockZ())) {
								evt.setCancelled(true); // If there's any transmitter loaded, the receiver might get used shortly
								return;
							}
						}
					}
				}
			}
		}
	}
	
	BlockListener bl = new BlockListener();
	
	WorldListener wl = new WorldListener();
	
	public Listener(Wireless plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * Checks if the given sign is a receiver or transmitter.
	 * 
	 * @param line the line of the sign
	 * @return true, if the sign is a receiver or transmitter
	 */
	public static String setTransmitterOrReceiver(String line) {
		boolean isTransmitter = line.equalsIgnoreCase("transmitter");
		boolean isReceiver = line.equalsIgnoreCase("receiver");
		
		return isTransmitter ? "[Transmitter]" : isReceiver ? "[Receiver]" : null;
	}
}