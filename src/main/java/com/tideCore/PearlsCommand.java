package com.tideCore;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;

public class PearlsCommand implements CommandExecutor, TabCompleter {

    private static final Set<UUID> payCooldown = new HashSet<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players may use this command.");
            return true;
        }

        if (!player.hasPermission("tidecore.player")) {
            player.sendMessage(MessageUtils.prefix() + "§cYou lack permission to use pearls.");
            return true;
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("see")) {
            if (args.length != 2) {
                player.sendMessage(MessageUtils.prefix() + "§7Usage: /pearls see <player>");
                return true;
            }

            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                player.sendMessage(MessageUtils.prefix() + "§cPlayer not found.");
                return true;
            }

            int pearls = PlayerDataManager.getPearls(target);
            player.sendMessage("§x§b§d§9§c§e§e" + target.getName() + " §7has §f" + pearls + " Pearls.");
            return true;
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("pay")) {
            if (args.length != 3) {
                player.sendMessage(MessageUtils.prefix() + "§7Usage: /pearls pay <player> <amount>");
                return true;
            }

            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null || target == player) {
                player.sendMessage(MessageUtils.prefix() + "§cInvalid player.");
                return true;
            }

            int amount;
            try {
                amount = Integer.parseInt(args[2]);
                if (amount <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                player.sendMessage(MessageUtils.prefix() + "§cInvalid amount.");
                return true;
            }

            if (PlayerDataManager.getPearls(player) < amount) {
                player.sendMessage(MessageUtils.prefix() + "§cYou don’t have enough pearls.");
                return true;
            }

            if (payCooldown.contains(player.getUniqueId())) {
                player.sendMessage(MessageUtils.prefix() + "§cYou must wait before sending pearls again.");
                return true;
            }

            payCooldown.add(player.getUniqueId());
            Bukkit.getScheduler().runTaskLater(TideCore.getInstance(), () ->
                    payCooldown.remove(player.getUniqueId()), 20L * 60); // 1 minute

            PearlPayConfirmGUI.open(player, target, amount);
            return true;
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("leaderboard")) {
            int page = 1;
            if (args.length == 2) {
                try {
                    page = Integer.parseInt(args[1]);
                    if (page < 1) page = 1;
                } catch (NumberFormatException ignored) {}
            }
            PearlsLeaderboardCommand.show(player, page);
            return true;
        }

        // Help
        player.sendMessage(MessageUtils.prefix() + "§7/pearls see <player>");
        player.sendMessage(MessageUtils.prefix() + "§7/pearls pay <player> <amount>");
        player.sendMessage(MessageUtils.prefix() + "§7/pearls leaderboard [page]");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            return List.of("see", "pay", "leaderboard");
        }

        if (args.length == 2 && (args[0].equalsIgnoreCase("see") || args[0].equalsIgnoreCase("pay"))) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("pay")) {
            return List.of("10", "100", "1000");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("leaderboard")) {
            return List.of("1", "2", "3");
        }

        return Collections.emptyList();
    }
}
