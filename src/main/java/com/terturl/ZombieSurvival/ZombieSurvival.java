package com.terturl.ZombieSurvival;

import org.bukkit.plugin.java.JavaPlugin;

import com.terturl.mcessentials.MCEssentials;

public class ZombieSurvival extends JavaPlugin {

	private static ZombieSurvival instance;
	
	public void onEnable() {
		instance = this;
		
		new ZombieMinigame();
		MCEssentials mc = JavaPlugin.getPlugin(MCEssentials.class);
		mc.registerCommand(new TestCommand());
	}
	
	public static ZombieSurvival getInstance() {
		return instance;
	}
	
}