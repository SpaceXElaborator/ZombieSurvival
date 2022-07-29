package com.terturl.zombiesurvival;

import org.bukkit.plugin.java.JavaPlugin;

import com.terturl.mcessentials.MCEssentials;

public class ZombieSurvival extends JavaPlugin {

	private static ZombieSurvival instance;

	@Override
	public void onEnable() {
		instance = this;

		new ZombieMinigame();
		final MCEssentials mc = JavaPlugin.getPlugin(MCEssentials.class);
		mc.registerCommand(new TestCommand());
	}

	public static ZombieSurvival getInstance() {
		return instance;
	}

}