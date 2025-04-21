package com.tideCore;

import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class HelpCommand implements SubCommand {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(MessageUtils.color("&bTideCore Help:"));
        sender.sendMessage(MessageUtils.color("&e/tidecore help &7- Show this help menu"));
        sender.sendMessage(MessageUtils.color("§e/tidecore reload §7- Reload the plugin"));
        sender.sendMessage(MessageUtils.color("§e/tidecore give harvesterhoe (player) §7- Gives player's a harvester hoe"));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
