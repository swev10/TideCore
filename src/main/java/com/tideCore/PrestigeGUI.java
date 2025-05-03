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
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;

public class PrestigeGUI implements Listener {

    private static final String GUI_TITLE = "§b§lPrestige Menu";

    public static void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, GUI_TITLE);
        fillBorder(gui, Material.LIGHT_BLUE_STAINED_GLASS_PANE, Material.BLUE_STAINED_GLASS_PANE);

        ItemStack prestigeButton = createPrestigeButton(player);
        gui.setItem(13, prestigeButton);

        player.openInventory(gui);
        animateBorder(gui, player);
    }

    private static ItemStack createPrestigeButton(Player player) {
        int currentPrestige = PlayerDataManager.getPrestige(player);
        int nextPrestige = currentPrestige + 1;
        int currentLevel = PlayerDataManager.getLevel(player);
        double requiredMoney = 10000 * Math.pow(1.5, currentPrestige);
        int requiredLevel = 10 + (currentPrestige * 5);

        boolean hasLevel = currentLevel >= requiredLevel;
        boolean hasMoney = TideCore.getEconomy().getBalance(player) >= requiredMoney;
        boolean canPrestige = hasLevel && hasMoney;

        ItemStack egg = new ItemStack(Material.DRAGON_EGG);
        ItemMeta meta = egg.getItemMeta();

        meta.setDisplayName("§8[§x§C§C§A§B§F§D\uD83D\uDD25§8] §x§0§0§B§7§F§F§lPRESTIGE" + (canPrestige ? "§8(§a✓§8)" : "§8(§c✘§8)"));
        DecimalFormat df = new DecimalFormat("#,###.##");
        meta.setLore(java.util.List.of(
                "§8ᴘʀᴇѕᴛɪɢᴇ ʙᴜᴛᴛᴏɴ",
                "",
                "§x§0§0§B§7§F§F▎ ɪɴꜰᴏʀᴍᴀᴛɪᴏɴ",
                "§x§0§0§B§7§F§F▎ §fʀᴇᴍᴏᴠᴇs ᴛʜᴇ ʀᴇǫᴜɪʀᴇᴅ ʟᴇᴠᴇʟ",
                "§x§0§0§B§7§F§F▎ §fᴀɴᴅ ᴇxᴘᴇʀɪᴇɴᴄᴇ ᴘᴇʀ ʟᴇᴠᴇʟ ᴜᴘ",
                "",
                "§x§0§0§B§7§F§F▎ ʀᴇǫᴜɪʀᴇᴍᴇɴᴛs",
                "§x§0§0§B§7§F§F▎ §fᴍᴏɴᴇʏ:" + df.format(requiredMoney),
                "§x§0§0§B§7§F§F▎ §fʟᴇᴠᴇʟ:" + requiredLevel,
                "§x§0§0§B§7§F§F▎ §fᴇʟɪɢɪʙʟᴇ:" + (canPrestige ? "§a✓" : "§c✘"),
                "",
                "§x§0§0§B§7§F§F▎ ʀᴇᴡᴀʀᴅs",
                "§x§0§0§B§7§F§F▎ §fᴍᴜʟᴛɪ:",
                "§x§0§0§B§7§F§F▎ §fɢᴇɴѕʟᴏᴛѕ:",
                "",
                canPrestige ? "§aᴄʟɪᴄᴋ ᴛᴏ ᴘʀᴇsᴛɪɢᴇ!" : "§cʏᴏᴜ ᴅᴏ ɴᴏᴛ ᴍᴇᴇᴛ ᴛʜᴇ ʀᴇǫᴜɪʀᴇᴍᴇɴᴛs!"
        ));
        egg.setItemMeta(meta);
        return egg;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (!e.getView().getTitle().equals(GUI_TITLE)) return;

        e.setCancelled(true);

        if (e.getSlot() != 13) return;

        int prestige = PlayerDataManager.getPrestige(player);
        int level = PlayerDataManager.getLevel(player);
        double money = TideCore.getEconomy().getBalance(player);
        double requiredMoney = 10000 * Math.pow(1.5, prestige);
        int requiredLevel = 10 + (prestige * 5);

        if (level < requiredLevel) {
            player.closeInventory();
            player.sendMessage("§cYou need level " + requiredLevel + " to prestige.");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
            return;
        }

        if (money < requiredMoney) {
            player.closeInventory();
            player.sendMessage("§cYou need $" + new DecimalFormat("#,###.##").format(requiredMoney) + " to prestige.");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
            return;
        }

        TideCore.getEconomy().withdrawPlayer(player, requiredMoney);
        PlayerDataManager.setLevel(player, 1);
        PlayerDataManager.setXP(player, 0);
        PlayerDataManager.setPrestige(player, prestige + 1);

        player.sendMessage("§aYou have prestiged to §ePrestige " + (prestige + 1) + "§a!");
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1.2f);
        player.closeInventory();
    }

    private static void fillBorder(Inventory gui, Material outer, Material inner) {
        ItemStack borderItem = new ItemStack(outer);
        ItemMeta meta = borderItem.getItemMeta();
        meta.setDisplayName(" ");
        borderItem.setItemMeta(meta);

        for (int i = 0; i < gui.getSize(); i++) {
            if (i <= 9 || i >= 17 || i % 9 == 0 || i % 9 == 8) {
                gui.setItem(i, borderItem);
            }
        }
    }

    private static void animateBorder(Inventory gui, Player player) {
        new BukkitRunnable() {
            final Material[] colors = {Material.LIGHT_BLUE_STAINED_GLASS_PANE, Material.BLUE_STAINED_GLASS_PANE};
            int tick = 0;

            @Override
            public void run() {
                if (!player.isOnline() || !player.getOpenInventory().getTitle().equals(GUI_TITLE)) {
                    cancel();
                    return;
                }

                fillBorder(gui, colors[tick % colors.length], colors[(tick + 1) % colors.length]);
                tick++;
            }
        }.runTaskTimer(TideCore.getInstance(), 0L, 20L);
    }
}
