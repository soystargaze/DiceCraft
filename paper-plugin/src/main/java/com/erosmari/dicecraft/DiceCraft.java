package com.erosmari.dicecraft;

import com.erosmari.dicecraft.config.ConfigHandler;
import com.erosmari.dicecraft.listeners.AttackListener;
import org.bukkit.plugin.java.JavaPlugin;

public class DiceCraft extends JavaPlugin {

    @Override
    public void onEnable() {
        // Cargar configuración
        ConfigHandler.setup(this);
        getLogger().info("Dicecraft ha sido activado correctamente.");

        // Registrar eventos
        getServer().getPluginManager().registerEvents(new AttackListener(), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Dicecraft se ha desactivado.");
    }
}
