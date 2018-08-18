package nl.wouter.minetopiafarms.events;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import nl.wouter.minetopiafarms.Main;
import nl.wouter.minetopiafarms.utils.CustomFlags;
import nl.wouter.minetopiafarms.utils.Utils;
import wouter.is.cool.SDBPlayer;

public class BlockBreaker implements Listener {

	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		// GMC -> no effects
		if (e.getPlayer().getGameMode() == GameMode.CREATIVE) {
			return;
		}
		
		Player p = e.getPlayer();
		
		if (CustomFlags.hasFlag(p) && !p.hasPermission("minetopiafarms.bypassregions")) {
			e.setCancelled(true);
		}

		if (e.getBlock().getType().toString().contains("_ORE")) {
			if (!SDBPlayer.createSDBPlayer(e.getPlayer()).getPrefix().equalsIgnoreCase("Mijnwerker")) {
				e.getPlayer().sendMessage(Main.getMessage("BeroepNodig").replaceAll("<Beroep>", "mijnwerker"));
				e.setCancelled(true);
				return;
			}
			if (!e.getPlayer().getInventory().getItemInMainHand().getType().toString().contains("PICKAXE")) {
				e.getPlayer().sendMessage(Main.getMessage("ToolNodig").replaceAll("<Tool>", "houweel"));
				e.setCancelled(true);
				return;
			}
			if (!CustomFlags.isAllowed(p, "mijn")) {
				p.sendMessage(Main.getMessage("GeenRegion").replaceAll("<Tag>", "mijn"));
				e.setCancelled(true);
				return;
			}

			

			Material blockType = e.getBlock().getType();
			e.setCancelled(true);
			e.getBlock().getLocation().getBlock().setType(Material.COBBLESTONE);
			if (blockType == Material.COAL_ORE) {
				e.getPlayer().getInventory().addItem(new ItemStack(Material.COAL));
			} else if (blockType == Material.DIAMOND_ORE) {
				e.getPlayer().getInventory().addItem(new ItemStack(Material.DIAMOND));
			} else if (blockType == Material.EMERALD_ORE) {
				e.getPlayer().getInventory().addItem(new ItemStack(Material.EMERALD));
			} else if (blockType == Material.GOLD_ORE) {
				e.getPlayer().getInventory().addItem(new ItemStack(Material.GOLD_INGOT));
			} else if (blockType == Material.IRON_ORE) {
				e.getPlayer().getInventory().addItem(new ItemStack(Material.IRON_INGOT));
			} else if (blockType == Material.LAPIS_ORE) {
				e.getPlayer().getInventory().addItem(new ItemStack(Material.INK_SACK, 1, (byte) 4));
			} else if (blockType == Material.REDSTONE_ORE || blockType == Material.GLOWING_REDSTONE_ORE) {
				e.getPlayer().getInventory().addItem(new ItemStack(Material.REDSTONE));
			}
			e.getBlock().getDrops().clear();
			Utils.ironOres.add(e.getBlock().getLocation());
			Utils.handleToolDurability(e.getPlayer());
			Bukkit.getScheduler().runTaskLater(Main.pl, new Runnable() {
				@Override
				public void run() {
					e.getBlock().setType(blockType);
					Utils.ironOres.remove(e.getBlock().getLocation());
				}
			}, /* seconds * 20 */ 120 * 20);
		}
	}
}