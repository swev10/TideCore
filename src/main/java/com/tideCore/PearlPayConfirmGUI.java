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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PearlPayConfirmGUI implements Listener {

    private static final Map<Player, TransferData> pendingTransfers = new HashMap<>();

    public static void open(Player sender, Player receiver, int amount) {
        Inventory inv = Bukkit.createInventory(null, 27, "§x§b§d§9§c§e§eConfirm Pearl Transfer");

        ItemStack confirm = new ItemStack(Material.LIME_WOOL);
        ItemMeta cMeta = confirm.getItemMeta();
        cMeta.setDisplayName("§aClick to Confirm");
        cMeta.setLore(List.of(
                "§7Pay §x§b§d§9§c§e§e" + amount + " Pearls",
                "§7To §f" + receiver.getName()
        ));
        confirm.setItemMeta(cMeta);

        ItemStack cancel = new ItemStack(Material.RED_WOOL);
        ItemMeta xMeta = cancel.getItemMeta();
        xMeta.setDisplayName("§cCancel");
        xMeta.setLore(List.of("§7No changes made."));
        cancel.setItemMeta(xMeta);

        inv.setItem(11, confirm);
        inv.setItem(15, cancel);

        pendingTransfers.put(sender, new TransferData(receiver, amount));
        sender.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (!e.getView().getTitle().contains("Confirm Pearl Transfer")) return;

        e.setCancelled(true);
        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        TransferData data = pendingTransfers.get(player);
        if (data == null) return;

        String name = clicked.getItemMeta().getDisplayName();

        if (name.contains("Confirm")) {
            if (PlayerDataManager.getPearls(player) < data.amount) {
                player.sendMessage(MessageUtils.prefix() + "§cYou no longer have enough pearls.");
                player.closeInventory();
                return;
            }

            PlayerDataManager.takePearls(player, data.amount);
            PlayerDataManager.addPearls(data.target, data.amount);

            player.sendMessage(MessageUtils.prefix() + "§aSent §x§b§d§9§c§e§e" + data.amount + " Pearls §ato §f" + data.target.getName());
            data.target.sendMessage(MessageUtils.prefix() + "§aReceived §x§b§d§9§c§e§e" + data.amount + " Pearls §afrom §f" + player.getName());

        } else if (name.contains("Cancel")) {
            player.sendMessage(MessageUtils.prefix() + "§7Cancelled pearl transfer.");
        }

        player.closeInventory();
        pendingTransfers.remove(player);
    }

    private static class TransferData {
        Player target;
        int amount;

        public TransferData(Player target, int amount) {
            this.target = target;
            this.amount = amount;
        }
    }
}
