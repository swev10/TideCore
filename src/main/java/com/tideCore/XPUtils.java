
package com.tideCore;

import org.bukkit.entity.Player;
import org.bukkit.Sound;

public class XPUtils {

    public static int getXPRequiredForLevel(Player player, int level) {
        int prestige = PlayerDataManager.getPrestige(player);
        double scale = 1 + (prestige * 0.1); // 10% ease per prestige
        return (int) (100 + Math.pow(level, 1.5) * 25 / scale);
    }

    public static String getProgressBar(Player player, int currentXP, int level) {
        int requiredXP = getXPRequiredForLevel(player, level);
        double percent = Math.min(1.0, (double) currentXP / requiredXP);
        int filledBars = (int) (percent * 20);

        StringBuilder bar = new StringBuilder("§7[");
        for (int i = 0; i < 20; i++) {
            if (i < filledBars) {
                bar.append("§x§8§5§B§3§D§7|");
            } else {
                bar.append("§7|");
            }
        }
        bar.append("§7]");
        return bar.toString();
    }

    public static void addXP(Player player, int xp) {
        int currentXP = PlayerDataManager.getXP(player);
        int level = PlayerDataManager.getLevel(player);

        PlayerDataManager.setXP(player, currentXP + xp);
        checkLevelUp(player);
    }

    public static void checkLevelUp(Player player) {
        int level = PlayerDataManager.getLevel(player);
        int xp = PlayerDataManager.getXP(player);
        int needed = getXPRequiredForLevel(player, level);

        while (xp >= needed) {
            xp -= needed;
            level += 1;

            PlayerDataManager.setLevel(player, level);
            PlayerDataManager.setXP(player, xp);

            // Reward and notify
            int tokenReward = 100;
            PlayerDataManager.addTokens(player, tokenReward);

            player.sendTitle("§b§lLEVEL UP!",
                    "§7Now level §a" + level + " §8| §e+" + tokenReward + " Tokens",
                    10, 50, 10);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);

            needed = getXPRequiredForLevel(player, level);
        }
    }
}
