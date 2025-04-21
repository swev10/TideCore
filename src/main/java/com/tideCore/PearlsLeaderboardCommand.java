package com.tideCore;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class PearlsLeaderboardCommand {

    public static void show(Player viewer, int page) {
        File dir = new File(TideCore.getInstance().getDataFolder(), "players");
        if (!dir.exists() || !dir.isDirectory()) {
            viewer.sendMessage(MessageUtils.prefix() + "§cNo player data found.");
            return;
        }

        Map<String, Integer> pearlMap = new HashMap<>();

        for (File file : Objects.requireNonNull(dir.listFiles())) {
            String uuid = file.getName().replace(".yml", "");
            try {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                int pearls = config.getInt("pearls", 0);
                if (pearls > 0) {
                    pearlMap.put(offlinePlayer.getName(), pearls);
                }
            } catch (Exception ignored) {}
        }

        List<Map.Entry<String, Integer>> sorted = pearlMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());

        int totalPages = (int) Math.ceil(sorted.size() / 10.0);
        if (page > totalPages) page = totalPages;
        if (page < 1) page = 1;

        int start = (page - 1) * 10;
        int end = Math.min(start + 10, sorted.size());

        viewer.sendMessage("§x§b§d§9§c§e§e§lTop Pearl Holders §7(Page " + page + "/" + totalPages + ")");
        for (int i = start; i < end; i++) {
            Map.Entry<String, Integer> entry = sorted.get(i);
            viewer.sendMessage("§x§b§d§9§c§e§e#" + (i + 1) +
                    " §7▸ §f" + entry.getKey() +
                    " §8- §x§b§d§9§c§e§e" + entry.getValue() + " pearls");
        }
    }
}
