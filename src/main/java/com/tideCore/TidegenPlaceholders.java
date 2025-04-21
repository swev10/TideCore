package com.tideCore;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.Locale;

public class TidegenPlaceholders extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "tidegen";
    }

    @Override
    public @NotNull String getAuthor() {
        return "TideCore";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) return "";

        return switch (identifier.toLowerCase()) {
            case "tokens"       -> String.valueOf(PlayerDataManager.getTokens(player));
            case "pearls"       -> String.valueOf(PlayerDataManager.getPearls(player));
            case "pearls_formatted" -> format(PlayerDataManager.getPearls(player));
            case "xp"           -> String.valueOf(PlayerDataManager.getXP(player));
            case "level"        -> String.valueOf(PlayerDataManager.getLevel(player));
            case "xpbar"        -> XPUtils.getProgressBar(
                    player,
                    PlayerDataManager.getXP(player),
                    PlayerDataManager.getLevel(player)
            );
            case "prestige"     -> String.valueOf(PlayerDataManager.getPrestige(player));
            default             -> null;
        };
    }

    private String format(int value) {
        return NumberFormat.getNumberInstance(Locale.US).format(value);
    }
}
