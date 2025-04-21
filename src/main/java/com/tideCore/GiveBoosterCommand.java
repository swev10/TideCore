package com.tideCore;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class GiveBoosterCommand implements SubCommand {

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 5 || !args[1].equalsIgnoreCase("booster")) {
            sender.sendMessage(MessageUtils.color("&cUsage: /tidecore give booster <minutes> <multiplier> <player>"));
            return;
        }

        int minutes;
        double multiplier;

        try {
            minutes = Integer.parseInt(args[2]);
            multiplier = Double.parseDouble(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageUtils.color("&cInvalid number."));
            return;
        }

        Player target = Bukkit.getPlayerExact(args[4]);
        if (target == null) {
            sender.sendMessage(MessageUtils.color("&cPlayer not found: " + args[4]));
            return;
        }

        BoosterManager.applyBooster(target, minutes, multiplier);
        target.getInventory().addItem(BoosterFactory.createBooster(multiplier, minutes));
        sender.sendMessage(MessageUtils.color("&aGave a &e" + multiplier + "x booster &afor &e" + minutes + " min to &b" + target.getName()));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) return List.of("booster");
        if (args.length == 5) return null;
        return Collections.emptyList();
    }
}
