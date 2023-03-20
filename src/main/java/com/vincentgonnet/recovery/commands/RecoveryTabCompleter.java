package com.vincentgonnet.recovery.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RecoveryTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1 && commandSender.hasPermission("recovery.reload")) {
            List<String> complete = new ArrayList<String>();
            complete.add("reload");
            return complete;
        }

        List<String> list = new ArrayList<String>();
        list.add("");
        return list;
    }
}
