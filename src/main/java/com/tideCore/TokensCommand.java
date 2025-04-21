package com.tideCore;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class TokensCommand implements CommandExecutor, TabCompleter {

    private static final int ENTRIES_PER_PAGE = 10;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command is player-only.");
            return true;
        }

        if (!sender.hasPermission("tidecore.tokens")) {
            sender.sendMessage(MessageUtils.prefix() + "§cYou don't have permission to use this.");
            return true;
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("leaderboard")) {
            int page = 1;
            if (args.length == 2) {
                try {
                    page = Integer.parseInt(args[1]);
                    if (page <= 0) page = 1;
                } catch (NumberFormatException ignored) {}
            }
            showLeaderboard(player, page);
            return true;
        }

        // Default: /tokens see (player)
        if (args.length == 2 && args[0].equalsIgnoreCase("see")) {
            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                sender.sendMessage(MessageUtils.prefix() + "§cPlayer not found.");
                return true;
            }

            int tokens = PlayerDataManager.getTokens(target);
            sender.sendMessage(MessageUtils.prefix() + "§e" + target.getName() + "§f has §b" + tokens + " tokens§f.");
            return true;
        }

        // Help
        sender.sendMessage(MessageUtils.prefix() + "§7Usage: /tokens see <player> or /tokens leaderboard [page]");
        return true;
    }

    private void showLeaderboard(Player player, int page) {
        File folder = new File(TideCore.getInstance().getDataFolder(), "players");
        if (!folder.exists() || !folder.isDirectory()) {
            player.sendMessage(MessageUtils.prefix() + "§cNo data found.");
            return;
        }

        List<Map.Entry<String, Integer>> sorted = Arrays.stream(Objects.requireNonNull(folder.listFiles()))
                .filter(file -> file.getName().endsWith(".yml"))
                .map(file -> {
                    String uuidStr = file.getName().replace(".yml", "");
                    OfflinePlayer offline = Bukkit.getOfflinePlayer(UUID.fromString(uuidStr));
                    int tokens = YamlFileUtils.getValue(file, "tokens", 0);
                    return Map.entry(offline.getName(), tokens);
                })
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .collect(Collectors.toList());

        int totalPages = (int) Math.ceil((double) sorted.size() / ENTRIES_PER_PAGE);
        if (page > totalPages) page = totalPages;

        int start = (page - 1) * ENTRIES_PER_PAGE;
        int end = Math.min(start + ENTRIES_PER_PAGE, sorted.size());

        player.sendMessage("§x§f§1§d§3§6§e§lTop Token Holders §7(Page " + page + "/" + totalPages + ")");
        for (int i = start; i < end; i++) {
            Map.Entry<String, Integer> entry = sorted.get(i);
            int position = i + 1;

            player.sendMessage(
                    "§x§f§1§d§3§6§e#" + position + " §7▸ §f" + entry.getKey()
                            + " §8- §x§f§1§d§3§6§e" + entry.getValue() + " tokens"
            );
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return List.of("see", "leaderboard");
        if (args.length == 2 && args[0].equalsIgnoreCase("see")) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }
        return List.of();
    }
}
