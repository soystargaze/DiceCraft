package com.erosmari.dicecraft.listeners;

import com.erosmari.dicecraft.config.ConfigHandler;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.NamespacedKey;

import java.util.Random;

public class AttackListener implements Listener {

    private final Random random = new Random();

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        // Calcular el daño del arma equipada
        int damage = getWeaponDamage(player.getInventory().getItemInMainHand(), ConfigHandler.getConfig());

        // Llamar al metodo handleAttack con 4 argumentos
        handleAttack(player, target, "player", damage);
    }


    public void handleAttack(Player attacker, LivingEntity target, String prefix, int damage) {
        FileConfiguration config = ConfigHandler.getConfig();
        int baseRoll = rollD20();

        // Si el atacante es un jugador y el daño no se pasó como argumento, calcula el daño del arma
        if (prefix.equals("player")) {
            damage = getWeaponDamage(attacker.getInventory().getItemInMainHand(), config);
        }

        if (baseRoll == 1) { // Fallo crítico
            attacker.sendMessage("§c¡Fallo crítico! (Natural 1)");
            playEffect(attacker, config, prefix + "MissParticles", prefix + "MissSound");
            target.damage(0);
        } else if (baseRoll == 20) { // Golpe crítico
            attacker.sendMessage("§a¡Golpe crítico! (Natural 20)");
            playEffect(attacker, config, prefix + "CriticalHitParticles", prefix + "CriticalHitSound");
            target.damage(damage * 2);
        } else {
            attacker.sendMessage("§e¡Golpe exitoso! Tirada: " + baseRoll + ", Daño: " + damage);
            target.damage(damage);
        }
    }




    private int rollD20() {
        return random.nextInt(20) + 1;
    }

    private int getWeaponDamage(ItemStack weapon, FileConfiguration config) {
        return switch (weapon.getType().toString()) {
            case "WOODEN_SWORD" -> config.getInt("woodenSwordDamage", 4);
            case "STONE_SWORD" -> config.getInt("stoneSwordDamage", 6);
            case "IRON_SWORD" -> config.getInt("ironSwordDamage", 8);
            case "DIAMOND_SWORD" -> config.getInt("diamondSwordDamage", 10);
            case "NETHERITE_SWORD" -> config.getInt("netheriteSwordDamage", 12);
            case "BOW" -> config.getInt("bowDamage", 6);
            case "CROSSBOW" -> config.getInt("crossbowDamage", 8);
            case "TRIDENT" -> config.getInt("tridentDamage", 10);
            default -> config.getInt("defaultDamage", 2);
        };
    }

    private void playEffect(Player player, FileConfiguration config, String particleKey, String soundKey) {
        String particleName = config.getString("effects." + particleKey, "crit");
        String soundName = config.getString("effects." + soundKey, "entity.player.levelup");

        try {
            Particle particle = Registry.PARTICLE_TYPE.get(NamespacedKey.minecraft(particleName.toLowerCase()));
            if (particle != null) {
                player.getWorld().spawnParticle(particle, player.getLocation(), 30);
            }
        } catch (IllegalArgumentException ignored) {}

        try {
            Sound sound = Registry.SOUNDS.get(NamespacedKey.minecraft(soundName.toLowerCase()));
            if (sound != null) {
                player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
            }
        } catch (IllegalArgumentException ignored) {}
    }
}
