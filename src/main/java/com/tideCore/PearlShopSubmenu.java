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

public class PearlShopSubmenu implements Listener {

    public static void open(Player player, String category) {
        File file = new File(TideCore.getInstance().getDataFolder(), "Pearlshop.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        ConfigurationSection catSection = config.getConfigurationSection(category + ".items");
        if (catSection == null) {
            player.sendMessage(MessageUtils.prefix() + "§cCategory not found.");
            return;
        }

        String title = MessageUtils.color("&x&b&d&9&c&e&e&l" + category.toUpperCase());
        Inventory gui = Bukkit.createInventory(null, 54, title);

        for (String key : catSection.getKeys(false)) {
            ConfigurationSection itemSec = catSection.getConfigurationSection(key);
            if (itemSec == null) continue;

            Material mat = Material.matchMaterial(itemSec.getString("material", "STONE"));
            if (mat == null) continue;

            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();

            String displayName = itemSec.getString("display_name", "&fItem");
            meta.setDisplayName(MessageUtils.color(displayName));

            List<String> lore = itemSec.getStringList("lore").stream()
                    .map(line -> MessageUtils.color(
                            line.replace("%pearls%", String.valueOf(PlayerDataManager.getPearls(player)))
                    )).toList();
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
        Inventory inv = e.getInventory();

        if (!e.getView().getTitle().contains("PEARLSHOP")) return;
        e.setCancelled(true);

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        File file = new File(TideCore.getInstance().getDataFolder(), "Pearlshop.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        String catName = e.getView().getTitle().replace("§x§b§d§9§c§e§e§l", "").toLowerCase();
        ConfigurationSection section = config.getConfigurationSection(catName + ".items");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            ConfigurationSection itemSec = section.getConfigurationSection(key);
            if (itemSec == null) continue;

            String configName = MessageUtils.color(itemSec.getString("display_name", ""));
            if (clicked.getItemMeta().getDisplayName().equals(configName)) {

                int cost = itemSec.getInt("price", 0);
                if (PlayerDataManager.getPearls(player) < cost) {
                    player.sendMessage(MessageUtils.prefix() + "§cThis costs §5" + cost + " pearls §cbut you only have §5" + PlayerDataManager.getPearls(player));
                    player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1f, 0.9f);
                    return;
                }

                PlayerDataManager.takePearls(player, cost);
                String command = itemSec.getString("command", "").replace("%player%", player.getName());
                if (!command.isEmpty()) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }

                player.sendMessage(MessageUtils.prefix() + "§aPurchased for §5" + cost + " pearls!");
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.2f);
                player.closeInventory();
                break;
            }
        }
    }
}
