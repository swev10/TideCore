package com.tideCore;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayerDataManager {

    public static File getFile(Player player) {
        return new File(TideCore.getInstance().getDataFolder(), "players/" + player.getUniqueId() + ".yml");
    }

    public static FileConfiguration get(Player player) {
        File file = getFile(player);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public static void save(Player player) {
        File file = getFile(player);
        FileConfiguration config = get(player);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // XP & Level
    public static int getXP(Player player) {
        return get(player).getInt("xp", 0);
    }

    public static void setXP(Player player, int xp) {
        get(player).set("xp", Math.max(0, xp));
        save(player);
    }

    public static void addXP(Player player, int xp) {
        int current = getXP(player);
        setXP(player, current + xp); // This will save automatically
    }

    public static int getLevel(Player player) {
        return get(player).getInt("level", 1);
    }

    public static void setLevel(Player player, int level) {
        get(player).set("level", Math.max(1, level));
        save(player);
    }

    public static void addLevel(Player player, int amount) {
        setLevel(player, getLevel(player) + amount);
    }

    // Prestige
    public static int getPrestige(Player player) {
        return get(player).getInt("prestige", 0);
    }

    public static void setPrestige(Player player, int prestige) {
        get(player).set("prestige", Math.max(0, prestige));
        save(player);
    }

    // Tokens
    public static int getTokens(Player player) {
        return get(player).getInt("tokens", 0);
    }

    public static void setTokens(Player player, int value) {
        File file = getFile(player);
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("tokens", Math.max(0, value));
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addTokens(Player player, int amount) {
        setTokens(player, getTokens(player) + amount);
    }

    public static void takeTokens(Player player, int amount) {
        setTokens(player, getTokens(player) - amount);
    }

    // Pearls
    public static int getPearls(Player player) {
        return get(player).getInt("pearls", 0);
    }

    public static void setPearls(Player player, int value) {
        File file = getFile(player);
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("pearls", Math.max(0, value));
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addPearls(Player player, int amount) {
        setPearls(player, getPearls(player) + amount);
    }

    public static void takePearls(Player player, int amount) {
        setPearls(player, getPearls(player) - amount);
    }

    // Purchase History
    public static void logPurchase(Player player, String itemName) {
        FileConfiguration config = get(player);
        List<String> purchases = config.getStringList("purchases");

        if (purchases == null) purchases = new ArrayList<>();
        purchases.add(itemName);

        config.set("purchases", purchases);
        save(player);
    }

    public static List<String> getPurchases(Player player) {
        List<String> purchases = get(player).getStringList("purchases");
        return purchases != null ? purchases : new ArrayList<>();
    }
}
