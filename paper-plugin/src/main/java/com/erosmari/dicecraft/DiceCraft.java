package com.erosmari.dicecraft;

import com.erosmari.dicecraft.config.ConfigHandler;
import com.erosmari.dicecraft.listeners.AttackListener;
import com.erosmari.dicecraft.listeners.MobAttackListener;
import org.bukkit.plugin.java.JavaPlugin;

public class DiceCraft extends JavaPlugin {

    @Override
    public void onEnable() {
        ConfigHandler.setup(this);
        getServer().getPluginManager().registerEvents(new AttackListener(), this);
        getServer().getPluginManager().registerEvents(new MobAttackListener(), this); // Nuevo Listener
        getLogger().info("Dicecraft ha sido activado correctamente.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Dicecraft se ha desactivado.");
    }
}
