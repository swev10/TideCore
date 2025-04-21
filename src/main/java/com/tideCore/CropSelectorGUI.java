package com.tideCore;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CropSelectorGUI implements Listener {

    public static void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "Select Crop");

        addCrop(gui, Material.WHEAT, "Wheat", 1, 10);
        addCrop(gui, Material.CARROT, "Carrot", 10, 12);
        addCrop(gui, Material.POTATO, "Potato", 20, 14);
        addCrop(gui, Material.BEETROOT, "Beetroot", 30, 16);
        addCrop(gui, Material.NETHER_WART, "Nether Wart", 40, 22);

        player.openInventory(gui);
    }

    private static void addCrop(Inventory gui, Material displayItem, String name, int requiredLevel, int slot) {
        ItemStack item = new ItemStack(displayItem);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§f" + name + " §7(Requires Level " + requiredLevel + ")");
        item.setItemMeta(meta);
        gui.setItem(slot, item);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().equalsIgnoreCase("Select Crop")) return;

        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        int level = PlayerDataManager.getLevel(player);
        Material type = clicked.getType();

        if (type == Material.WHEAT) {
            if (level >= 1) {
                CropRegionReplacer.replace(player, Material.WHEAT);
            } else {
                deny(player, 1, "Wheat");
            }
        } else if (type == Material.CARROT) {
            if (level >= 10) {
                CropRegionReplacer.replace(player, Material.CARROTS);
            } else {
                deny(player, 10, "Carrots");
            }
        } else if (type == Material.POTATO) {
            if (level >= 20) {
                CropRegionReplacer.replace(player, Material.POTATOES);
            } else {
                deny(player, 20, "Potatoes");
            }
        } else if (type == Material.BEETROOT) {
            if (level >= 30) {
                CropRegionReplacer.replace(player, Material.BEETROOTS);
            } else {
                deny(player, 30, "Beetroot");
            }
        } else if (type == Material.NETHER_WART) {
            if (level >= 40) {
                CropRegionReplacer.replace(player, Material.NETHER_WART);
            } else {
                deny(player, 40, "Nether Wart");
            }
        }

        player.closeInventory();
    }

    private void deny(Player player, int req, String cropName) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
        player.sendMessage(MessageUtils.prefix() + "§cYou need level §e" + req + " §cto select §f" + cropName + "§c.");
    }
}
