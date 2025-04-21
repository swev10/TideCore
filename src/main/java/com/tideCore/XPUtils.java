package com.tideCore;

import org.bukkit.entity.Player;

public class XPUtils {

    public static int getXPRequiredForLevel(Player player, int level) {
        int prestige = PlayerDataManager.getPrestige(player);
        double scale = 1 + (prestige * 0.1); // Each prestige makes leveling easier
        return (int) (100 + (Math.pow(level, 1.5) * 25 / scale));
    }

    public static String getProgressBar(Player player, int currentXP, int level) {
        int required = getXPRequiredForLevel(player, level);
        double percent = Math.min(1.0, (double) currentXP / required);
        int bars = 20;
        int filledBars = (int) (percent * bars);

        StringBuilder bar = new StringBuilder("§7[");
        for (int i = 0; i < bars; i++) {
            if (i < filledBars) {
                bar.append("§x§8§5§B§3§D§7|"); // #85B3D7 filled
            } else {
                bar.append("§7|");
            }
        }
        bar.append("§7]");
        return bar.toString();
    }

    public static void checkLevelUp(Player player) {
        int level = PlayerDataManager.getLevel(player);
        int xp = PlayerDataManager.getXP(player);
        int needed = getXPRequiredForLevel(player, level);

        while (xp >= needed) {
            PlayerDataManager.setLevel(player, level + 1);
            PlayerDataManager.setXP(player, xp - needed);

            // Rewards
            int tokenReward = TideCore.getInstance().getConfig().getInt("leveling.token-reward", 100);
            int moneyReward = 2500;
            int expReward = 10000;

            // Grant rewards
            TideCore.getEconomy().depositPlayer(player, moneyReward);
            player.giveExp(expReward);
            PlayerDataManager.addTokens(player, tokenReward);

            // Title message
            player.sendTitle(
                    "§x§5§E§A§C§E§A§l>> LEVEL UP <<",
                    "§x§b§e§f§5§9§7$" + moneyReward + " §7| " +
                            "§x§9§7§b§4§f§5" + expReward + " Exp §7| " +
                            "§x§f§1§d§3§6§e" + tokenReward + " Tokens",
                    10, 50, 10
            );

            player.playSound(player.getLocation(), org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1.2f);

            // Refresh values for next loop
            level = PlayerDataManager.getLevel(player);
            xp = PlayerDataManager.getXP(player);
            needed = getXPRequiredForLevel(player, level);
        }
    }
}
