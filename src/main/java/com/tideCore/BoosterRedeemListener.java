package com.tideCore;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class BoosterRedeemListener implements Listener {

    @EventHandler
    public void onUseBooster(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!event.getAction().toString().contains("RIGHT")) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() != Material.EMERALD) return;
        if (!item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        if (!meta.getPersistentDataContainer().has(TideCoreKeys.BOOSTER_KEY, PersistentDataType.STRING)) return;

        if (BoosterManager.hasBooster(player)) {
            player.sendMessage(MessageUtils.prefix() + "§cYou already have a booster active!");
            return;
        }

        String stored = meta.getPersistentDataContainer().get(TideCoreKeys.BOOSTER_KEY, PersistentDataType.STRING);
        if (stored == null || !stored.contains(":")) return;

        String[] split = stored.split(":");
        double multiplier = Double.parseDouble(split[0]);
        int minutes = Integer.parseInt(split[1]);

        BoosterManager.applyBooster(player, minutes, multiplier);
        player.sendMessage(MessageUtils.prefix() + "§aYour §e" + multiplier + "x booster §ahas been activated for §e" + minutes + " minutes!");

        item.setAmount(item.getAmount() - 1);
    }
}
