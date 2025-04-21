package com.tideCore;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

public class HarvesterListener implements Listener {

    private final Set<Material> allowedCrops = Set.of(
            Material.WHEAT,
            Material.CARROTS,
            Material.POTATOES,
            Material.BEETROOTS,
            Material.NETHER_WART
    );

    @EventHandler
    public void onCropBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (!(block.getBlockData() instanceof Ageable)) return;
        Material cropType = block.getType();
        if (!allowedCrops.contains(cropType)) return;

        event.setCancelled(true); // Always cancel crop breaking for these

        if (!isHarvesterHoe(player.getInventory().getItemInMainHand())) {
            player.sendMessage(MessageUtils.color("&cPlease use your harvester hoe to harvest crops."));
            return;
        }

        Ageable age = (Ageable) block.getBlockData();
        if (age.getAge() < age.getMaximumAge()) {
            player.sendMessage(MessageUtils.color("&cThis crop is not fully grown."));
            return;
        }

        block.setType(Material.AIR);

        TideCore.getEconomy().depositPlayer(player, 2.50);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                new TextComponent("§x§a§4§f§a§a§6+$2.50"));

        int gainedXP = BoosterManager.getBoostedXP(5, player);
        PlayerDataManager.addXP(player, gainedXP);
        XPUtils.checkLevelUp(player);

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
        animateRegrowth(block, cropType, 5);
    }

    @EventHandler
    public void onCropTrampleByPlayer(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL &&
                event.getClickedBlock() != null &&
                event.getClickedBlock().getType() == Material.FARMLAND) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCropTrampleByEntity(EntityInteractEvent event) {
        if (event.getBlock().getType() == Material.FARMLAND) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCropFade(BlockFadeEvent event) {
        if (event.getBlock().getType() == Material.FARMLAND) {
            event.setCancelled(true);
        }
    }

    private boolean isHarvesterHoe(ItemStack item) {
        if (item == null || item.getType() != Material.WOODEN_HOE) return false;
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(
                TideCoreKeys.HARVESTER_KEY,
                PersistentDataType.INTEGER
        );
    }

    private void animateRegrowth(Block block, Material cropType, int seconds) {
        int maxAge = ((Ageable) Bukkit.createBlockData(cropType)).getMaximumAge();

        new BukkitRunnable() {
            int stage = 0;

            @Override
            public void run() {
                if (stage > maxAge) {
                    cancel();
                    return;
                }

                block.setType(cropType);
                Ageable ageable = (Ageable) block.getBlockData();
                ageable.setAge(stage);
                block.setBlockData(ageable);
                stage++;
            }
        }.runTaskTimer(TideCore.getInstance(), 20L, (seconds * 20L) / maxAge);
    }
}
