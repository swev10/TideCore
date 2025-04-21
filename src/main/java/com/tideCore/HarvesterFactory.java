package com.tideCore;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class HarvesterFactory {

    public static ItemStack createHarvesterHoe() {
        ItemStack hoe = new ItemStack(Material.WOODEN_HOE);
        ItemMeta meta = hoe.getItemMeta();

        meta.setDisplayName("§x§7§B§B§8§E§9Harvester Hoe");
        meta.setLore(List.of(
                "§7Automatically collects crops",
                "§7Adds XP and Regrows instantly",
                "§7Used to level up your farm skill"
        ));

        meta.setUnbreakable(true); // ✅ Indestructible

        meta.getPersistentDataContainer().set(
                TideCoreKeys.HARVESTER_KEY,
                PersistentDataType.INTEGER,
                1
        );

        hoe.setItemMeta(meta);
        return hoe;
    }
}
