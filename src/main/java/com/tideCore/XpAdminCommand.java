package com.tideCore;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class XpAdminCommand implements SubCommand {

    @Override
    public String getName() {
        return "xp";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 4) {
            sender.sendMessage(MessageUtils.prefix() + "§cUsage: /tidecore xp <give|take> <player> <amount>");
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
            sender.sendMessage(MessageUtils.prefix() + "§cInvalid number.");
            return;
        }

        switch (action) {
            case "give" -> {
                PlayerDataManager.addXP(target, amount);
                XPUtils.checkLevelUp(target);
                sender.sendMessage(MessageUtils.prefix() + "§aGave §b" + amount + " XP §ato §e" + target.getName());
            }
            case "take" -> {
                PlayerDataManager.setXP(target, Math.max(0, PlayerDataManager.getXP(target) - amount));
                sender.sendMessage(MessageUtils.prefix() + "§cTook §b" + amount + " XP §cfrom §e" + target.getName());
            }
            default -> sender.sendMessage(MessageUtils.prefix() + "§cInvalid action: give | take");
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) return List.of("give", "take");
        if (args.length == 3) return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        return Collections.emptyList();
    }
}
