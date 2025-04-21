package com.tideCore;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BoosterManager {

    private static final Map<UUID, Long> activeBoosters = new HashMap<>();
    private static final Map<UUID, Double> multipliers = new HashMap<>();
    private static final Map<UUID, BossBar> boosterBars = new HashMap<>();

    public static boolean hasBooster(Player player) {
        return activeBoosters.containsKey(player.getUniqueId());
    }

    public static void applyBooster(Player player, int minutes, double multiplier) {
        UUID uuid = player.getUniqueId();
        long endTime = System.currentTimeMillis() + (minutes * 60L * 1000L);
        activeBoosters.put(uuid, endTime);
        multipliers.put(uuid, multiplier);

        BossBar bar = Bukkit.createBossBar("§aHarvest Booster Active", BarColor.GREEN, BarStyle.SEGMENTED_10);
        bar.setProgress(1.0);
        bar.addPlayer(player);
        boosterBars.put(uuid, bar);

        new BukkitRunnable() {
            @Override
            public void run() {
                long remaining = endTime - System.currentTimeMillis();
                if (remaining <= 0 || !player.isOnline()) {
                    activeBoosters.remove(uuid);
                    multipliers.remove(uuid);
                    bar.removeAll();
                    boosterBars.remove(uuid);
                    cancel();
                    return;
                }
                double percent = Math.max(0, Math.min(1, remaining / (minutes * 60.0 * 1000.0)));
                bar.setProgress(percent);
                bar.setTitle("§aHarvest Booster §7(" + multiplier + "x XP) §f" +
                        (remaining / 1000 / 60) + "m " + ((remaining / 1000) % 60) + "s left");
            }
        }.runTaskTimer(TideCore.getInstance(), 0L, 20L);
    }

    public static int getBoostedXP(int baseXP, Player player) {
        if (!hasBooster(player)) return baseXP;
        double multiplier = multipliers.getOrDefault(player.getUniqueId(), 1.0);
        return (int) (baseXP * multiplier);
    }
}
