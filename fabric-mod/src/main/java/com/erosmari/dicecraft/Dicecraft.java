package com.erosmari.dicecraft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
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
    private static final int NATURAL_ONE = 1;
    private static final int NATURAL_TWENTY = 20;
    private static final Logger LOGGER = Logger.getLogger(Dicecraft.class.getName());

    @Override
    public void onInitialize() {
        DiceConfigHandler.loadConfig();
        LOGGER.info("RollD20 Mod initialized with custom configuration!");

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
        int baseRoll = rollD20();
        int attackRoll = baseRoll;

        if (baseRoll != NATURAL_ONE && baseRoll != NATURAL_TWENTY) {
            attackRoll += getWeaponBonus(attacker.getStackInHand(Hand.MAIN_HAND)) + DiceConfigHandler.getConfig().playerBonus;
        }

        int targetArmorValue = target.getArmor();

        LOGGER.info("Attack Roll: " + attackRoll);
        LOGGER.info("Target Armor: " + targetArmorValue);

        if (baseRoll == NATURAL_ONE) {
            LOGGER.info("Natural 1! Attack automatically misses.");
            spawnAngryParticles(attacker);
            return false; // Falla automática
        }

        if (attackRoll >= targetArmorValue) {
            handleCriticalHit(baseRoll, attacker);
            performDamageRoll(attacker, target, attacker.getStackInHand(Hand.MAIN_HAND), baseRoll == NATURAL_TWENTY);
            return true; // Hit successful
        } else {
            LOGGER.info("Attack missed!");
            return false; // Hit missed
        }
    }

    public boolean performMobAttack(LivingEntity attacker, PlayerEntity target) {
        int baseRoll = rollD20();
        int attackRoll = baseRoll;

        if (baseRoll != NATURAL_ONE && baseRoll != NATURAL_TWENTY) {
            attackRoll += DiceConfigHandler.getConfig().mobBonus;
        }

        int targetArmorValue = target.getArmor();
        int damage = getMobDamage(attacker);

        LOGGER.info("Mob Attack Roll: " + attackRoll);
        LOGGER.info("Player Armor: " + targetArmorValue);
        LOGGER.info("Mob Damage: " + damage);

        if (baseRoll == NATURAL_ONE) {
            LOGGER.info("Natural 1! Mob attack automatically misses.");
            spawnAngryParticles(attacker);
            return false; // Falla automática
        }

        if (attackRoll >= targetArmorValue) {
            handleCriticalHit(baseRoll, target);
            performDamageRoll(attacker, target, attacker.getMainHandStack(), baseRoll == NATURAL_TWENTY);
            return true; // Hit successful
        } else {
            LOGGER.info("Mob Attack missed!");
            return false; // Hit missed
        }
    }

    private void handleCriticalHit(int baseRoll, LivingEntity entity) {
        if (baseRoll == NATURAL_TWENTY) {
            spawnTotemAnimation(entity);
        }
    }

    private int rollD20() {
        return Random.create().nextInt(DiceConfigHandler.getConfig().d20) + 1;
    }

    public static int getMobDamage(LivingEntity mob) {
        DiceConfigHandler.DiceConfig config = DiceConfigHandler.getConfig();

        if (mob instanceof ZombieEntity) return rollDice(config.zombieDamage);
        if (mob instanceof SkeletonEntity) return rollDice(config.skeletonDamage);
        if (mob instanceof CreeperEntity) return rollDice(config.creeperDamage);
        if (mob instanceof SpiderEntity) return rollDice(config.spiderDamage);
        if (mob instanceof CaveSpiderEntity) return rollDice(config.caveSpiderDamage);
        if (mob instanceof EndermanEntity) return rollDice(config.endermanDamage);
        if (mob instanceof SlimeEntity slime) {
            return switch (slime.getSize()) {
                case 1 -> config.slimeSmallDamage;
                case 2 -> rollDice(config.slimeMediumDamage);
                default -> rollDice(config.slimeLargeDamage);
            };
        }
        if (mob instanceof MagmaCubeEntity magma) {
            return switch (magma.getSize()) {
                case 1 -> config.magmaCubeSmallDamage;
                case 2 -> rollDice(config.magmaCubeMediumDamage);
                default -> rollDice(config.magmaCubeLargeDamage);
            };
        }
        if (mob instanceof BlazeEntity) return rollDice(config.blazeDamage);
        if (mob instanceof WitherSkeletonEntity) return rollDice(config.witherSkeletonDamage);
        if (mob instanceof PiglinEntity) return rollDice(config.piglinDamage);
        if (mob instanceof PiglinBruteEntity) return rollDice(config.piglinBruteDamage);
        if (mob instanceof HoglinEntity) return rollDice(config.hoglinDamage);
        if (mob instanceof ZoglinEntity) return rollDice(config.zoglinDamage);
        if (mob instanceof VindicatorEntity) return rollDice(config.vindicatorDamage);
        if (mob instanceof EvokerEntity) return rollDice(config.evokerDamage);
        if (mob instanceof RavagerEntity) return rollDice(config.ravagerDamage);
        if (mob instanceof WardenEntity) return rollDice(config.wardenMeleeDamage);
        if (mob instanceof GuardianEntity) return rollDice(config.guardianDamage);
        if (mob instanceof ElderGuardianEntity) return rollDice(config.elderGuardianDamage);
        if (mob instanceof PhantomEntity) return rollDice(config.phantomDamage);
        if (mob instanceof SilverfishEntity) return rollDice(config.silverfishDamage);
        if (mob instanceof DrownedEntity) return rollDice(config.drownedDamage);
        if (mob instanceof ShulkerEntity) return rollDice(config.shulkerDamage);
        if (mob instanceof PillagerEntity) return rollDice(config.pillagerDamage);
        if (mob instanceof IllusionerEntity) return rollDice(config.illusionerDamage);
        return 0;
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
            LOGGER.info("Natural 20! Critical hit, rolling damage twice.");
            int criticalDamage = getDamageBasedOnWeapon(weapon); // Segunda tirada de daño
            damageRoll += criticalDamage; // Sumar ambos daños
            LOGGER.info("Critical Damage Roll: " + criticalDamage);
        }

        LOGGER.info("Damage Roll: " + damageRoll);

        if (attacker.getWorld() instanceof ServerWorld serverWorld) {
            if (weapon.isOf(Items.BOW) || weapon.isOf(Items.CROSSBOW)) {
                LOGGER.info((weapon.isOf(Items.BOW) ? "Bow" : "Crossbow") + " used for attack. Applying damage to target.");
                target.damage(serverWorld, attacker.getDamageSources().indirectMagic(attacker, attacker), (float) damageRoll);
                LOGGER.info("Target hit by " + (weapon.isOf(Items.BOW) ? "arrow" : "bolt") + ". Final Damage Applied: " + damageRoll);
            } else if (attacker instanceof PlayerEntity player) {
                target.damage(serverWorld, player.getDamageSources().playerAttack(player), (float) damageRoll);
            } else {
                target.damage(serverWorld, attacker.getDamageSources().mobAttack(attacker), (float) damageRoll);
            }
        }
    }

    private void spawnTotemAnimation(LivingEntity entity) {
        if (entity.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.TOTEM_OF_UNDYING, entity.getX(), entity.getY() + 1, entity.getZ(), 50, 0.5, 1.0, 0.5, 0.1);
        }
    }

    private void spawnAngryParticles(LivingEntity entity) {
        if (entity.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.ANGRY_VILLAGER, entity.getX(), entity.getY() + 1, entity.getZ(), 20, 0.5, 1.0, 0.5, 0.1);
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

    private static int rollDice(int sides) {
        return sides > 0 ? Random.create().nextInt(sides) + 1 : 0;
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

        // Mob-specific damage dice
        public int zombieDamage = 4;
        public int skeletonDamage = 6;
        public int creeperDamage = 20;
        public int spiderDamage = 4;
        public int caveSpiderDamage = 4;
        public int endermanDamage = 6;
        public int slimeLargeDamage = 4;
        public int slimeMediumDamage = 2;
        public int slimeSmallDamage = 0;
        public int magmaCubeLargeDamage = 4;
        public int magmaCubeMediumDamage = 2;
        public int magmaCubeSmallDamage = 0;
        public int blazeDamage = 4;
        public int witherSkeletonDamage = 8;
        public int piglinDamage = 6;
        public int piglinBruteDamage = 12;
        public int hoglinDamage = 8;
        public int zoglinDamage = 8;
        public int vindicatorDamage = 12;
        public int evokerDamage = 8;
        public int ravagerDamage = 20;
        public int wardenMeleeDamage = 30;
        public int guardianDamage = 4;
        public int elderGuardianDamage = 6;
        public int phantomDamage = 4;
        public int silverfishDamage = 2;
        public int drownedDamage = 4;
        public int shulkerDamage = 4;
        public int pillagerDamage = 6;
        public int illusionerDamage = 6;
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
