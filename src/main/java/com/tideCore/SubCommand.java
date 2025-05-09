package com.tideCore;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommand {
    String getName();
    void execute(CommandSender sender, String[] args);
    List<String> tabComplete(CommandSender sender, String[] args);
}
