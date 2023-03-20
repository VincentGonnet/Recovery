package com.vincentgonnet.recovery.handlers;

import com.vincentgonnet.recovery.Recovery;
import com.vincentgonnet.recovery.items.Compass;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CompassHandler implements Listener {

    private static CompassHandler s = null;
    private CompassHandler(Recovery plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public static CompassHandler getInstance() {
        return s;
    }
    public static CompassHandler getInstance(Recovery plugin) {
        if (s == null) {
            s = new CompassHandler(plugin);
        }
        return s;
    }

    private ArrayList<Player> recoveringPlayers = new ArrayList<Player>();

    public ArrayList<Player> getRecoveringPlayers() {
        return recoveringPlayers;
    }

    public void addRecoveringPlayer(Player player) {
        recoveringPlayers.add(player);
    }

    public void removeRecoveringPlayer(Player player) {
        recoveringPlayers.remove(player);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(""));
    }

    public boolean isPlayerRecovering(Player player) {
        return recoveringPlayers.contains(player);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onSwapHand(PlayerSwapHandItemsEvent event) {
        ItemStack item = event.getMainHandItem();
        if (item == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        if (meta.getDisplayName().equals(new Compass().getItemMeta().getDisplayName())) {
            if (isPlayerRecovering(event.getPlayer())) removeRecoveringPlayer(event.getPlayer());
            event.setMainHandItem(new ItemStack(Material.AIR));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.getCurrentItem() == null) return;
        if(event.getCurrentItem().getItemMeta() == null) return;
        if(!event.getCurrentItem().getItemMeta().getDisplayName().equals(new Compass().getItemMeta().getDisplayName())) return;
        event.setCancelled(true);
        if (isPlayerRecovering((Player) event.getWhoClicked())) removeRecoveringPlayer((Player) event.getWhoClicked());
        int slot = event.getSlot();
        if (event.getClickedInventory() != null) {
            event.getClickedInventory().setItem(slot, new ItemStack(Material.AIR));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if(isPlayerRecovering(event.getEntity())) removeRecoveringPlayer(event.getEntity());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if(isPlayerRecovering(player)) removeRecoveringPlayer(player);
        if (player.getInventory().getItemInOffHand().getItemMeta() == null || player.getInventory().getItemInOffHand().getItemMeta().getDisplayName() == null) return;
        if (player.getInventory().getItemInOffHand().getItemMeta().getDisplayName().equals(new Compass().getItemMeta().getDisplayName())) {
            player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(isPlayerRecovering(player)) removeRecoveringPlayer(player);
        if (player.getInventory().getItemInOffHand().getItemMeta() == null || player.getInventory().getItemInOffHand().getItemMeta().getDisplayName() == null) return;
        if (player.getInventory().getItemInOffHand().getItemMeta().getDisplayName().equals(new Compass().getItemMeta().getDisplayName())) {
            player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (Recovery.getMainConfig().getConfig().get("recover-on-respawn").equals(false) || !event.getPlayer().hasPermission("recovery.auto")) return;
        if (!Recovery.getMainConfig().getConfig().getStringList("recover-on-respawn-worlds").contains(event.getPlayer().getWorld().getName())) return;
        event.getPlayer().getInventory().setItemInOffHand(new Compass());
        addRecoveringPlayer(event.getPlayer());
    }
}
