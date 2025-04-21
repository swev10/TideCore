package com.tideCore;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PearlShopSubmenu implements Listener {

    public static void open(Player player, String fileName) {
        File file = new File(TideCore.getInstance().getDataFolder(), "Pearlshop_" + fileName + ".yml");
        if (!file.exists()) {
            player.sendMessage(MessageUtils.prefix() + "§cThat shop doesn't exist.");
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        String title = MessageUtils.color(config.getString("menu.title", "&fPearl Shop"));
        int rows = config.getInt("menu.rows", 3);
        Inventory gui = Bukkit.createInventory(null, rows * 9, title);

        ConfigurationSection items = config.getConfigurationSection("items");
        if (items == null) return;

        for (String key : items.getKeys(false)) {
            ConfigurationSection itemSec = items.getConfigurationSection(key);
            if (itemSec == null) continue;

            Material mat = Material.matchMaterial(itemSec.getString("material", "STONE"));
            if (mat == null) continue;

            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();

            meta.setDisplayName(MessageUtils.color(itemSec.getString("display_name", "&fItem")));

            List<String> rawLore = itemSec.getStringList("lore");
            List<String> lore = new ArrayList<>();
            int pearls = PlayerDataManager.getPearls(player);

            for (String line : rawLore) {
                lore.add(MessageUtils.color(line.replace("{pearls}", String.valueOf(pearls))));
            }

            meta.setLore(lore);

            if (itemSec.getBoolean("enchanted", false)) {
                meta.addEnchant(Enchantment.MENDING, 1, true);
            }
            if (itemSec.getBoolean("hide_enchants", false)) {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            item.setItemMeta(meta);
            gui.setItem(itemSec.getInt("slot"), item);
        }

        player.openInventory(gui);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        String title = e.getView().getTitle();
        if (!title.contains("Shop")) return;

        e.setCancelled(true);

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        String clickedName = clicked.getItemMeta().getDisplayName();

        File[] files = TideCore.getInstance().getDataFolder().listFiles((dir, name) -> name.startsWith("Pearlshop_"));
        if (files == null) return;

        for (File file : files) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection items = config.getConfigurationSection("items");
            if (items == null) continue;

            for (String key : items.getKeys(false)) {
                ConfigurationSection itemSec = items.getConfigurationSection(key);
                if (itemSec == null) continue;

                String configName = MessageUtils.color(itemSec.getString("display_name", ""));
                if (!clickedName.equals(configName)) continue;

                int price = itemSec.getInt("price", 0);
                if (price > 0) {
                    int current = PlayerDataManager.getPearls(player);
                    if (current < price) {
                        player.sendMessage(MessageUtils.prefix() + "§cThis costs §x§b§d§9§c§e§e" + price + " Pearls §cbut you only have §f" + current + "§c!");
                        player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1.2f);
                        return;
                    }

                    PlayerDataManager.takePearls(player, price);
                    player.sendMessage(MessageUtils.prefix() + "§aPurchased for §x§b§d§9§c§e§e" + price + " Pearls!");
                    player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.3f);
                    PlayerDataManager.logPurchase(player, clickedName);
                }

                String cmd = itemSec.getString("command");
                if (cmd != null && !cmd.isEmpty()) {
                    player.closeInventory();
                    player.performCommand(cmd.replace("%player%", player.getName()));
                }
                return;
            }
        }
    }
}
