package com.erosmari.dicecraft.listeners;

import com.erosmari.dicecraft.config.ConfigHandler;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.Random;

public class MobAttackListener implements Listener {

    private final Random random = new Random();

    @EventHandler
    public void onMobAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof LivingEntity mob)) return;
        if (!(event.getEntity() instanceof Player player)) return;

        FileConfiguration config = ConfigHandler.getConfig();
        int baseRoll = rollD20();
        int damage;

        if (mob instanceof Warden) {
            if (random.nextBoolean()) { // 50% de probabilidad de lanzar Sonic Boom
                simulateSonicBoom(player, config.getInt("wardenSonicBoomDamage", 15));
                return; // Finaliza el evento para evitar daño normal
            }
            damage = config.getInt("wardenMeleeDamage", 30);
        } else {
            damage = getMobDamage(mob, config);
        }

        // Fallo crítico o golpe crítico
        if (baseRoll == 1) {
            player.sendMessage("§c¡El ataque del mob ha fallado! (Natural 1)");
            mob.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, mob.getLocation(), 20);
            event.setDamage(0);
        } else if (baseRoll == 20) {
            player.sendMessage("§a¡Golpe crítico del mob! (Natural 20)");
            mob.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, player.getLocation(), 50);
            event.setDamage(damage * 2);
        } else {
            player.sendMessage("§e¡El mob te golpeó con " + damage + " puntos de daño!");
            event.setDamage(damage);
        }
    }

    private void simulateSonicBoom(Player player, int damage) {
        player.sendMessage("§b¡El Warden lanza su Sonic Boom!");

        // Reproducir sonido
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 1.0f, 1.0f);

        // Generar partículas
        player.getWorld().spawnParticle(Particle.SONIC_BOOM, player.getLocation(), 1);

        // Aplicar daño
        player.damage(damage);

        // Desplazar al jugador hacia atrás
        Vector knockback = player.getLocation().toVector()
                .subtract(player.getWorld().getSpawnLocation().toVector())
                .normalize().multiply(-2);
        player.setVelocity(knockback);
    }

    private int rollD20() {
        return random.nextInt(20) + 1; // Tirada D20
    }

    private int getMobDamage(LivingEntity mob, FileConfiguration config) {
        if (mob instanceof Zombie) return config.getInt("zombieDamage", 4);
        if (mob instanceof Skeleton) return config.getInt("skeletonDamage", 6);
        if (mob instanceof Creeper) return config.getInt("creeperDamage", 20);
        if (mob instanceof Warden) return config.getInt("wardenMeleeDamage", 30);
        return 2;
    }
}
