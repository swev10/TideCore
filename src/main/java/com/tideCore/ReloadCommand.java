package com.tideCore;

import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class ReloadCommand implements SubCommand {

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        TideCore.getInstance().reloadConfig();
        MessageUtils.reloadPrefix();
        sender.sendMessage(MessageUtils.prefix() + "§aTideCore config reloaded.");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
