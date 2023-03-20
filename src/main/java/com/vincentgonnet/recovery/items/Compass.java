package com.vincentgonnet.recovery.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Compass extends ItemStack {
    public Compass() {
        this.setType(Material.RECOVERY_COMPASS);
        this.setAmount(1);

        ItemMeta meta = this.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + "Recovery Compass");

        this.setItemMeta(meta);
        this.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);
    }
}
