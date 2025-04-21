package com.tideCore;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class BoosterFactory {

    public static ItemStack createBooster(double multiplier, int minutes) {
        ItemStack item = new ItemStack(Material.EMERALD);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName("§x§b§e§f§5§9§7Harvest Booster");

        List<String> lore = new ArrayList<>();
        lore.add("§8ʙᴏᴏsᴛᴇʀ");
        lore.add("§7");
        lore.add("§7Grants a boost for");
        lore.add("§7Your harvester hoe!");
        lore.add("§7");
        lore.add("§7 Multiplier: §e" + multiplier + "x");
        lore.add("§7 Time: §e" + minutes + " minutes");
        lore.add("§7");
        lore.add("§7Purchase §7boosters");
        lore.add("§7At §x§b§e§f§5§9§7store.tidegens.net");

        meta.setLore(lore);

        // Store both multiplier and time as PDC string: "2.0:10"
        meta.getPersistentDataContainer().set(
                TideCoreKeys.BOOSTER_KEY,
                PersistentDataType.STRING,
                multiplier + ":" + minutes
        );

        item.setItemMeta(meta);
        return item;
    }
}
