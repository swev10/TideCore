package com.tideCore;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;

public class CropRegionReplacer {

    public static void replace(Player player, Material newCrop) {
        World world = player.getWorld();

        RegionManager regionManager = WorldGuard.getInstance()
                .getPlatform()
                .getRegionContainer()
                .get(BukkitAdapter.adapt(world));

        if (regionManager == null) return;

        boolean changed = false;

        for (int i = 1; i <= 10; i++) {
            ProtectedRegion region = regionManager.getRegion("crops_" + i);
            if (region == null) continue;

            BlockVector3 min = region.getMinimumPoint();
            BlockVector3 max = region.getMaximumPoint();

            for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
                for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                    for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                        Block block = world.getBlockAt(x, y, z);
                        if (isReplaceableCrop(block.getType()) && block.getType() != newCrop) {
                            block.setType(newCrop);
                            if (block.getBlockData() instanceof Ageable ageable) {
                                ageable.setAge(ageable.getMaximumAge());
                                block.setBlockData(ageable);
                            }
                            changed = true;
                        }
                    }
                }
            }
        }

        if (changed) {
            player.sendTitle("§f⌠  CROPS UPDATED!", "§7All fields changed to §a" + format(newCrop), 10, 40, 10);
        } else {
            player.sendTitle("§cNo crops changed", "§7All crops are already §f" + format(newCrop), 10, 40, 10);
        }
    }

    private static boolean isReplaceableCrop(Material mat) {
        return mat == Material.WHEAT
                || mat == Material.CARROTS
                || mat == Material.POTATOES
                || mat == Material.BEETROOTS
                || mat == Material.NETHER_WART;
    }

    private static String format(Material mat) {
        return mat.toString().toLowerCase().replace("_", " ");
    }
}
