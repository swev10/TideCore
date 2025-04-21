package com.tideCore;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PrestigeGUI implements Listener {

    public static void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "§9Prestige Confirmation");

        ItemStack yes = new ItemStack(Material.GREEN_WOOL);
        ItemMeta yesMeta = yes.getItemMeta();
        yesMeta.setDisplayName("§aYes, Prestige me!");
        yes.setItemMeta(yesMeta);

        ItemStack no = new ItemStack(Material.RED_WOOL);
        ItemMeta noMeta = no.getItemMeta();
        noMeta.setDisplayName("§cNo thanks!");
        no.setItemMeta(noMeta);

        gui.setItem(11, yes);
        gui.setItem(15, no);
        player.openInventory(gui);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getView().getTitle().equals("§9Prestige Confirmation")) return;

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        String name = clicked.getItemMeta().getDisplayName();

        if (name.contains("Prestige me")) {
            int prestige = PlayerDataManager.getPrestige(player);
            PlayerDataManager.setPrestige(player, prestige + 1);
            PlayerDataManager.setLevel(player, 1);
            PlayerDataManager.setXP(player, 0);
            player.sendMessage("§bYou have prestiged! Welcome to Prestige §e" + (prestige + 1));
            player.closeInventory();
        } else if (name.contains("No thanks")) {
            player.closeInventory();
        }
    }
}
