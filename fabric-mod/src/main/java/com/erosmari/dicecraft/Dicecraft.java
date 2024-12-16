package com.erosmari.dicecraft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

public class Dicecraft implements ModInitializer {
    @Override
    public void onInitialize() {
        DiceConfigHandler.loadConfig();
        System.out.println("RollD20 Mod initialized with custom configuration!");

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
        int attackRoll = rollD20() + getWeaponBonus(attacker.getStackInHand(Hand.MAIN_HAND)) + DiceConfigHandler.getConfig().playerBonus;
        int targetArmorValue = target.getArmor();

        System.out.println("Attack Roll (with bonus): " + attackRoll);
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
        int attackRoll = rollD20() + DiceConfigHandler.getConfig().mobBonus;
        int targetArmorValue = target.getArmor();

        System.out.println("Mob Attack Roll (with bonus): " + attackRoll);
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
        return Random.create().nextInt(DiceConfigHandler.getConfig().d20) + 1;
    }

    private int getWeaponBonus(ItemStack weapon) {
        DiceConfigHandler.DiceConfig config = DiceConfigHandler.getConfig();
        if (weapon.isOf(Items.WOODEN_SWORD)) {
            return config.woodenSwordBonus;
        } else if (weapon.isOf(Items.STONE_SWORD)) {
            return config.stoneSwordBonus;
        } else if (weapon.isOf(Items.IRON_SWORD)) {
            return config.ironSwordBonus;
        } else if (weapon.isOf(Items.GOLDEN_SWORD)) {
            return config.goldenSwordBonus;
        } else if (weapon.isOf(Items.DIAMOND_SWORD)) {
            return config.diamondSwordBonus;
        } else if (weapon.isOf(Items.NETHERITE_SWORD)) {
            return config.netheriteSwordBonus;
        } else if (weapon.isOf(Items.TRIDENT)) {
            return config.tridentBonus;
        } else if (weapon.isOf(Items.BOW) || weapon.isOf(Items.CROSSBOW)) {
            return config.bowBonus;
        } else {
            return 0;
        }
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
            if (weapon.isOf(Items.BOW) || weapon.isOf(Items.CROSSBOW)) {
                System.out.println((weapon.isOf(Items.BOW) ? "Bow" : "Crossbow") + " used for attack. Applying damage to target.");
                target.damage(serverWorld, attacker.getDamageSources().indirectMagic(attacker, attacker), (float) damageRoll);
                System.out.println("Target hit by " + (weapon.isOf(Items.BOW) ? "arrow" : "bolt") + ". Final Damage Applied: " + damageRoll);
            } else if (attacker instanceof PlayerEntity player) {
                target.damage(serverWorld, player.getDamageSources().playerAttack(player), (float) damageRoll);
            } else {
                target.damage(serverWorld, attacker.getDamageSources().mobAttack(attacker), (float) damageRoll);
            }
        }
    }

    private int getDamageBasedOnWeapon(ItemStack weapon) {
        DiceConfigHandler.DiceConfig config = DiceConfigHandler.getConfig();
        if (weapon.isEmpty()) {
            return rollDice(config.d4); // Ataque sin armas (básico d4)
        } else if (weapon.isOf(Items.WOODEN_SWORD)) {
            return rollDice(config.d4);
        } else if (weapon.isOf(Items.STONE_SWORD)) {
            return rollDice(config.d6);
        } else if (weapon.isOf(Items.IRON_SWORD)) {
            return rollDice(config.d8);
        } else if (weapon.isOf(Items.DIAMOND_SWORD)) {
            return rollDice(config.d10);
        } else if (weapon.isOf(Items.NETHERITE_SWORD)) {
            return rollDice(config.d12);
        } else if (weapon.isOf(Items.BOW)) {
            return rollDice(config.d6); // El arco hace d6 de daño
        } else if (weapon.isOf(Items.CROSSBOW)) {
            return rollDice(config.d8); // La ballesta hace d8 de daño
        } else {
            return rollDice(config.d4); // Default sin armas o no reconocida
        }
    }

    private int rollDice(int sides) {
        return Random.create().nextInt(sides) + 1;
    }
}

class DiceConfigHandler {
    private static final Logger LOGGER = Logger.getLogger(DiceConfigHandler.class.getName());
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File("config/dicecraft.json");
    private static DiceConfig config;

    public static class DiceConfig {
        public int d4 = 4;
        public int d6 = 6;
        public int d8 = 8;
        public int d10 = 10;
        public int d12 = 12;
        public int d20 = 20;
        public int playerBonus = 0;
        public int mobBonus = 0;
        public int woodenSwordBonus = 0;
        public int stoneSwordBonus = 0;
        public int ironSwordBonus = 1;
        public int goldenSwordBonus = 1;
        public int diamondSwordBonus = 2;
        public int netheriteSwordBonus = 3;
        public int tridentBonus = 3;
        public int bowBonus = 0;
    }

    public static void loadConfig() {
        try {
            if (!CONFIG_FILE.getParentFile().exists() && !CONFIG_FILE.getParentFile().mkdirs()) {
                LOGGER.severe("Failed to create configuration directory: " + CONFIG_FILE.getParentFile().getAbsolutePath());
            }

            if (!CONFIG_FILE.exists()) {
                saveDefaultConfig();
            }
            config = GSON.fromJson(new FileReader(CONFIG_FILE), DiceConfig.class);
        } catch (IOException e) {
            LOGGER.severe("Error loading configuration file: " + e.getMessage());
            saveDefaultConfig();
        }
    }

    public static void saveDefaultConfig() {
        config = new DiceConfig();
        saveConfig();
    }

    public static void saveConfig() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            LOGGER.severe("Error saving configuration file: " + e.getMessage());
        }
    }

    public static DiceConfig getConfig() {
        return config;
    }
}
