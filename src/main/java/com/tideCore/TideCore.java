package com.tideCore;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class TideCore extends JavaPlugin {

    private static TideCore instance;
    private static Economy econ;

    private final String[] splash = {
            "_¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶",
            "_¶¶___________________________________¶¶",
            "_¶¶___________________________________¶¶",
            "__¶¶_________________________________¶¶_",
            "__¶¶_________________________________¶¶_",
            "___¶¶_______________________________¶¶__",
            "___¶¶______________________________¶¶___",
            "____¶¶¶__________________________¶¶¶____",
            "_____¶¶¶¶_¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶_¶¶¶¶_____",
            "_______¶¶¶¶_¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶_¶¶¶¶_______",
            "_________¶¶¶¶_¶¶¶¶¶¶¶¶¶¶¶¶_¶¶¶¶_________",
            "___________¶¶¶¶¶_¶¶¶¶¶¶¶_¶¶¶¶___________",
            "______________¶¶¶¶_¶¶¶_¶¶¶______________",
            "________________¶¶¶_¶_¶¶________________",
            "_________________¶¶¶_¶¶_________________",
            "__________________¶¶_¶¶_________________",
            "__________________¶¶_¶__________________",
            "__________________¶¶_¶¶_________________",
            "________________¶¶¶_¶_¶¶¶_______________",
            "_____________¶¶¶¶¶__¶__¶¶¶¶¶____________",
            "__________¶¶¶¶¶_____¶_____¶¶¶¶__________",
            "________¶¶¶¶________¶_______¶¶¶¶¶_______",
            "_______¶¶¶__________¶__________¶¶¶¶_____",
            "_____¶¶¶____________¶____________¶¶¶____",
            "____¶¶¶_____________¶______________¶¶___",
            "___¶¶¶______________¶_______________¶¶__",
            "___¶¶_______________¶________________¶¶_",
            "__¶¶________________¶________________¶¶_",
            "__¶¶_______________¶¶¶________________¶_",
            "__¶¶_¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶_¶¶",
            "__¶¶_¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶_¶¶",
            "__¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶"
    };

    @Override
    public void onLoad() {
        File playerDataFolder = new File(getDataFolder(), "players");
        if (!playerDataFolder.exists()) playerDataFolder.mkdirs();
    }

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig(); // Ensures plugin directory + config is created

        // ✅ Load Pearlshop.yml after files exist
        PearlShop.load();

        // Vault economy hook
        if (!setupEconomy()) {
            getLogger().severe("Vault not found! Disabling TideCore.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Commands
        TideCoreCommand commandHandler = new TideCoreCommand();
        getCommand("tidecore").setExecutor(commandHandler);
        getCommand("tidecore").setTabCompleter(commandHandler);
        getCommand("prestige").setExecutor(new PrestigeCommand());
        getCommand("tokens").setExecutor(new TokensCommand());
        getCommand("tokens").setTabCompleter(new TokensCommand());
        getCommand("pearls").setExecutor(new PearlsCommand());
        getCommand("pearls").setTabCompleter(new PearlsCommand());
        getCommand("pearlshop").setExecutor(new PearlShopCommand());
        getCommand("pearlshop").setTabCompleter(new PearlShopCommand());

        // Listeners
        Bukkit.getPluginManager().registerEvents(new HarvesterListener(), this);
        Bukkit.getPluginManager().registerEvents(new PrestigeGUI(), this);
        Bukkit.getPluginManager().registerEvents(new CropSelectorGUI(), this);
        Bukkit.getPluginManager().registerEvents(new BoosterRedeemListener(), this);
        Bukkit.getPluginManager().registerEvents(new PearlPayConfirmGUI(), this);
        Bukkit.getPluginManager().registerEvents(new PearlShop(), this);
        Bukkit.getPluginManager().registerEvents(new PearlShopSubmenu(), this);

        // PAPI placeholders
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new TidegenPlaceholders().register();
            getLogger().info("PlaceholderAPI hooked for %tidegen_*%");
        }

        printSplash("ENABLED");
    }

    @Override
    public void onDisable() {
        printSplash("DISABLED");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        econ = rsp.getProvider();
        return econ != null;
    }

    public static TideCore getInstance() {
        return instance;
    }

    public static Economy getEconomy() {
        return econ;
    }

    private void printSplash(String state) {
        getLogger().info("§b----=[ TIDECORE " + state + " ]=----");
        for (String line : splash) {
            getLogger().info(line);
        }
    }
}
