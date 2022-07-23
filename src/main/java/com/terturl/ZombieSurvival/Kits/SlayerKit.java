package com.terturl.ZombieSurvival.Kits;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.terturl.ZombieSurvival.ZombieSurvival;
import com.terturl.mcessentials.minigameapi.kits.Kit;
import com.terturl.mcessentials.minigameapi.kits.KitCooldown;
import com.terturl.mcessentials.minigameapi.listeners.MinigamePlayerDamageEvent;

public class SlayerKit extends Kit {

	public SlayerKit(ItemStack item) {
		super("Slayer", item);
		getAdditionalPlayerInformation().put("Slayer-Undamageable", new ArrayList<UUID>());
		
		addAction(PlayerMoveEvent.class, "Slayer-AllowFlight", (event) -> {
			Player p = event.getPlayer();
			if(!p.getLocation().subtract(0, 1, 0).getBlock().getType().equals(Material.AIR)) return;
			p.setAllowFlight(true);
		}, null);
		
		addAction(PlayerToggleFlightEvent.class, "Slayer-DoubleJump", (event) -> {
			Player p = event.getPlayer();
			event.setCancelled(true);
			p.setAllowFlight(false);
			p.setVelocity(p.getVelocity().add(new Vector(0, 0.3, 0)));
		}, null);
		
		addAction(PlayerToggleSneakEvent.class, "Slayer-GroundPound", (event) -> {
			Player p = event.getPlayer();
			
			if (p.isFlying()) return;
			if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
				Location loc = p.getLocation();
				loc.add(0.0D, -1.0D, 0.0D);
				p.setVelocity(new Vector(0.0D, -1.5D, 0.0D));
				rg(p, 1, 3);
			}
		}, new KitCooldown(5));
		
		addAction(PlayerInteractEvent.class, "Slayer-SwordDash", (event) -> {
			Player p = event.getPlayer();
			if(p.getItemInUse() == null) return;
			if(p.getItemInUse().getType() != Material.STONE_SWORD) return;
			
			
		}, new KitCooldown(5));
		
		addAction(MinigamePlayerDamageEvent.class, "Slayer-Rage", (event) -> {
			Player p = event.getPlayer();
			
			
			if(event.getDamage() >= p.getHealth()) {
				
			}
			
			
			
		}, null);
		
		
		
	}
	
	public void rg(final Player p, final int start, final int rad) {
		ZombieSurvival zs = JavaPlugin.getPlugin(ZombieSurvival.class);
		Bukkit.getScheduler().runTaskLater(zs, new Runnable() {
			public void run() {
				if(start < rad) {
					if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR) {
						for (Block b : circle(p, start)) {
							b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, b.getType());
							FallingBlock fb = b.getWorld().spawnFallingBlock(b.getLocation(), b.getState().getBlockData());
							fb.setHurtEntities(false);
							fb.setVelocity(new Vector(0, .35, 0));
							
							//Location loc = b.getLocation().add(0, 1, 0);
							List<Entity> ents = fb.getNearbyEntities(1, 1, 1);
							if(ents.size() > 0) {
								for(Entity e : ents) {
									if(e instanceof Player) continue;
									if(e instanceof FallingBlock) continue;
									Vector direction = e.getLocation().toVector().add(fb.getLocation().toVector().multiply(-1));
									if(p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) continue;
									direction.setY(0.5);
									e.setVelocity(direction);
								}
							}
						}
						rg(p, start+1, rad);
					}
				}
			}

		}, 5L);
	}
	
	public List<Block> circle(Player p, int radius) {
		List<Block> blocks = new ArrayList<Block>();
		for(int x = (p.getLocation().getBlockX()-radius); x  <= (p.getLocation().getBlockX() + radius); x++) {
			for(int z = (p.getLocation().getBlockZ()-radius); z <= (p.getLocation().getBlockZ()+radius); z++) {
				blocks.add(p.getWorld().getBlockAt(x, p.getLocation().getBlockY()-1, z));
			}
		}
		return blocks;
	}
	
}