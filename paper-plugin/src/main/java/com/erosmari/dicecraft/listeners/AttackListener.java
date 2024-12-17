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


    public void handleAttack(Player attacker, LivingEntity target, String prefix, int baseDamage) {
        FileConfiguration config = ConfigHandler.getConfig();
        int attackRoll = rollD20();
        int targetArmor = getArmorValue(target);

        attacker.sendMessage("§eTirada de ataque: " + attackRoll + " vs Armadura: " + targetArmor);

        if (attackRoll == 1) { // Fallo crítico (1 Natural)
            attacker.sendMessage("§c¡Fallo crítico! (Natural 1)");
            playEffect(attacker, config, prefix + "MissParticles", prefix + "MissSound");
            return; // Salimos sin aplicar daño
        }

        if (attackRoll == 20) { // Golpe crítico (20 Natural)
            int damageRoll1 = rollDice(baseDamage);
            int damageRoll2 = rollDice(baseDamage);
            int totalDamage = damageRoll1 + damageRoll2;

            attacker.sendMessage("§a¡Golpe crítico! (Natural 20) Daño total: " + totalDamage);
            playEffect(attacker, config, prefix + "CriticalHitParticles", prefix + "CriticalHitSound");

            target.setNoDamageTicks(0);
            target.setHealth(Math.max(0, target.getHealth() - totalDamage));
            return; // Terminamos aquí ya que el golpe es automático
        }

        if (attackRoll >= targetArmor) { // Ataque exitoso normal
            int damageRoll = rollDice(baseDamage);
            attacker.sendMessage("§a¡Ataque exitoso! Daño infligido: " + damageRoll);

            target.setNoDamageTicks(0);
            target.setHealth(Math.max(0, target.getHealth() - damageRoll));
        } else { // Fallo normal
            attacker.sendMessage("§c¡Ataque fallido! No superaste la armadura del objetivo.");
            playEffect(attacker, config, prefix + "MissParticles", prefix + "MissSound");
        }
    }



    private int getArmorValue(LivingEntity entity) {
        if (entity != null) {
            var attribute = entity.getAttribute(org.bukkit.attribute.Attribute.ARMOR);
            if (attribute != null) { // Verificar si el atributo existe
                return (int) attribute.getValue(); // Devolver el valor del atributo
            }
        }
        return 0; // Si no existe el atributo, devolver 0
    }


    private int rollD20() {
        return random.nextInt(20) + 1;
    }

    private int rollDice(int sides) {
        return random.nextInt(sides) + 1;
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
