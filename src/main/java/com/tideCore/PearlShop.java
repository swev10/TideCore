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
import java.util.List;

public class PearlShop implements Listener {

    private static YamlConfiguration config;

    public static void load() {
        File file = new File(TideCore.getInstance().getDataFolder(), "Pearlshop.yml");
        if (!file.exists()) {
            Bukkit.getLogger().warning("[TideCore] Pearlshop.yml not found!");
            return;
        }

        config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("items");

        if (section == null) {
            Bukkit.getLogger().warning("[TideCore] No 'items' section found in Pearlshop.yml!");
        } else {
            Bukkit.getLogger().info("[TideCore] Loaded PearlShop items: " + section.getKeys(false));
        }
    }

    public static void open(Player player) {
        if (config == null) load();

        String title = MessageUtils.color(config.getString("menu.title", "&aPearl Shop"));
        int rows = config.getInt("menu.rows", 4);
        Inventory gui = Bukkit.createInventory(null, rows * 9, title);

        ConfigurationSection items = config.getConfigurationSection("items");
        if (items == null) {
            player.sendMessage(MessageUtils.prefix() + "§cPearlshop.yml has no items defined.");
            return;
        }

        for (String key : items.getKeys(false)) {
            ConfigurationSection itemSec = items.getConfigurationSection(key);
            if (itemSec == null) continue;

            Material mat = Material.matchMaterial(itemSec.getString("material", "STONE"));
            if (mat == null) continue;

            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;

            String display = itemSec.getString("display_name", "&fItem");
            meta.setDisplayName(MessageUtils.color(display));

            List<String> lore = itemSec.getStringList("lore").stream()
                    .map(line -> MessageUtils.color(line.replace("{pearls}", String.valueOf(PlayerDataManager.getPearls(player)))))
                    .toList();
            meta.setLore(lore);

            if (itemSec.getBoolean("enchanted", false)) {
                meta.addEnchant(Enchantment.MENDING, 1, true);
            }
            if (itemSec.getBoolean("hide_enchants", false)) {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            item.setItemMeta(meta);
            gui.setItem(itemSec.getInt("slot", 0), item);
        }

        player.openInventory(gui);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (!e.getView().getTitle().contains("Pearl Shop")) return;

        e.setCancelled(true);

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        String clickedName = clicked.getItemMeta().getDisplayName();
        ConfigurationSection items = config.getConfigurationSection("items");
        if (items == null) return;

        for (String key : items.getKeys(false)) {
            ConfigurationSection itemSec = items.getConfigurationSection(key);
            if (itemSec == null) continue;

            String configName = MessageUtils.color(itemSec.getString("display_name", ""));
            if (clickedName.equals(configName)) {
                String cmd = itemSec.getString("command");
                if (cmd != null && !cmd.isEmpty()) {
                    player.closeInventory();
                    player.performCommand(cmd);
                }
                break;
            }
        }
    }
}
