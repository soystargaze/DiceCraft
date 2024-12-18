package com.erosmari.dicecraft.listeners;

import com.erosmari.dicecraft.config.ConfigHandler;
import com.erosmari.dicecraft.utils.EffectUtils;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Random;

public class MobAttackListener implements Listener {

    private final Random random = new Random();

    @EventHandler
    public void onMobAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof LivingEntity mob)) return; // Solo mobs
        if (!(event.getEntity() instanceof Player player)) return; // Solo jugadores como objetivo

        FileConfiguration config = ConfigHandler.getConfig();
        int attackRoll = rollD20();
        int playerArmor = getArmorValue(player);

        if (attackRoll == 1) { // Fallo crítico (1 Natural)
            player.sendMessage("§a¡El mob falló su ataque! (Natural 1)");
            EffectUtils.playEffect(mob, config, false, "Miss");
            event.setCancelled(true); // Cancela el daño
            return;
        }

        if (mob instanceof org.bukkit.entity.Warden) { // Caso especial: Warden
            double sonicBoomChance = config.getDouble("warden.sonicBoomChance", 0.5);
            if (random.nextDouble() < sonicBoomChance) {
                handleSonicBoom(player, config.getInt("wardenSonicBoomDamage", 15));
                event.setCancelled(true); // Cancela el ataque físico si se lanza Sonic Boom
                return;
            }
        }

        if (attackRoll == 20) { // Golpe crítico (20 Natural)
            handleCriticalHit(mob, player, config, event);
            return;
        }

        if (attackRoll >= playerArmor) { // Ataque exitoso normal
            handleNormalHit(mob, player, config, event);
        } else { // Fallo normal
            player.sendMessage("§a¡El mob falló su ataque!");
            EffectUtils.playEffect(mob, config, false, "Miss");
            event.setCancelled(true); // Cancela el daño
        }
    }

    private void handleCriticalHit(LivingEntity mob, Player player, FileConfiguration config, EntityDamageByEntityEvent event) {
        int damageRoll1 = getMobDamage(mob, config);
        int damageRoll2 = getMobDamage(mob, config);
        int totalDamage = damageRoll1 + damageRoll2;

        player.sendMessage("§c¡El mob te golpeó con un crítico! Daño total: " + totalDamage);
        EffectUtils.playEffect(mob, config, false, "CriticalHit");

        event.setDamage(totalDamage); // Aplicamos el daño crítico
    }

    private void handleNormalHit(LivingEntity mob, Player player, FileConfiguration config, EntityDamageByEntityEvent event) {
        int damageRoll = getMobDamage(mob, config);
        player.sendMessage("§c¡El mob te golpeó! Daño infligido: " + damageRoll);

        event.setDamage(damageRoll); // Aplicamos el daño normal
    }

    private void handleSonicBoom(Player player, int damage) {
        player.sendMessage("§b¡El Warden lanza su Sonic Boom e inflige " + damage + " de daño!");
        player.getWorld().spawnParticle(Particle.SONIC_BOOM, player.getLocation(), 1);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 1.0f, 1.0f);

        // Daño y retroceso
        player.damage(damage);
        player.setVelocity(player.getLocation().getDirection().multiply(-3).setY(1));
    }

    private int rollD20() {
        return random.nextInt(20) + 1;
    }

    private int getMobDamage(LivingEntity mob, FileConfiguration config) {
        if (mob instanceof org.bukkit.entity.Zombie) return rollDice(config.getInt("zombieDamageDice", 4));
        if (mob instanceof org.bukkit.entity.Skeleton) return rollDice(config.getInt("skeletonDamageDice", 6));
        if (mob instanceof org.bukkit.entity.Creeper) return rollDice(config.getInt("creeperDamageDice", 20));
        if (mob instanceof org.bukkit.entity.Warden) return rollDice(config.getInt("wardenMeleeDamageDice", 30));
        if (mob instanceof org.bukkit.entity.Spider) return rollDice(config.getInt("spiderDamageDice", 4));
        if (mob instanceof org.bukkit.entity.CaveSpider) return rollDice(config.getInt("caveSpiderDamageDice", 4));
        if (mob instanceof org.bukkit.entity.Enderman) return rollDice(config.getInt("endermanDamageDice", 6));
        if (mob instanceof org.bukkit.entity.Blaze) return rollDice(config.getInt("blazeDamageDice", 8));
        if (mob instanceof org.bukkit.entity.Silverfish) return rollDice(config.getInt("silverfishDamageDice", 2));
        if (mob instanceof org.bukkit.entity.Phantom) return rollDice(config.getInt("phantomDamageDice", 6));
        if (mob instanceof org.bukkit.entity.Piglin) return rollDice(config.getInt("piglinDamageDice", 6));
        if (mob instanceof org.bukkit.entity.PiglinBrute) return rollDice(config.getInt("piglinBruteDamageDice", 12));
        if (mob instanceof org.bukkit.entity.Hoglin) return rollDice(config.getInt("hoglinDamageDice", 8));
        if (mob instanceof org.bukkit.entity.Zoglin) return rollDice(config.getInt("zoglinDamageDice", 10));
        if (mob instanceof org.bukkit.entity.WitherSkeleton) return rollDice(config.getInt("witherSkeletonDamageDice", 8));
        if (mob instanceof org.bukkit.entity.Evoker) return rollDice(config.getInt("evokerDamageDice", 8));
        if (mob instanceof org.bukkit.entity.Vindicator) return rollDice(config.getInt("vindicatorDamageDice", 8));
        if (mob instanceof org.bukkit.entity.Ravager) return rollDice(config.getInt("ravagerDamageDice", 20));
        if (mob instanceof org.bukkit.entity.Ghast) return rollDice(config.getInt("ghastDamageDice", 10));
        if (mob instanceof org.bukkit.entity.Shulker) return rollDice(config.getInt("shulkerDamageDice", 4));
        if (mob instanceof org.bukkit.entity.Slime slime) {
            if (slime.getSize() == 1) return rollDice(config.getInt("slimeSmallDamageDice", 2));
            if (slime.getSize() == 2) return rollDice(config.getInt("slimeMediumDamageDice", 4));
            return rollDice(config.getInt("slimeLargeDamageDice", 6));
        }
        if (mob instanceof org.bukkit.entity.MagmaCube magma) {
            if (magma.getSize() == 1) return rollDice(config.getInt("magmaCubeSmallDamageDice", 2));
            if (magma.getSize() == 2) return rollDice(config.getInt("magmaCubeMediumDamageDice", 4));
            return rollDice(config.getInt("magmaCubeLargeDamageDice", 6));
        }
        return rollDice(config.getInt("defaultMobDamageDice", 2)); // Daño predeterminado
    }

    private int getArmorValue(LivingEntity entity) {
        var attribute = entity.getAttribute(Attribute.ARMOR);
        return attribute != null ? (int) attribute.getValue() : 0;
    }

    private int rollDice(int sides) {
        return random.nextInt(sides) + 1;
    }
}
