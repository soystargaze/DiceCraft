package com.erosmari.dicecraft.listeners;

import com.erosmari.dicecraft.config.ConfigHandler;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Random;

public class AttackListener implements Listener {
    private final Random random = new Random();

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        FileConfiguration config = ConfigHandler.getConfig();

        // Tirada D20
        int roll = random.nextInt(20) + 1;
        int playerBonus = config.getInt("playerBonus", 0);
        int attackRoll = roll + playerBonus;

        player.sendMessage("Tirada D20: " + roll + " (Total con bonus: " + attackRoll + ")");

        int targetArmor = target.getEquipment() != null ? target.getEquipment().getArmorContents().length * 2 : 10;

        if (roll == 1) {
            player.sendMessage("¡Fallo crítico! Natural 1.");
            target.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, target.getLocation(), 20);
            event.setCancelled(true);
        } else if (roll == 20) {
            player.sendMessage("¡Crítico! Natural 20.");
            event.setDamage(event.getDamage() * 2);
            target.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, target.getLocation(), 50);
        } else if (attackRoll >= targetArmor) {
            player.sendMessage("¡Golpe exitoso! Superaste la armadura del objetivo.");
        } else {
            player.sendMessage("¡Fallaste el golpe!");
            event.setCancelled(true);
        }
    }
}
