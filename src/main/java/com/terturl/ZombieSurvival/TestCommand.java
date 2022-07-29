package com.terturl.zombiesurvival;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.terturl.mcessentials.MCEssentials;
import com.terturl.mcessentials.spigotframework.CraftCommand;

public class TestCommand extends CraftCommand {

	protected TestCommand() {
		super("test-command");
	}

	@Override
	protected void handleCommand(Player p, String[] args) {
		final MCEssentials mc = JavaPlugin.getPlugin(MCEssentials.class);
		mc.getMinigameHandler().addPlayerToMinigame("ZombieSurvival", p);
	}

	@Override
	protected void handleCommand(ConsoleCommandSender ccs, String[] args) {
		return;
	}

	@Override
	protected void handleCommand(BlockCommandSender bcs, String[] args) {
		return;
	}

}