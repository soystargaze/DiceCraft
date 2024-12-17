package com.erosmari.dicecraft.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class ConfigHandler {
    private static FileConfiguration config; // Campo global necesario

    public static void setup(JavaPlugin plugin) {
        File configFile = new File(plugin.getDataFolder(), "config.yml"); // Variable local

        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public static FileConfiguration getConfig() {
        return config;
    }
}
