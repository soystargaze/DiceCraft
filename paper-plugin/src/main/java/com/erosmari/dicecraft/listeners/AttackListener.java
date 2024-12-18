package com.erosmari.dicecraft.listeners;

import com.erosmari.dicecraft.config.ConfigHandler;
import com.erosmari.dicecraft.utils.EffectUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class AttackListener implements Listener {

    private final Random random = new Random();

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return; // Solo jugadores
        if (!(event.getEntity() instanceof LivingEntity target)) return; // Solo entidades vivas

        FileConfiguration config = ConfigHandler.getConfig();
        int attackRoll = rollD20();
        int targetArmor = getArmorValue(target);

        attacker.sendMessage("§eTirada de ataque: " + attackRoll + " vs Armadura: " + targetArmor);

        if (attackRoll == 1) { // Fallo crítico (1 Natural)
            attacker.sendMessage("§c¡Fallo crítico! (Natural 1)");
            EffectUtils.playEffect(attacker, config, true, "Miss");
            event.setCancelled(true); // Cancela completamente el daño
            return;
        }

        if (attackRoll == 20) { // Golpe crítico (20 Natural)
            int damageRoll1 = getWeaponDamage(attacker.getInventory().getItemInMainHand());
            int damageRoll2 = getWeaponDamage(attacker.getInventory().getItemInMainHand());
            int totalDamage = damageRoll1 + damageRoll2;

            attacker.sendMessage("§a¡Golpe crítico! (Natural 20) Daño total: " + totalDamage);
            EffectUtils.playEffect(attacker, config, true, "CriticalHit");

            event.setDamage(totalDamage); // Aplicamos el daño total directamente
            return;
        }

        if (attackRoll >= targetArmor) { // Ataque exitoso normal
            int damageRoll = getWeaponDamage(attacker.getInventory().getItemInMainHand());
            attacker.sendMessage("§a¡Ataque exitoso! Daño infligido: " + damageRoll);

            event.setDamage(damageRoll); // Aplicamos el daño normal
        } else { // Fallo normal
            attacker.sendMessage("§c¡Ataque fallido! No superaste la armadura del objetivo.");
            EffectUtils.playEffect(attacker, config, true, "Miss");
            event.setCancelled(true); // Cancela el daño
        }
    }

    private int rollD20() {
        return random.nextInt(20) + 1;
    }

    private int getWeaponDamage(ItemStack weapon) {
        // Determinamos el dado basado en el arma equipada
        return switch (weapon.getType()) {
            case WOODEN_SWORD, GOLDEN_SWORD -> rollDice(4); // d4
            case STONE_SWORD -> rollDice(6); // d6
            case IRON_SWORD -> rollDice(8); // d8
            case DIAMOND_SWORD -> rollDice(10); // d10
            case NETHERITE_SWORD -> rollDice(12); // d12
            case BOW -> rollDice(6); // d6 para arco
            case CROSSBOW -> rollDice(8); // d8 para ballesta
            case TRIDENT -> rollDice(10); // d10 para tridente
            default -> rollDice(2); // Valor por defecto (d2) para armas no reconocidas o manos vacías
        };
    }

    private int getArmorValue(LivingEntity entity) {
        var attribute = entity.getAttribute(Attribute.ARMOR);
        return attribute != null ? (int) attribute.getValue() : 0;
    }

    private int rollDice(int sides) {
        return random.nextInt(sides) + 1;
    }
}
