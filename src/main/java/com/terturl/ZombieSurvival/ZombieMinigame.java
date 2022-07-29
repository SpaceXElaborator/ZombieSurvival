package com.terturl.zombiesurvival;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.terturl.mcessentials.MCEssentials;
import com.terturl.mcessentials.minigameapi.Minigame;
import com.terturl.mcessentials.minigameapi.configuration.MinigameConfiguration;
import com.terturl.mcessentials.minigameapi.configuration.endstates.AllPlayersDeadEndState;
import com.terturl.mcessentials.minigameapi.configuration.loadstrategy.PureConfigLoadStrategy;
import com.terturl.mcessentials.minigameapi.leaderboard.Leaderboard;
import com.terturl.mcessentials.minigameapi.listeners.MinigameEntityDeathEvent;
import com.terturl.zombiesurvival.kits.SlayerKit;

import net.md_5.bungee.api.ChatColor;

public class ZombieMinigame {

	public ZombieMinigame() {

		final Leaderboard<MinigameEntityDeathEvent, Integer> lb = new Leaderboard<MinigameEntityDeathEvent, Integer>("ZombiesKilled") {
			@Override
			@SuppressWarnings("unchecked")
			@EventHandler
			public void onEvent(MinigameEntityDeathEvent event) {
				final EntityDeathEvent e = event.getEntityDeathEvent();
				if (!(e.getEntity() instanceof Zombie))
					return;
				final Zombie z = (Zombie) e.getEntity();
				if (!event.getActiveMinigame().getEntitiesToNotBurn().contains(z.getUniqueId()))
					return;
				final List<UUID> zombiesInArena = (List<UUID>) event.getActiveMinigame().getAdditionalInformation().getList("ZombiesInArena");
				if (!zombiesInArena.contains(z.getUniqueId()))
					return;
				((List<UUID>) event.getActiveMinigame().getAdditionalInformation().getList("ZombiesInArena")).remove(z.getUniqueId());
				event.getActiveMinigame().getEntitiesToNotBurn().remove(z.getUniqueId());
				addOrUpdateLeaderboard(e.getEntity().getKiller(), 1);
			}
		};

		final List<Integer> notifyingOn = new ArrayList<>();
		notifyingOn.add(15);
		notifyingOn.add(10);
		notifyingOn.add(5);
		notifyingOn.add(4);
		notifyingOn.add(3);
		notifyingOn.add(2);
		notifyingOn.add(1);

		final ItemStack kitSelector = new ItemStack(Material.EMERALD);
		final ItemMeta meta = kitSelector.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Kit Selection");
		final List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY + "Right Click To Select Kit");
		meta.setLore(lore);
		kitSelector.setItemMeta(meta);

		final MinigameConfiguration tMC = MinigameConfiguration.builder()
				.minPlayer(1)
				.maxPlayer(1)
				.removedOnDeath(true)
				.blocksBreakable(false)
				.canBlocksExplode(false)
				.blocksRegenerating(false)
				.fallingBlocksPlacable(false)
				.kitsEnabled(true)
				.kitSelector(kitSelector)
				.blocksPlaceable(false)
				.preStartExecution(new ZombiePreExecution())
				.runningTimerExecution(new ZombieGameTimer())
				.gameEndState(new AllPlayersDeadEndState())
				.lobbyCountdownEndTime(30)
				.lobbyCountdownNofitier(notifyingOn)
				.loadStrategy(new PureConfigLoadStrategy(ZombieSurvival.getInstance()))
				.lobbyNotEnoughPlayersString("Not Enough Players")
				.lobbyNotifyString("Game will start in %%timerValue%%")
				.build();

		final Minigame minigame = new Minigame("ZombieSurvival", tMC);
		final List<Leaderboard<?, ? extends Number>> leaderboards = new ArrayList<>();
		leaderboards.add(lb);
		minigame.addLeaderboards(leaderboards);
		createKits(minigame);
		final MCEssentials mc = JavaPlugin.getPlugin(MCEssentials.class);
		mc.getMinigameHandler().registerMinigame(minigame);
		minigame.createActiveGame();
		minigame.getMinigameConfig().getLoadStrategy().loadAllArenas();
	}

	private void createKits(Minigame m) {
		final ItemStack slayerItem = new ItemStack(Material.STONE_SWORD);
		final ItemMeta slayerMeta = slayerItem.getItemMeta();
		slayerMeta.setDisplayName(ChatColor.GOLD + "Slayer");
		slayerItem.setItemMeta(slayerMeta);

		//		Kit slayerKit = new Kit("Slayer", slayerItem);
		//		slayerKit.addItem(new ItemStack(Material.STONE_SWORD));
		//		slayerKit.addAction(PlayerInteractEvent.class, (e) -> {
		//			if(e.getItem() == null) return;
		//			if(!e.getItem().getType().equals(Material.STONE_SWORD)) return;
		//			Bukkit.getConsoleSender().sendMessage("Test");
		//		});

		m.getKitManager().addKit(new SlayerKit(slayerItem));
	}

}