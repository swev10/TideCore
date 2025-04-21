package com.tideCore;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PearlsAdminSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "pearls";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 4) {
            sender.sendMessage(MessageUtils.prefix() + "§cUsage: /tidecore pearls <give|set|take> <player> <amount>");
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
            if (amount < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageUtils.prefix() + "§cInvalid number.");
            return;
        }

        switch (action) {
            case "give" -> {
                PlayerDataManager.addPearls(target, amount);
                sender.sendMessage(MessageUtils.prefix() + "§aGave §x§b§d§9§c§e§e" + amount + " Pearls §ato §f" + target.getName());
            }
            case "set" -> {
                PlayerDataManager.setPearls(target, amount);
                sender.sendMessage(MessageUtils.prefix() + "§aSet §f" + target.getName() + "'s §apearls to §x§b§d§9§c§e§e" + amount);
            }
            case "take" -> {
                PlayerDataManager.takePearls(target, amount);
                sender.sendMessage(MessageUtils.prefix() + "§aTook §x§b§d§9§c§e§e" + amount + " Pearls §afrom §f" + target.getName());
            }
            default -> sender.sendMessage(MessageUtils.prefix() + "§cUnknown action: " + action);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) return List.of("give", "set", "take");
        if (args.length == 3) return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        if (args.length == 4) return List.of("10", "100", "1000");
        return Collections.emptyList();
    }
}
