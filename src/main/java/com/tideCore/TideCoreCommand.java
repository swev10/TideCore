package com.tideCore;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;

public class TideCoreCommand implements CommandExecutor, TabCompleter {

    private final Map<String, SubCommand> subcommands = new HashMap<>();

    public TideCoreCommand() {
        registerSubCommand(new HelpCommand());
        registerSubCommand(new ReloadCommand());
        registerSubCommand(new GiveCommand());
        registerSubCommand(new SelectCropCommand());
        registerSubCommand(new ReloadCommand());
        registerSubCommand(new TokensAdminCommand());
        registerSubCommand(new PearlsAdminSubCommand());
    }

    private void registerSubCommand(SubCommand cmd) {
        subcommands.put(cmd.getName().toLowerCase(), cmd);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("tidecore.admin")) {
            sender.sendMessage(MessageUtils.color("&cYou don't have permission to use this command."));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(MessageUtils.color("&eUse /tidecore help"));
            return true;
        }

        SubCommand cmd = subcommands.get(args[0].toLowerCase());
        if (cmd != null) {
            cmd.execute(sender, args);
        } else {
            sender.sendMessage(MessageUtils.color("&cUnknown subcommand. Use &e/tidecore help"));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player) || !sender.hasPermission("tidecore.admin")) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            return subcommands.keySet().stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }

        SubCommand cmd = subcommands.get(args[0].toLowerCase());
        if (cmd != null) {
            return cmd.tabComplete(sender, args);
        }

        return Collections.emptyList();
    }
}
