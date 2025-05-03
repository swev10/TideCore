package com.tideCore;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class TokensAdminCommand implements SubCommand {

    @Override
    public String getName() {
        return "tokens";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 4) {
            sender.sendMessage(MessageUtils.prefix() + "§cUsage: /tidecore tokens <give|take|set> <player> <amount>");
            return;
        }

        String action = args[1].toLowerCase();
        Player target = Bukkit.getPlayerExact(args[2]);

        if (target == null) {
            sender.sendMessage(MessageUtils.prefix() + "§cPlayer not found.");
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageUtils.prefix() + "§cInvalid number: " + args[3]);
            return;
        }

        switch (action) {
            case "give" -> PlayerDataManager.addTokens(target, amount);
            case "take" -> PlayerDataManager.takeTokens(target, amount);
            case "set" -> PlayerDataManager.setTokens(target, amount);
            default -> {
                sender.sendMessage(MessageUtils.prefix() + "§cUnknown action: " + action);
                return;
            }
        }

        sender.sendMessage(MessageUtils.prefix() + "§aUpdated tokens for §e" + target.getName() + "§a successfully.");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) return List.of("give", "take", "set");
        if (args.length == 3) return null;
        return Collections.emptyList();
    }
}
