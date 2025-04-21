package com.tideCore;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class YamlFileUtils {
    public static int getValue(File file, String path, int def) {
        return YamlConfiguration.loadConfiguration(file).getInt(path, def);
    }
}
