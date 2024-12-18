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

        // Obtenemos el bonificador del arma
        ItemStack weapon = attacker.getInventory().getItemInMainHand();
        int attackBonus = getWeaponBonus(weapon, config);

        // Tirada de ataque
        int attackRoll = rollD20() + attackBonus;
        int targetArmor = getArmorValue(target);

        attacker.sendMessage("§eTirada de ataque: " + attackRoll + " vs Armadura: " + targetArmor);

        if (attackRoll == 1 + attackBonus) { // Fallo crítico (1 Natural)
            attacker.sendMessage("§c¡Fallo crítico! (Natural 1)");
            EffectUtils.playEffect(attacker, config, true, "Miss");
            event.setCancelled(true); // Cancela completamente el daño
            return;
        }

        if (attackRoll >= 20 + attackBonus) { // Golpe crítico (20 Natural)
            int damageRoll1 = getWeaponDamage(weapon, config);
            int damageRoll2 = getWeaponDamage(weapon, config);
            int totalDamage = damageRoll1 + damageRoll2;

            attacker.sendMessage("§a¡Golpe crítico! (Natural 20) Daño total: " + totalDamage);
            EffectUtils.playEffect(attacker, config, true, "CriticalHit");

            event.setDamage(totalDamage); // Aplicamos el daño total directamente
            return;
        }

        if (attackRoll >= targetArmor) { // Ataque exitoso normal
            int damageRoll = getWeaponDamage(weapon, config);
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

    private int getWeaponDamage(ItemStack weapon, FileConfiguration config) {
        String weaponKey = weapon.getType().toString().toLowerCase(); // Convierte el tipo del arma a string
        String baseKey = "weapons." + weaponKey;

        // Obtenemos el dado de daño y el bonificador desde config.yml
        int damageDice = config.getInt(baseKey + ".damageDice", config.getInt("weapons.default.damageDice", 2));
        int bonus = config.getInt(baseKey + ".bonus", config.getInt("weapons.default.bonus", 0));

        int damageRoll = rollDice(damageDice) + bonus;

        // Mensaje para depuración (puedes eliminarlo si no es necesario)
        System.out.println("Arma: " + weaponKey + ", Dado de daño: d" + damageDice + ", Bonificador: " + bonus + ", Daño Total: " + damageRoll);

        return damageRoll;
    }

    private int getWeaponBonus(ItemStack weapon, FileConfiguration config) {
        String weaponKey = weapon.getType().toString().toLowerCase();
        return config.getInt("weapons." + weaponKey + ".bonus", config.getInt("weapons.default.bonus", 0));
    }

    private int getArmorValue(LivingEntity entity) {
        var attribute = entity.getAttribute(Attribute.ARMOR);
        return attribute != null ? (int) attribute.getValue() : 0;
    }

    private int rollDice(int sides) {
        return random.nextInt(sides) + 1;
    }
}
