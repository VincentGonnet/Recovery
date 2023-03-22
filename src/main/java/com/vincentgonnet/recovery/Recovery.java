package com.vincentgonnet.recovery;

import com.vincentgonnet.recovery.commands.RecoveryCommand;
import com.vincentgonnet.recovery.commands.RecoveryTabCompleter;
import com.vincentgonnet.recovery.handlers.CompassHandler;
import com.vincentgonnet.recovery.items.Compass;
import com.vincentgonnet.recovery.util.ConfigUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class Recovery extends JavaPlugin {

    static ConfigUtil mainConfig;
    static ConfigUtil messagesConfig;

    public static ConfigUtil getMainConfig() {
        return mainConfig;
    }

    public static ConfigUtil getMessagesConfig() {
        return messagesConfig;
    }

    public void loadConfigs(ConfigUtil mainConfig, ConfigUtil messagesConfig) {
        // Init : ticks-between-compass-refresh
        if (mainConfig.getConfig().get("ticks-between-compass-refresh") == null || !(mainConfig.getConfig().get("ticks-between-compass-refresh") instanceof Integer)) {
            mainConfig.getConfig().set("ticks-between-compass-refresh", 1);
            mainConfig.save();
        }
        // Init : recover-on-respawn
        if (mainConfig.getConfig().get("recover-on-respawn") == null || !(mainConfig.getConfig().get("recover-on-respawn") instanceof Boolean)) {
            mainConfig.getConfig().set("recover-on-respawn", false);
            mainConfig.save();
        }
        // Init : recover-on-respawn
        if (mainConfig.getConfig().get("recover-on-respawn-worlds") == null || !(mainConfig.getConfig().get("recover-on-respawn-worlds") instanceof List)) {
            ArrayList<String> world = new ArrayList<>();
            world.add("world");
            mainConfig.getConfig().set("recover-on-respawn-worlds", world);
            mainConfig.save();
        }

        // Init : distance-from-deathpoint
        if (messagesConfig.getConfig().get("distance-from-deathpoint") == null || !(mainConfig.getConfig().get("distance-from-deathpoint") instanceof String)) {
            messagesConfig.getConfig().set("distance-from-deathpoint", "§6Your deathpoint is %distance% blocks away");
            messagesConfig.save();
        }
        // Init : command-wrong-usage
        if (messagesConfig.getConfig().get("command-wrong-usage") == null || !(mainConfig.getConfig().get("command-wrong-usage") instanceof String)) {
            messagesConfig.getConfig().set("command-wrong-usage", "§cWrong usage : please run '%command%' instead");
            messagesConfig.save();
        }
        // Init : command-offhand-not-empty
        if (messagesConfig.getConfig().get("command-offhand-not-empty") == null || !(mainConfig.getConfig().get("command-offhand-not-empty") instanceof String)) {
            messagesConfig.getConfig().set("command-offhand-not-empty", "§cPlease be sure your offhand is empty before running this command");
            messagesConfig.save();
        }
        // Init : command-no-permission
        if (messagesConfig.getConfig().get("command-no-permission") == null || !(mainConfig.getConfig().get("command-no-permission") instanceof String)) {
            messagesConfig.getConfig().set("command-no-permission", "§cYou do not have permission to run this command.");
            messagesConfig.save();
        }
        // Init : plugin-reloaded
        if (messagesConfig.getConfig().get("plugin-reloaded") == null || !(mainConfig.getConfig().get("plugin-reloaded") instanceof String)) {
            messagesConfig.getConfig().set("plugin-reloaded", "§8[§1§lRecovery§r§8] §ePlugin reloaded.");
            messagesConfig.save();
        }
        // Init : player-has-not-died
        if (messagesConfig.getConfig().get("player-has-not-died") == null || !(mainConfig.getConfig().get("player-has-not-died") instanceof String)) {
            messagesConfig.getConfig().set("player-has-not-died", "§cCongrats ! You haven't died yet, hence you cannot get the Recovery Compass.");
            messagesConfig.save();
        }

    }

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("[Recovery] Plugin loaded.");

        getCommand("recovery").setExecutor(new RecoveryCommand());
        getCommand("recovery").setTabCompleter(new RecoveryTabCompleter());

        mainConfig = new ConfigUtil(this, "config.yml");
        messagesConfig = new ConfigUtil(this, "messages.yml");

        loadConfigs(mainConfig, messagesConfig);


        int ticksBetweenCompassRefresh = (int) mainConfig.getConfig().get("ticks-between-compass-refresh");

        CompassHandler.getInstance(this);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for (int i = 0 ; i < CompassHandler.getInstance().getRecoveringPlayers().size(); i++) {
                Player player = CompassHandler.getInstance().getRecoveringPlayers().get(i);
                double distance = player.getLastDeathLocation().distance(player.getLocation());
                if (distance < 2) {
                    i--;
                    CompassHandler.getInstance().removeRecoveringPlayer(player);
                    if (player.getInventory().getItemInOffHand().getItemMeta() == null || player.getInventory().getItemInOffHand().getItemMeta().getDisplayName() == null) return;
                    if (player.getInventory().getItemInOffHand().getItemMeta().getDisplayName().equals(new Compass().getItemMeta().getDisplayName())) {
                        player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
                    }
                    continue;
                }
                String formattedDistance = String.format(messagesConfig.getConfig().get("distance-from-deathpoint").toString().replace("%distance%", "%.0f"), distance);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(formattedDistance));
            }
        }, 0, ticksBetweenCompassRefresh);
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("[Recovery] Shutting down.");
    }

}
