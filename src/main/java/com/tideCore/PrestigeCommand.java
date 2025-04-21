package com.tideCore;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PrestigeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command is only for players.");
            return true;
        }

        int currentPrestige = PlayerDataManager.getPrestige(player);
        int level = PlayerDataManager.getLevel(player);
        int requiredLevel = 10 + (currentPrestige * 5);
        double requiredMoney = PrestigeManager.getPrestigeCost(currentPrestige);

        if (!PrestigeManager.canPrestige(player)) {
            player.sendMessage(MessageUtils.prefix() + "§cYou must be level §e" + requiredLevel +
                    " §cand have §a$" + String.format("%,.2f", requiredMoney) + " §cto prestige.");
            return true;
        }

        PrestigeManager.prestige(player);
        return true;
    }
}
