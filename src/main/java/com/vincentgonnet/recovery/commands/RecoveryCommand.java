package com.vincentgonnet.recovery.commands;

import com.vincentgonnet.recovery.Recovery;
import com.vincentgonnet.recovery.handlers.CompassHandler;
import com.vincentgonnet.recovery.items.Compass;
import com.vincentgonnet.recovery.util.ConfigUtil;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RecoveryCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args) {

        ConfigUtil messagesConfig = Recovery.getMessagesConfig();

        if(args.length > 1) {
            sender.sendMessage(messagesConfig.getConfig().get("command-wrong-usage").toString().replace("%command%", "/recovery"));
            return false;
        } else if (args.length == 1) {
            if (!args[0].equals("reload")) {
                sender.sendMessage(messagesConfig.getConfig().get("command-wrong-usage").toString().replace("%command%", "/recovery"));
                return false;
            }

            if (!sender.hasPermission("recovery.reload")) {
                sender.sendMessage(messagesConfig.getConfig().get("command-no-permission").toString());
                return true;
            }
            Recovery.getMainConfig().reload();
            Recovery.getMessagesConfig().reload();
            sender.sendMessage(messagesConfig.getConfig().get("plugin-reloaded").toString());
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("[Recovery] Only players can run this command.");
            return true;
        }

        Player player = (Player) sender;

        if(!player.hasPermission("recovery.recover")) {
            sender.sendMessage(messagesConfig.getConfig().get("command-no-permission").toString());
            return true;
        }

        if (player.getInventory().getItemInOffHand().getType() != Material.AIR) {
            sender.sendMessage(messagesConfig.getConfig().get("command-offhand-not-empty").toString());
            return true;
        }

        if (player.getLastDeathLocation() == null) {
            sender.sendMessage(messagesConfig.getConfig().getString("player-has-not-died"));
            return true;
        }

        player.getInventory().setItemInOffHand(new Compass());
        CompassHandler.getInstance().addRecoveringPlayer(player);

        return true;
    }
}
