package com.terturl.ZombieSurvival;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.scheduler.BukkitScheduler;

import com.terturl.mcessentials.minigameapi.configuration.MinigameRunnerTimerExecution;

public class ZombieGameTimer extends MinigameRunnerTimerExecution {

	private int zombies = 0;
	private Boolean firstLaunch = true;
	
	private Location start;
	private Location end;
	
	@SuppressWarnings("unchecked")
	@Override
	public void runCleanup() {
		if(((List<UUID>)getActiveGame().getAdditionalInformation().getList("ZombiesInArena")).size() >= 1) {
			for(UUID uuid : (List<UUID>)getActiveGame().getAdditionalInformation().getList("ZombiesInArena")) {
				((Zombie)Bukkit.getEntity(uuid)).setHealth(0.0);
			}
			getActiveGame().getAdditionalInformation().getList("ZombiesInArena").clear();
		}
	}
	
	@Override
	public void inGameRunningMethod(Integer gameTime) {
		Random r = new Random();
		if(firstLaunch) {
			getActiveGame().getAdditionalInformation().addList("ZombiesInArena", new ArrayList<UUID>());
			zombies = r.nextInt((10 - 6) + 1) + 6;
			start = getActiveGame().getArena().getLocPoint1();
			end = getActiveGame().getArena().getLocPoint2();
			
			for(int zombie = 0; zombie < zombies; zombie++) {
				int xMin = Math.min((int) start.getX(), (int) end.getX());
				int xMax = Math.max((int) start.getX(), (int) end.getX());
				int zMin = Math.min((int) start.getZ(), (int) end.getZ());
				int zMax = Math.max((int) start.getZ(), (int) end.getZ());
				int x = r.nextInt(xMax - xMin) + xMin;
				int z = r.nextInt(zMax - zMin) + zMin;
				int y = start.getWorld().getHighestBlockYAt(x, z);
				spawnZombie(new Location(start.getWorld(), x, y + 1, z));
			}
			firstLaunch = false;
		}
		if(getActiveGame().getAdditionalInformation().getList("ZombiesInArena").size() == 0) {
			for(int zombie = 0; zombie < zombies; zombie++) {
				int xMin = Math.min((int) start.getX(), (int) end.getX());
				int xMax = Math.max((int) start.getX(), (int) end.getX());
				int zMin = Math.min((int) start.getZ(), (int) end.getZ());
				int zMax = Math.max((int) start.getZ(), (int) end.getZ());
				int x = r.nextInt(xMax - xMin) + xMin;
				int z = r.nextInt(zMax - zMin) + zMin;
				int y = start.getWorld().getHighestBlockYAt(x, z);
				spawnZombie(new Location(start.getWorld(), x, y + 1, z));
			}
			double twoPerc = zombies * 0.2;
			zombies = (int) Math.ceil(zombies + twoPerc);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public void spawnZombie(Location loc) {
		final Zombie z = (Zombie)loc.getWorld().spawnEntity(loc.subtract(0, 2, 0), EntityType.ZOMBIE);
		z.setFireTicks(0);
		z.setGravity(false);
		final BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		final int i = scheduler.scheduleSyncRepeatingTask(ZombieSurvival.getInstance(), new Runnable() {
			int j = 0;
			public void run() {
				if(j < 22) {
					j++;
					Block b = z.getLocation().getBlock();
					Block tmp = Bukkit.getWorld(b.getWorld().getName()).getHighestBlockAt(b.getLocation());
					tmp.getWorld().playEffect(tmp.getLocation(), Effect.STEP_SOUND, tmp.getType());
					z.teleport(z.getLocation().add(0, 0.1, 0));
				} else {
					z.setGravity(true);
				}
			}
		}, 0L, 1L);
		((List<UUID>)getActiveGame().getAdditionalInformation().getList("ZombiesInArena")).add(z.getUniqueId());
		getActiveGame().getEntitiesToNotBurn().add(z.getUniqueId());
		scheduler.scheduleSyncDelayedTask(ZombieSurvival.getInstance(), new Runnable() {
			public void run() {
				scheduler.cancelTask(i);
			}
		}, 500L);
	}
	
}