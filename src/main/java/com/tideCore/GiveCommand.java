package com.tideCore;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class GiveCommand implements SubCommand {

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(MessageUtils.color("&cUsage: /tidecore give <harvesterhoe|booster> ..."));
            return;
        }

        if (args[1].equalsIgnoreCase("harvesterhoe")) {
            if (args.length != 3) {
                sender.sendMessage(MessageUtils.color("&cUsage: /tidecore give harvesterhoe <player>"));
                return;
            }

            Player target = Bukkit.getPlayerExact(args[2]);
            if (target == null) {
                sender.sendMessage(MessageUtils.color("&cPlayer not found."));
                return;
            }

            ItemStack hoe = HarvesterFactory.createHarvesterHoe();
            target.getInventory().addItem(hoe);
            sender.sendMessage(MessageUtils.color("&aGave a harvester hoe to &e" + target.getName()));
            return;
        }

        if (args[1].equalsIgnoreCase("booster")) {
            if (args.length != 5) {
                sender.sendMessage(MessageUtils.color("&cUsage: /tidecore give booster <minutes> <multiplier> <player>"));
                return;
            }

            int minutes;
            double multiplier;

            try {
                minutes = Integer.parseInt(args[2]);
                multiplier = Double.parseDouble(args[3]);
            } catch (NumberFormatException e) {
                sender.sendMessage(MessageUtils.color("&cInvalid number input."));
                return;
            }

            Player target = Bukkit.getPlayerExact(args[4]);
            if (target == null) {
                sender.sendMessage(MessageUtils.color("&cPlayer not found."));
                return;
            }

            ItemStack booster = BoosterFactory.createBooster(multiplier, minutes);
            target.getInventory().addItem(booster);
            sender.sendMessage(MessageUtils.color("&aGave a &e" + multiplier + "x &abooster for &e" + minutes + " minutes &ato &e" + target.getName()));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) return List.of("harvesterhoe", "booster");
        if (args.length == 3 && args[1].equalsIgnoreCase("harvesterhoe")) {
            return null; // suggest online players
        }
        if (args.length == 5 && args[1].equalsIgnoreCase("booster")) {
            return null; // suggest online players
        }
        return Collections.emptyList();
    }
}
