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
    private final AttackListener attackListener = new AttackListener();

    @EventHandler
    public void onMobAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof LivingEntity mob)) return; // El atacante debe ser un mob
        if (!(event.getEntity() instanceof Player player)) return; // El objetivo debe ser un jugador

        // Obtener daño del mob y manejar ataques especiales
        FileConfiguration config = ConfigHandler.getConfig();
        int mobDamage = getMobDamage(mob, config);

        if (mob instanceof Warden && random.nextDouble() < 0.5) { // 50% de probabilidad de Sonic Boom
            handleSonicBoom(player, config.getInt("wardenSonicBoomDamage", 15));
            return; // Finaliza aquí para evitar el daño normal del Warden
        }

        // Reutilizar lógica centralizada de ataques
        attackListener.handleAttack(player, mob, "mob", mobDamage);
    }

    private int getMobDamage(LivingEntity mob, FileConfiguration config) {
        // Configuración del daño para cada tipo de mob hostil
        if (mob instanceof Zombie) return config.getInt("zombieDamage", 4);
        if (mob instanceof Skeleton) return config.getInt("skeletonDamage", 6);
        if (mob instanceof Creeper) return config.getInt("creeperDamage", 20);
        if (mob instanceof Enderman) return config.getInt("endermanDamage", 6);
        if (mob instanceof Spider) return config.getInt("spiderDamage", 4);
        if (mob instanceof CaveSpider) return config.getInt("caveSpiderDamage", 4);
        if (mob instanceof Blaze) return config.getInt("blazeDamage", 4);
        if (mob instanceof Witch) return config.getInt("witchDamage", 6);
        if (mob instanceof Warden) return config.getInt("wardenMeleeDamage", 30);
        if (mob instanceof Ghast) return config.getInt("ghastDamage", 10);
        if (mob instanceof Piglin) return config.getInt("piglinDamage", 6);
        if (mob instanceof PigZombie) return config.getInt("piglinDamage", 6);
        if (mob instanceof PiglinBrute) return config.getInt("piglinBruteDamage", 12);
        if (mob instanceof Hoglin) return config.getInt("hoglinDamage", 8);
        if (mob instanceof Zoglin) return config.getInt("zoglinDamage", 8);
        if (mob instanceof Vindicator) return config.getInt("vindicatorDamage", 12);
        if (mob instanceof Evoker) return config.getInt("evokerDamage", 8);
        if (mob instanceof Pillager) return config.getInt("pillagerDamage", 6);
        if (mob instanceof Ravager) return config.getInt("ravagerDamage", 20);
        if (mob instanceof Shulker) return config.getInt("shulkerDamage", 4);
        if (mob instanceof MagmaCube) return config.getInt("magmaCubeDamage", 4);
        if (mob instanceof Slime) return config.getInt("slimeDamage", 4);
        if (mob instanceof WitherSkeleton) return config.getInt("witherSkeletonDamage", 8);
        if (mob instanceof Phantom) return config.getInt("phantomDamage", 4);
        if (mob instanceof Guardian) return config.getInt("guardianDamage", 4);
        if (mob instanceof ElderGuardian) return config.getInt("elderGuardianDamage", 8);
        if (mob instanceof Drowned) return config.getInt("drownedDamage", 6);
        if (mob instanceof Stray) return config.getInt("strayDamage", 6);
        if (mob instanceof Husk) return config.getInt("huskDamage", 6);
        if (mob instanceof Vex) return config.getInt("vexDamage", 6);
        if (mob instanceof Endermite) return config.getInt("endermiteDamage", 2);
        if (mob instanceof Silverfish) return config.getInt("silverfishDamage", 2);
        return config.getInt("defaultMobDamage", 2); // Daño por defecto
    }

    private void handleSonicBoom(Player player, int damage) {
        player.sendMessage("§b¡El Warden lanza su Sonic Boom!");

        // Partículas y sonido del Sonic Boom
        player.getWorld().spawnParticle(Particle.SONIC_BOOM, player.getLocation(), 1);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 1.0f, 1.0f);

        // Aplicar daño y empuje al jugador
        player.damage(damage);
        Vector knockback = player.getLocation().getDirection().multiply(-3).setY(1);
        player.setVelocity(knockback);
    }
}
