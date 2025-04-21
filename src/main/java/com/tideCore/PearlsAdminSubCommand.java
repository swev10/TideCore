package com.tideCore;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class PearlsAdminSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "pearls";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage(MessageUtils.color("&cUsage: /tidecore pearls <give|set|take> <player> <amount>"));
            return;
        }

        String action = args[1];
        Player target = Bukkit.getPlayerExact(args[2]);
        if (target == null) {
            sender.sendMessage(MessageUtils.color("&cPlayer not found."));
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageUtils.color("&cInvalid number input."));
            return;
        }

        switch (action.toLowerCase()) {
            case "give" -> PlayerDataManager.addPearls(target, amount);
            case "take" -> PlayerDataManager.takePearls(target, amount);
            case "set" -> PlayerDataManager.setPearls(target, amount);
            default -> {
                sender.sendMessage(MessageUtils.color("&cInvalid action. Use give, take, or set."));
                return;
            }
        }

        sender.sendMessage(MessageUtils.color("&aPearls updated for &e" + target.getName()));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) return List.of("give", "set", "take");
        if (args.length == 3) return null; // Let Bukkit auto-suggest players
        return Collections.emptyList();
    }
}
