package com.erosmari.dicecraft;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.random.Random;

public class Dicecraft implements ModInitializer {

    @Override
    public void onInitialize() {
        System.out.println("RollD20 Mod initialized!");

        // Registro del evento de ataque del jugador
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (entity instanceof LivingEntity target) {
                boolean hitSuccessful = performAttack(player, target);
                return hitSuccessful ? ActionResult.SUCCESS : ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        // Registro del evento cuando un mob ataca
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (source.getAttacker() instanceof LivingEntity attacker && entity instanceof PlayerEntity targetPlayer) {
                return performMobAttack(attacker, targetPlayer);
            }
            return true; // Permite el daño si no cumple las condiciones
        });
    }

    public boolean performAttack(PlayerEntity attacker, LivingEntity target) {
        int attackRoll = rollD20();
        int targetArmorValue = target.getArmor();

        System.out.println("Attack Roll: " + attackRoll);
        System.out.println("Target Armor: " + targetArmorValue);

        if (attackRoll == 1) {
            System.out.println("Natural 1! Attack automatically misses.");
            return false; // Falla automática
        }

        if (attackRoll >= targetArmorValue) {
            boolean isCritical = attackRoll == 20;
            performDamageRoll(attacker, target, attacker.getStackInHand(Hand.MAIN_HAND), isCritical);
            return true; // Hit successful
        } else {
            System.out.println("Attack missed!");
            return false; // Hit missed
        }
    }

    public boolean performMobAttack(LivingEntity attacker, PlayerEntity target) {
        int attackRoll = rollD20(); // Los mobs no tienen bonificadores
        int targetArmorValue = target.getArmor();

        System.out.println("Mob Attack Roll: " + attackRoll);
        System.out.println("Player Armor: " + targetArmorValue);

        if (attackRoll == 1) {
            System.out.println("Natural 1! Mob attack automatically misses.");
            return false; // Falla automática
        }

        if (attackRoll >= targetArmorValue) {
            boolean isCritical = attackRoll == 20;
            performDamageRoll(attacker, target, attacker.getMainHandStack(), isCritical);
            return true; // Hit successful
        } else {
            System.out.println("Mob Attack missed!");
            return false; // Hit missed
        }
    }

    private int rollD20() {
        return Random.create().nextInt(20) + 1;
    }

    private void performDamageRoll(LivingEntity attacker, LivingEntity target, ItemStack weapon, boolean isCritical) {
        int damageRoll = getDamageBasedOnWeapon(weapon);

        if (isCritical) {
            System.out.println("Natural 20! Critical hit, rolling damage twice.");
            int criticalDamage = getDamageBasedOnWeapon(weapon); // Segunda tirada de daño
            damageRoll += criticalDamage; // Sumar ambos daños
            System.out.println("Critical Damage Roll: " + criticalDamage);
        }

        System.out.println("Damage Roll: " + damageRoll);

        if (attacker.getWorld() instanceof ServerWorld serverWorld) {
            if (attacker instanceof PlayerEntity player) {
                target.damage(serverWorld, player.getDamageSources().playerAttack(player), (float) damageRoll);
            } else {
                target.damage(serverWorld, attacker.getDamageSources().mobAttack(attacker), (float) damageRoll);
            }
        }
    }

    private int getDamageBasedOnWeapon(ItemStack weapon) {
        if (weapon.isEmpty()) {
            return rollDice(4); // Ataque sin armas (básico d4)
        } else if (weapon.isOf(Items.WOODEN_SWORD)) {
            return rollDice(4);
        } else if (weapon.isOf(Items.STONE_SWORD)) {
            return rollDice(6);
        } else if (weapon.isOf(Items.IRON_SWORD)) {
            return rollDice(8);
        } else if (weapon.isOf(Items.DIAMOND_SWORD)) {
            return rollDice(10);
        } else if (weapon.isOf(Items.NETHERITE_SWORD)) {
            return rollDice(12);
        } else {
            return rollDice(4); // Default sin armas o no reconocida
        }
    }

    private int rollDice(int sides) {
        return Random.create().nextInt(sides) + 1;
    }
}
