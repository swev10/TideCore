package com.tideCore;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class PrestigeManager {

    private static final FileConfiguration config = TideCore.getInstance().getConfig();

    public static boolean canPrestige(Player player) {
        int level = PlayerDataManager.getLevel(player);
        int prestige = PlayerDataManager.getPrestige(player);
        int cap = config.getInt("prestige.max", 10);

        if (prestige >= cap) return false;

        int requiredLevel = 10 + (prestige * 5);
        if (level < requiredLevel) return false;

        double requiredMoney = getPrestigeCost(prestige);
        return TideCore.getEconomy().has(player, requiredMoney);
    }

    public static double getPrestigeCost(int prestige) {
        double base = config.getDouble("prestige.base-cost", 50000);
        return base * (prestige + 1);
    }

    public static void prestige(Player player) {
        int prestige = PlayerDataManager.getPrestige(player);
        double cost = getPrestigeCost(prestige);

        if (!TideCore.getEconomy().withdrawPlayer(player, cost).transactionSuccess()) {
            player.sendMessage(MessageUtils.prefix() + "§cYou don't have enough money.");
            return;
        }

        PlayerDataManager.setLevel(player, 1);
        if (config.getBoolean("prestige.reset-xp-on-prestige", true)) {
            PlayerDataManager.setXP(player, 0);
        }
        PlayerDataManager.setPrestige(player, prestige + 1);

        player.sendTitle("§b§lPRESTIGED!", "§fYou're now §ePrestige " + (prestige + 1), 10, 60, 10);
        player.sendMessage(MessageUtils.prefix() + "§aYou've prestiged to §bPrestige " + (prestige + 1) + "§a!");

        if (config.getBoolean("prestige.broadcast", true)) {
            Bukkit.broadcastMessage(MessageUtils.prefix() +
                    "§b" + player.getName() + " §ahas prestiged to §ePrestige " + (prestige + 1) + "§a!");
        }

        reward(player, prestige + 1);
    }

    private static void reward(Player player, int prestigeLevel) {
        // TODO: Add specific rewards per prestige if needed
    }
}
